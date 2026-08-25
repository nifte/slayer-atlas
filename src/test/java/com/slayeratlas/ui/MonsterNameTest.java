package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MonsterNameTest
{
	@Test
	public void capitalizesEachWord()
	{
		assertEquals("Baby Black Dragon", MonsterName.display("Baby black dragon"));
		assertEquals("Black Dragons", MonsterName.display("Black dragons"));
		assertEquals("Dust Devils", MonsterName.display("Dust devils"));
		assertEquals("Long-Tailed Wyvern", MonsterName.display("Long-tailed wyvern"));
		assertEquals("Donny The Lad", MonsterName.display("Donny the lad"));
		assertEquals(
			"King Black Dragon (Not On Krystilia Tasks)",
			MonsterName.display("King Black Dragon (not on Krystilia tasks)"));
	}

	@Test
	public void keepsSpecialInternalCapitals()
	{
		assertEquals("TzTok-Jad", MonsterName.display("TzTok-Jad"));
		assertEquals("TzKal-Zuk", MonsterName.display("TzKal-Zuk"));
		assertEquals("K'ril Tsutsaroth", MonsterName.display("K'ril Tsutsaroth"));
	}

	@Test
	public void emptyWhenMissing()
	{
		assertEquals("", MonsterName.display(null));
		assertEquals("", MonsterName.display(""));
	}
}
