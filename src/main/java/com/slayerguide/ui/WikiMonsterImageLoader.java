package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
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
	private static final String USER_AGENT = "SlayerAtlasRuneLitePlugin (https://github.com/nifte/slayer-atlas)";

	private final OkHttpClient httpClient;
	private final Map<String, BufferedImage> sources = new HashMap<>();
	private final Map<String, List<WikiImageWaiter>> waiters = new HashMap<>();
	private final Deque<WikiImageDownload> queue = new ArrayDeque<>();
	private final Set<String> queuedOrActive = new HashSet<>();
	private final Object lock = new Object();
	private int inFlight;

	@Inject
	public WikiMonsterImageLoader(OkHttpClient httpClient)
	{
		this.httpClient = httpClient;
	}

	@Override
	public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded)
	{
		if (monster == null || size <= 0 || onLoaded == null)
		{
			return;
		}
		String fileName = WikiImageUrl.fileName(monster);
		if (fileName.isEmpty())
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
				if (queuedOrActive.add(fileName))
				{
					queue.addLast(new WikiImageDownload(fileName, 1));
				}
				pumpLocked();
				return;
			}
		}
		deliver(onLoaded, MonsterImageScaler.fitSquare(source, size));
	}

	private void pumpLocked()
	{
		while (inFlight < WikiImageFetchPolicy.MAX_CONCURRENT && !queue.isEmpty())
		{
			WikiImageDownload download = queue.removeFirst();
			inFlight++;
			start(download);
		}
	}

	private void start(WikiImageDownload download)
	{
		String url = WikiImageUrl.fromFileName(download.fileName(), WikiImageFetchPolicy.SOURCE_WIDTH);
		if (url.isEmpty())
		{
			finish(download.fileName(), null, false);
			return;
		}
		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", USER_AGENT)
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
					BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(bytes));
					if (decoded == null)
					{
						retryOrFinish(download, status);
						return;
					}
					finish(download.fileName(), decoded, true);
				}
				catch (IOException error)
				{
					log.debug("Failed to decode {}", url, error);
					retryOrFinish(download, 0);
				}
			}
		});
	}

	private void retryOrFinish(WikiImageDownload download, int statusCode)
	{
		if (WikiImageFetchPolicy.shouldRetry(statusCode, download.attempt()))
		{
			synchronized (lock)
			{
				inFlight--;
				queue.addFirst(download.nextAttempt());
				pumpLocked();
			}
			return;
		}
		finish(download.fileName(), null, false);
	}

	private void finish(String fileName, BufferedImage source, boolean success)
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
			inFlight--;
			pumpLocked();
		}
		if (!success || source == null || pending == null)
		{
			return;
		}
		for (WikiImageWaiter waiter : pending)
		{
			deliver(waiter.onLoaded(), MonsterImageScaler.fitSquare(source, waiter.size()));
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
