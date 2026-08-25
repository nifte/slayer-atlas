package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
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
}
