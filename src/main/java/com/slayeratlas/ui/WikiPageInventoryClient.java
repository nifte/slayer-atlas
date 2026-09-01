package com.slayeratlas.ui;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.WikiInventoryText;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
@Singleton
public class WikiPageInventoryClient implements WikiInventoryClient
{
	private final OkHttpClient httpClient;
	private final Gson gson;
	private final Object lock = new Object();
	private final Map<String, List<GearItem>> cache = new HashMap<>();
	private final Map<String, List<Consumer<List<GearItem>>>> waiting = new HashMap<>();

	@Inject
	public WikiPageInventoryClient(OkHttpClient httpClient, Gson gson)
	{
		this.httpClient = httpClient.newBuilder()
			.connectTimeout(8, TimeUnit.SECONDS)
			.readTimeout(20, TimeUnit.SECONDS)
			.writeTimeout(8, TimeUnit.SECONDS)
			.build();
		this.gson = gson;
	}

	@Override
	public void load(String pageName, Consumer<List<GearItem>> onLoaded)
	{
		if (onLoaded == null)
		{
			return;
		}
		if (pageName == null || pageName.isEmpty())
		{
			deliver(onLoaded, List.of());
			return;
		}
		List<GearItem> cached;
		synchronized (lock)
		{
			cached = cache.get(pageName);
			if (cached == null)
			{
				List<Consumer<List<GearItem>>> pending = waiting.computeIfAbsent(pageName, key -> new ArrayList<>());
				pending.add(onLoaded);
				if (pending.size() == 1)
				{
					fetch(pageName);
				}
				return;
			}
		}
		deliver(onLoaded, cached);
	}

	private void fetch(String pageName)
	{
		HttpUrl url = HttpUrl.parse("https://oldschool.runescape.wiki/api.php").newBuilder()
			.addQueryParameter("action", "parse")
			.addQueryParameter("format", "json")
			.addQueryParameter("prop", "wikitext")
			.addQueryParameter("page", pageName)
			.addQueryParameter("redirects", "1")
			.build();
		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", WikiHttp.USER_AGENT)
			.build();
		httpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException error)
			{
				log.debug("Failed to download wiki inventory for {}", pageName, error);
				finish(pageName, List.of());
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try (Response ignored = response)
				{
					ResponseBody body = ignored.body();
					if (!ignored.isSuccessful() || body == null)
					{
						finish(pageName, List.of());
						return;
					}
					ParseResponse parsed = gson.fromJson(body.charStream(), ParseResponse.class);
					String wikitext = wikitext(parsed);
					finish(pageName, WikiInventoryText.parse(wikitext));
				}
				catch (Exception error)
				{
					log.debug("Failed to parse wiki inventory for {}", pageName, error);
					finish(pageName, List.of());
				}
			}
		});
	}

	private void finish(String pageName, List<GearItem> items)
	{
		List<Consumer<List<GearItem>>> pending;
		synchronized (lock)
		{
			cache.put(pageName, items);
			pending = waiting.remove(pageName);
		}
		if (pending == null)
		{
			return;
		}
		for (Consumer<List<GearItem>> consumer : pending)
		{
			deliver(consumer, items);
		}
	}

	private static String wikitext(ParseResponse parsed)
	{
		if (parsed == null || parsed.parse == null || parsed.parse.wikitext == null)
		{
			return "";
		}
		return parsed.parse.wikitext.text == null ? "" : parsed.parse.wikitext.text;
	}

	private static void deliver(Consumer<List<GearItem>> onLoaded, List<GearItem> items)
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			onLoaded.accept(items);
			return;
		}
		SwingUtilities.invokeLater(() -> onLoaded.accept(items));
	}

	private static final class ParseResponse
	{
		private Parse parse;
	}

	private static final class Parse
	{
		private Wikitext wikitext;
	}

	private static final class Wikitext
	{
		@SerializedName("*")
		private String text;
	}
}
