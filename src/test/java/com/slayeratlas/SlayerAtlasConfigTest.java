package com.slayeratlas;

import static org.junit.Assert.assertFalse;
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

	@Test
	public void showsTheBankTabButtonByDefault()
	{
		assertTrue(new SlayerAtlasConfig()
		{
		}.showBankTabButton());
	}

	@Test
	public void doesNotUseGoadingPotionsByDefault()
	{
		assertFalse(new SlayerAtlasConfig()
		{
		}.useGoadingPotions());
	}

	@Test
	public void preventsTagTabItemDraggingByDefault()
	{
		assertTrue(new SlayerAtlasConfig()
		{
		}.preventTagTabDrags());
	}

	@Test
	public void usesBankTabLayoutsByDefault()
	{
		assertTrue(new SlayerAtlasConfig()
		{
		}.useBankTabLayouts());
	}
}
