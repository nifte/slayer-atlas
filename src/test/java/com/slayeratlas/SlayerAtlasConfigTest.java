package com.slayeratlas;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SlayerAtlasConfigTest
{
	@Test
	public void onlyRecommendsOwnedEquipmentByDefault()
	{
		assertTrue(new SlayerAtlasConfig()
		{
		}.onlyRecommendOwnedEquipment());
	}

	@Test
	public void onlyRecommendsUnlockedPrayersByDefault()
	{
		assertTrue(new SlayerAtlasConfig()
		{
		}.onlyRecommendUnlockedPrayers());
	}
}
