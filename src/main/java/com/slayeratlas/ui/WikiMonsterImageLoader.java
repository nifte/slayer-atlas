package com.slayeratlas.ui;

import com.slayeratlas.data.SlayerMonster;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
@Singleton
public class WikiMonsterImageLoader implements MonsterImageLoader
{
	private static final Object DECODE_LOCK = new Object();

	private final OkHttpClient httpClient;
	private final WikiImageCache cache;
	private final ScheduledExecutorService retries = Executors.newSingleThreadScheduledExecutor(runnable ->
	{
		Thread thread = new Thread(runnable, "slayer-atlas-wiki-images");
		thread.setDaemon(true);
		return thread;
	});
	private final Map<String, BufferedImage> sources = new HashMap<>();
	private final Map<String, List<WikiImageWaiter>> waiters = new HashMap<>();
	private final Deque<WikiImageDownload> urgentQueue = new ArrayDeque<>();
	private final Deque<WikiImageDownload> backgroundQueue = new ArrayDeque<>();
	private final Set<String> queuedOrActive = new HashSet<>();
	private final Set<String> refreshAttempted = new HashSet<>();
	private final Object lock = new Object();
	private int inFlight;

	@Inject
	public WikiMonsterImageLoader(OkHttpClient httpClient, WikiImageCache cache)
	{
		this.httpClient = httpClient.newBuilder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.build();
		this.cache = cache == null ? new WikiImageCache() : cache;
	}

	@Override
	public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded)
	{
		load(monster, size, onLoaded, true);
	}

	@Override
	public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded, boolean urgent)
	{
		if (monster == null)
		{
			return;
		}
		request(WikiImageUrl.fileName(monster), size, onLoaded, urgent);
	}

	@Override
	public void loadFile(String fileName, int size, Consumer<BufferedImage> onLoaded)
	{
		request(fileName, size, onLoaded, true);
	}

	@Override
	public void prefetch(Iterable<SlayerMonster> monsters)
	{
		if (monsters == null)
		{
			return;
		}
		synchronized (lock)
		{
			for (SlayerMonster monster : monsters)
			{
				if (monster == null)
				{
					continue;
				}
				queueIfNeeded(WikiImageUrl.fileName(monster), false);
			}
			pumpLocked();
		}
	}

	private void request(String fileName, int size, Consumer<BufferedImage> onLoaded, boolean urgent)
	{
		if (fileName == null || fileName.isEmpty() || size <= 0 || onLoaded == null)
		{
			return;
		}
		BufferedImage source;
		synchronized (lock)
		{
			source = sources.get(fileName);
			if (source == null)
			{
				waiters.computeIfAbsent(fileName, key -> new ArrayList<>())
					.add(new WikiImageWaiter(size, onLoaded));
				if (queuedOrActive.contains(fileName))
				{
					if (urgent)
					{
						promoteToUrgent(fileName);
					}
					return;
				}
				if (cache.contains(fileName))
				{
					queuedOrActive.add(fileName);
					retries.execute(() -> completeFromDisk(fileName));
					return;
				}
				queueIfNeeded(fileName, urgent);
				pumpLocked();
				return;
			}
		}
		deliver(onLoaded, MonsterImageScaler.fitSquare(source, size));
		queueRefreshIfStale(fileName);
	}

	private void queueIfNeeded(String fileName, boolean urgent)
	{
		if (fileName == null || fileName.isEmpty() || sources.containsKey(fileName))
		{
			return;
		}
		if (cache.contains(fileName))
		{
			if (queuedOrActive.add(fileName))
			{
				retries.execute(() -> completeFromDisk(fileName));
			}
			return;
		}
		if (queuedOrActive.add(fileName))
		{
			WikiImageQueue.enqueueNew(urgentQueue, backgroundQueue, new WikiImageDownload(fileName, 1, urgent));
			return;
		}
		if (urgent)
		{
			promoteToUrgent(fileName);
		}
	}

	private void queueRefreshIfStale(String fileName)
	{
		if (fileName == null || fileName.isEmpty() || !cache.stale(fileName))
		{
			return;
		}
		synchronized (lock)
		{
			if (refreshAttempted.contains(fileName) || !queuedOrActive.add(fileName))
			{
				return;
			}
			refreshAttempted.add(fileName);
			WikiImageQueue.enqueueNew(urgentQueue, backgroundQueue, WikiImageDownload.refresh(fileName));
			pumpLocked();
		}
	}

	private void promoteToUrgent(String fileName)
	{
		WikiImageDownload found = null;
		for (WikiImageDownload download : backgroundQueue)
		{
			if (download.fileName().equals(fileName))
			{
				found = download;
				break;
			}
		}
		if (found == null)
		{
			return;
		}
		backgroundQueue.remove(found);
		WikiImageQueue.promote(urgentQueue, found);
	}

	private void pumpLocked()
	{
		while (inFlight < WikiImageFetchPolicy.MAX_CONCURRENT
			&& (!urgentQueue.isEmpty() || !backgroundQueue.isEmpty()))
		{
			WikiImageDownload download = !urgentQueue.isEmpty()
				? urgentQueue.removeFirst()
				: backgroundQueue.removeFirst();
			inFlight++;
			start(download);
		}
	}

	private void start(WikiImageDownload download)
	{
		if (!download.isRefresh())
		{
			BufferedImage cached = decode(cache.load(download.fileName()));
			if (cached != null)
			{
				complete(download.fileName(), cached, true, true);
				queueRefreshIfStale(download.fileName());
				return;
			}
		}
		String url = WikiImageUrl.fromFileName(download.fetchName(), WikiImageFetchPolicy.SOURCE_WIDTH);
		if (url.isEmpty())
		{
			retryOrFinish(download, 404);
			return;
		}
		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", WikiHttp.USER_AGENT)
			.build();
		httpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException error)
			{
				log.debug("Failed to download {}", url, error);
				retryOrFinish(download, 0);
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try (Response ignored = response)
				{
					int status = ignored.code();
					ResponseBody body = ignored.body();
					if (!ignored.isSuccessful() || body == null)
					{
						retryOrFinish(download, status);
						return;
					}
					byte[] bytes = body.bytes();
					BufferedImage decoded = decode(bytes);
					if (decoded == null)
					{
						retryOrFinish(download, status);
						return;
					}
					cache.save(download.fileName(), bytes);
					complete(download.fileName(), decoded, true, true);
				}
				catch (Exception error)
				{
					log.debug("Failed to decode {}", url, error);
					retryOrFinish(download, 0);
				}
			}
		});
	}

	private void retryOrFinish(WikiImageDownload download, int statusCode)
	{
		boolean waiting;
		synchronized (lock)
		{
			List<WikiImageWaiter> pending = waiters.get(download.fileName());
			waiting = pending != null && !pending.isEmpty();
		}
		WikiImageDownload next = WikiImageRetry.next(download, statusCode, waiting);
		if (next != null)
		{
			if (waiting)
			{
				next = next.asUrgent();
			}
			requeue(next, WikiImageRetry.delayMs(download, statusCode));
			return;
		}
		if (download.isRefresh())
		{
			BufferedImage cached = decode(cache.load(download.fileName()));
			if (cached != null)
			{
				complete(download.fileName(), cached, true, true);
				return;
			}
		}
		finish(download.fileName(), null, false);
	}

	private void completeFromDisk(String fileName)
	{
		BufferedImage decoded = decode(cache.load(fileName));
		if (decoded != null)
		{
			complete(fileName, decoded, true, false);
			queueRefreshIfStale(fileName);
			return;
		}
		cache.delete(fileName);
		synchronized (lock)
		{
			queuedOrActive.remove(fileName);
			queueIfNeeded(fileName, true);
			pumpLocked();
		}
	}

	private static BufferedImage decode(byte[] bytes)
	{
		if (bytes == null || bytes.length == 0)
		{
			return null;
		}
		try
		{
			synchronized (DECODE_LOCK)
			{
				return ImageIO.read(new ByteArrayInputStream(bytes));
			}
		}
		catch (IOException ignored)
		{
			return null;
		}
	}

	private void requeue(WikiImageDownload download, int delayMs)
	{
		Runnable enqueue = () ->
		{
			synchronized (lock)
			{
				WikiImageQueue.requeueRetry(urgentQueue, backgroundQueue, download);
				pumpLocked();
			}
		};
		synchronized (lock)
		{
			inFlight--;
			pumpLocked();
		}
		if (delayMs > 0)
		{
			retries.schedule(enqueue, delayMs, TimeUnit.MILLISECONDS);
			return;
		}
		enqueue.run();
	}

	private void finish(String fileName, BufferedImage source, boolean success)
	{
		complete(fileName, source, success, true);
	}

	private void complete(String fileName, BufferedImage source, boolean success, boolean networkSlot)
	{
		List<WikiImageWaiter> pending;
		synchronized (lock)
		{
			if (success && source != null)
			{
				sources.put(fileName, source);
			}
			pending = waiters.remove(fileName);
			queuedOrActive.remove(fileName);
			if (networkSlot)
			{
				inFlight--;
				pumpLocked();
			}
		}
		if (pending == null)
		{
			return;
		}
		for (WikiImageWaiter waiter : pending)
		{
			BufferedImage image = success && source != null
				? MonsterImageScaler.fitSquare(source, waiter.size())
				: null;
			deliver(waiter.onLoaded(), image);
		}
	}

	private static void deliver(Consumer<BufferedImage> onLoaded, BufferedImage image)
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			onLoaded.accept(image);
			return;
		}
		SwingUtilities.invokeLater(() -> onLoaded.accept(image));
	}
}
