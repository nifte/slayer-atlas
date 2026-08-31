package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnlockedPrayersTest
{
	@Test
	public void unknownHasNoLevelsOrUnlocks()
	{
		UnlockedPrayers unknown = UnlockedPrayers.unknown();
		assertFalse(unknown.known());
		assertEquals(0, unknown.prayerLevel());
		assertFalse(unknown.rigour());
		assertEquals(UnlockedPrayers.unknown(), unknown);
	}

	@Test
	public void knownSnapshotsAreEqualWhenFieldsMatch()
	{
		UnlockedPrayers first = UnlockedPrayers.known(70, 70, true, false, true, false, true);
		UnlockedPrayers second = UnlockedPrayers.known(70, 70, true, false, true, false, true);
		assertTrue(first.known());
		assertEquals(70, first.prayerLevel());
		assertEquals(70, first.defenceLevel());
		assertTrue(first.knightWaves());
		assertFalse(first.rigour());
		assertTrue(first.augury());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, UnlockedPrayers.known(70, 70, true, true, true, false, true));
	}
}
