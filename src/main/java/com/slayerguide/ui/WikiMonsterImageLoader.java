package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
	private final Map<String, BufferedImage> cache = new ConcurrentHashMap<>();

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
		String cacheKey = fileName + "#" + size;
		BufferedImage cached = cache.get(cacheKey);
		if (cached != null)
		{
			deliver(onLoaded, cached);
			return;
		}
		String url = WikiImageUrl.fromFileName(fileName);
		if (url.isEmpty())
		{
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
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try (Response ignored = response)
				{
					ResponseBody body = ignored.body();
					if (!ignored.isSuccessful() || body == null)
					{
						return;
					}
					BufferedImage decoded;
					try (InputStream stream = body.byteStream())
					{
						decoded = ImageIO.read(stream);
					}
					if (decoded == null)
					{
						return;
					}
					BufferedImage fitted = MonsterImageScaler.fitSquare(decoded, size);
					cache.put(cacheKey, fitted);
					deliver(onLoaded, fitted);
				}
				catch (IOException error)
				{
					log.debug("Failed to decode {}", url, error);
				}
			}
		});
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
