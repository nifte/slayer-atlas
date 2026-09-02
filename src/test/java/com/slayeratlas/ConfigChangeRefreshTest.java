package com.slayeratlas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.data.ConfigFavoriteTasks;
import org.junit.Test;

public class ConfigChangeRefreshTest
{
	@Test
	public void favoritesOnlyWhenThatKeyChanges()
	{
		assertTrue(ConfigChangeRefresh.favorites(null));
		assertTrue(ConfigChangeRefresh.favorites(ConfigFavoriteTasks.KEY));
		assertFalse(ConfigChangeRefresh.favorites(SlayerAtlasConfig.KEY_SHOW_BANK_TAB_BUTTON));
	}

	@Test
	public void pathButtonsOnlyWhenShortestPathChanges()
	{
		assertTrue(ConfigChangeRefresh.pathButtons(null));
		assertTrue(ConfigChangeRefresh.pathButtons(SlayerAtlasConfig.KEY_SHORTEST_PATH_ENABLED));
		assertFalse(ConfigChangeRefresh.pathButtons(SlayerAtlasConfig.KEY_AUTO_PATH_ON_NEW_TASK));
		assertFalse(ConfigChangeRefresh.pathButtons(SlayerAtlasConfig.KEY_SHOW_BANK_TAB_BUTTON));
	}

	@Test
	public void gearOnlyWhenRecommendationSettingsChange()
	{
		assertTrue(ConfigChangeRefresh.gear(null));
		assertTrue(ConfigChangeRefresh.gear(SlayerAtlasConfig.KEY_ONLY_RECOMMEND_OWNED_EQUIPMENT));
		assertTrue(ConfigChangeRefresh.gear(SlayerAtlasConfig.KEY_USE_GOADING_POTIONS));
		assertFalse(ConfigChangeRefresh.gear(SlayerAtlasConfig.KEY_ONLY_RECOMMEND_UNLOCKED_PRAYERS));
		assertFalse(ConfigChangeRefresh.gear(SlayerAtlasConfig.KEY_OPEN_PANEL_ON_TASK));
	}

	@Test
	public void prayersOnlyWhenUnlockedPrayerSettingChanges()
	{
		assertTrue(ConfigChangeRefresh.prayers(null));
		assertTrue(ConfigChangeRefresh.prayers(SlayerAtlasConfig.KEY_ONLY_RECOMMEND_UNLOCKED_PRAYERS));
		assertFalse(ConfigChangeRefresh.prayers(SlayerAtlasConfig.KEY_ONLY_RECOMMEND_OWNED_EQUIPMENT));
		assertFalse(ConfigChangeRefresh.prayers(SlayerAtlasConfig.KEY_PREVENT_TAG_TAB_DRAGS));
	}
}
