package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;

public class QuickPrayersTest
{
	@Test
	public void decodesProtectFromMeleeAndPiety()
	{
		int bits = 1 << 18 | 1 << 26;
		assertEquals(
			List.of("Protect from Melee", "Piety"),
			QuickPrayers.fromBits(bits, false, false));
	}

	@Test
	public void usesDeadeyeAndMysticVigourWhenUnlocked()
	{
		int bits = 1 << 19 | 1 << 20;
		assertEquals(List.of("Eagle Eye", "Mystic Might"), QuickPrayers.fromBits(bits, false, false));
		assertEquals(List.of("Deadeye", "Mystic Vigour"), QuickPrayers.fromBits(bits, true, true));
	}

	@Test
	public void returnsNoPrayersWhenNothingIsSelected()
	{
		assertEquals(List.of(), QuickPrayers.fromBits(0, true, true));
	}
}
