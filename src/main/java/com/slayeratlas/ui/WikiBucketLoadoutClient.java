package com.slayeratlas.ui;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.WikiEquipmentRow;
import com.slayeratlas.data.WikiLoadoutMatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
public class WikiBucketLoadoutClient implements WikiLoadoutClient
{
	private static final String USER_AGENT = "SlayerAtlasRuneLitePlugin (https://github.com/nifte/slayer-atlas)";
	private static final String QUERY =
		"bucket('recommended_equipment').select('page_name','json').limit(5000).run()";

	private final OkHttpClient httpClient;
	private final Gson gson;
	private final Object lock = new Object();
	private List<WikiEquipmentRow> cache;
	private final List<Pending> waiting = new ArrayList<>();
	private boolean loading;

	@Inject
	public WikiBucketLoadoutClient(OkHttpClient httpClient, Gson gson)
	{
		this.httpClient = httpClient.newBuilder()
			.connectTimeout(8, TimeUnit.SECONDS)
			.readTimeout(20, TimeUnit.SECONDS)
			.writeTimeout(8, TimeUnit.SECONDS)
			.build();
		this.gson = gson;
	}

	@Override
	public void load(SlayerMonster monster, Consumer<List<GearLoadout>> onLoaded)
	{
		if (onLoaded == null)
		{
			return;
		}
		List<WikiEquipmentRow> rows;
		synchronized (lock)
		{
			if (cache != null)
			{
				rows = cache;
			}
			else
			{
				waiting.add(new Pending(monster, onLoaded));
				if (!loading)
				{
					loading = true;
					fetch();
				}
				return;
			}
		}
		deliver(onLoaded, WikiLoadoutMatcher.match(gson, monster, rows));
	}

	private void fetch()
	{
		HttpUrl url = HttpUrl.parse("https://oldschool.runescape.wiki/api.php").newBuilder()
			.addQueryParameter("action", "bucket")
			.addQueryParameter("format", "json")
			.addQueryParameter("query", QUERY)
			.build();
		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", USER_AGENT)
			.build();
		httpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException error)
			{
				log.debug("Failed to download recommended equipment", error);
				finish(List.of());
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try (Response ignored = response)
				{
					ResponseBody body = ignored.body();
					if (!ignored.isSuccessful() || body == null)
					{
						finish(List.of());
						return;
					}
					BucketResponse parsed = gson.fromJson(body.charStream(), BucketResponse.class);
					finish(rows(parsed));
				}
				catch (Exception error)
				{
					log.debug("Failed to parse recommended equipment", error);
					finish(List.of());
				}
			}
		});
	}

	private void finish(List<WikiEquipmentRow> rows)
	{
		List<Pending> pending;
		synchronized (lock)
		{
			cache = rows;
			loading = false;
			pending = new ArrayList<>(waiting);
			waiting.clear();
		}
		for (Pending request : pending)
		{
			deliver(request.onLoaded, WikiLoadoutMatcher.match(gson, request.monster, rows));
		}
	}

	private static List<WikiEquipmentRow> rows(BucketResponse parsed)
	{
		List<WikiEquipmentRow> rows = new ArrayList<>();
		if (parsed == null || parsed.bucket == null)
		{
			return rows;
		}
		for (BucketRow row : parsed.bucket)
		{
			if (row != null && row.pageName != null && row.json != null)
			{
				rows.add(new WikiEquipmentRow(row.pageName, row.json));
			}
		}
		return rows;
	}

	private static void deliver(Consumer<List<GearLoadout>> onLoaded, List<GearLoadout> loadouts)
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			onLoaded.accept(loadouts);
			return;
		}
		SwingUtilities.invokeLater(() -> onLoaded.accept(loadouts));
	}

	private static final class Pending
	{
		private final SlayerMonster monster;
		private final Consumer<List<GearLoadout>> onLoaded;

		private Pending(SlayerMonster monster, Consumer<List<GearLoadout>> onLoaded)
		{
			this.monster = monster;
			this.onLoaded = onLoaded;
		}
	}

	private static final class BucketResponse
	{
		private List<BucketRow> bucket;
	}

	private static final class BucketRow
	{
		@SerializedName("page_name")
		private String pageName;
		private String json;
	}
}
