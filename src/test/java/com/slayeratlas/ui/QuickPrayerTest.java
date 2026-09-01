package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class QuickPrayerTest
{
	@Test
	public void findsPrayersByDisplayName()
	{
		assertEquals(QuickPrayer.PIETY, QuickPrayer.named("Piety"));
		assertEquals(QuickPrayer.PROTECT_FROM_MELEE, QuickPrayer.named("Protect from Melee"));
		assertEquals(QuickPrayer.DEADEYE, QuickPrayer.named("Deadeye"));
		assertNull(QuickPrayer.named(""));
		assertNull(QuickPrayer.named(null));
		assertNull(QuickPrayer.named("Unknown Prayer"));
	}
}
