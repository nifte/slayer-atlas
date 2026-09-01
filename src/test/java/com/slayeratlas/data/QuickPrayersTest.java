package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;

public class QuickPrayersTest
{
	@Test
	public void decodesProtectFromMagicAndAuguryFromTheQuickPrayerBitmap()
	{
		int bits = 1 << 12 | 1 << 27;
		assertEquals(
			List.of("Protect from Magic", "Augury"),
			QuickPrayers.fromBits(bits, false, false));
	}

	@Test
	public void decodesProtectFromMeleeAndPiety()
	{
		int bits = 1 << 14 | 1 << 25;
		assertEquals(
			List.of("Protect from Melee", "Piety"),
			QuickPrayers.fromBits(bits, false, false));
	}

	@Test
	public void usesDeadeyeAndMysticVigourWhenUnlocked()
	{
		int bits = 1 << 22 | 1 << 23;
		assertEquals(List.of("Eagle Eye", "Mystic Might"), QuickPrayers.fromBits(bits, false, false));
		assertEquals(List.of("Deadeye", "Mystic Vigour"), QuickPrayers.fromBits(bits, true, true));
	}

	@Test
	public void mapsBitsByPrayerIdWhenTheBookIsReordered()
	{
		int bits = 1 << 12 | 1 << 27;
		int[] ids = {27, 12, 0};
		String[] names = {"Augury", "Protect from Magic", "Thick Skin"};
		assertEquals(
			List.of("Protect from Magic", "Augury"),
			QuickPrayers.fromIds(bits, ids, names));
	}

	@Test
	public void mapsBitsByPrayerIdWhenPrayersAreHidden()
	{
		int bits = 1 << 12 | 1 << 27;
		int[] ids = {12, 27};
		String[] names = {"Protect from Magic", "Augury"};
		assertEquals(
			List.of("Protect from Magic", "Augury"),
			QuickPrayers.fromIds(bits, ids, names));
		assertEquals(
			List.of(),
			QuickPrayers.fromIds(bits, new int[] {0, 1}, names));
	}

	@Test
	public void returnsNoPrayersWhenNothingIsSelected()
	{
		assertEquals(List.of(), QuickPrayers.fromBits(0, true, true));
	}
}
