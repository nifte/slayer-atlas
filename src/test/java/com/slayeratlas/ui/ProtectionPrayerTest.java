package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.slayeratlas.data.UnlockedPrayers;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class ProtectionPrayerTest
{
	@Test
	public void parsesSingleOverheadPrayers()
	{
		assertEquals(
			Collections.singletonList(ProtectionPrayer.MELEE),
			ProtectionPrayer.parse("Protect from Melee"));
		assertEquals(
			Collections.singletonList(ProtectionPrayer.MAGIC),
			ProtectionPrayer.parse("Protect from Magic"));
		assertEquals(
			Collections.singletonList(ProtectionPrayer.MISSILES),
			ProtectionPrayer.parse("Protect from Missiles"));
	}

	@Test
	public void parsesAlternatePrayersInMentionOrder()
	{
		assertEquals(
			Arrays.asList(ProtectionPrayer.MISSILES, ProtectionPrayer.MAGIC),
			ProtectionPrayer.parse("Protect from Missiles or Magic (they alternate)"));
	}

	@Test
	public void emptyWhenNoneNeededOrMissing()
	{
		assertEquals(Collections.emptyList(), ProtectionPrayer.parse("None needed"));
		assertEquals(Collections.emptyList(), ProtectionPrayer.parse(""));
		assertEquals(Collections.emptyList(), ProtectionPrayer.parse(null));
	}

	@Test
	public void keepsOverheadsWhenUnlockedFilteringIsOffOrUnknown()
	{
		List<ProtectionPrayer> melee = Collections.singletonList(ProtectionPrayer.MELEE);
		assertEquals(melee, ProtectionPrayer.recommended(melee, false, UnlockedPrayers.known(1, 1, false, false, false, false, false)));
		assertEquals(melee, ProtectionPrayer.recommended(melee, true, UnlockedPrayers.unknown()));
	}

	@Test
	public void keepsUnlockedOverheadsAndFallsBackToDefenceWhenNoneAreUnlocked()
	{
		List<ProtectionPrayer> all = Arrays.asList(
			ProtectionPrayer.MAGIC,
			ProtectionPrayer.MISSILES,
			ProtectionPrayer.MELEE);
		assertEquals(
			Arrays.asList(ProtectionPrayer.MAGIC, ProtectionPrayer.MISSILES),
			ProtectionPrayer.recommended(all, true, UnlockedPrayers.known(40, 1, false, false, false, false, false)));
		assertEquals(
			Collections.singletonList(ProtectionPrayer.STEEL_SKIN),
			ProtectionPrayer.recommended(all, true, UnlockedPrayers.known(30, 1, false, false, false, false, false)));
		assertEquals(
			Collections.singletonList(ProtectionPrayer.ROCK_SKIN),
			ProtectionPrayer.recommended(all, true, UnlockedPrayers.known(10, 1, false, false, false, false, false)));
		assertEquals(
			Collections.singletonList(ProtectionPrayer.THICK_SKIN),
			ProtectionPrayer.recommended(all, true, UnlockedPrayers.known(1, 1, false, false, false, false, false)));
		assertEquals(
			all,
			ProtectionPrayer.recommended(all, true, UnlockedPrayers.known(43, 1, false, false, false, false, false)));
	}
}
