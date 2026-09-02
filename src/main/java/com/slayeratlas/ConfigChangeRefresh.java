package com.slayeratlas;

import com.slayeratlas.data.ConfigFavoriteTasks;

public final class ConfigChangeRefresh
{
	private ConfigChangeRefresh()
	{
	}

	public static boolean favorites(String key)
	{
		return key == null || ConfigFavoriteTasks.KEY.equals(key);
	}

	public static boolean pathButtons(String key)
	{
		return key == null || SlayerAtlasConfig.KEY_SHORTEST_PATH_ENABLED.equals(key);
	}

	public static boolean gear(String key)
	{
		return key == null
			|| SlayerAtlasConfig.KEY_ONLY_RECOMMEND_OWNED_EQUIPMENT.equals(key)
			|| SlayerAtlasConfig.KEY_USE_GOADING_POTIONS.equals(key);
	}

	public static boolean prayers(String key)
	{
		return key == null || SlayerAtlasConfig.KEY_ONLY_RECOMMEND_UNLOCKED_PRAYERS.equals(key);
	}
}
