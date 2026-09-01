package com.slayeratlas.data;

import com.slayeratlas.SlayerAtlasConfig;
import java.util.LinkedHashSet;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.config.ConfigManager;

@Singleton
public final class ConfigFavoriteTasks implements FavoriteTasks
{
	public static final String KEY = "favoriteTasks";

	private final ConfigManager configManager;

	@Inject
	public ConfigFavoriteTasks(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	@Override
	public boolean contains(String monsterId)
	{
		return monsterId != null && FavoriteTaskIds.parse(stored()).contains(monsterId);
	}

	@Override
	public void set(String monsterId, boolean favorite)
	{
		if (monsterId == null || monsterId.isEmpty() || configManager == null)
		{
			return;
		}
		LinkedHashSet<String> ids = new LinkedHashSet<>(FavoriteTaskIds.parse(stored()));
		if (favorite)
		{
			ids.add(monsterId);
		}
		else
		{
			ids.remove(monsterId);
		}
		configManager.setConfiguration(SlayerAtlasConfig.GROUP, KEY, FavoriteTaskIds.serialize(ids));
	}

	private String stored()
	{
		return configManager == null ? null : configManager.getConfiguration(SlayerAtlasConfig.GROUP, KEY);
	}
}
