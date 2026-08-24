package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayerguide.data.SlayerMonster;
import org.junit.Test;

public class DpsCalculatorUrlTest
{
	@Test
	public void acceptsWikiMonsterLinks()
	{
		assertEquals(
			"https://tools.runescape.wiki/osrs-dps/?monster=104",
			DpsCalculatorUrl.sanitize("https://tools.runescape.wiki/osrs-dps/?monster=104"));
	}

	@Test
	public void rejectsAnythingElse()
	{
		assertEquals("", DpsCalculatorUrl.sanitize(null));
		assertEquals("", DpsCalculatorUrl.sanitize(""));
		assertEquals("", DpsCalculatorUrl.sanitize("https://example.com/?monster=104"));
		assertEquals("", DpsCalculatorUrl.sanitize("https://tools.runescape.wiki/osrs-dps/?monster=104&foo=1"));
		assertEquals("", DpsCalculatorUrl.fromMonster(null));
	}

	@Test
	public void readsCatalogLinks()
	{
		SlayerMonster hellhounds = new Gson().fromJson(
			"{\"name\":\"Hellhounds\",\"dps\":\"https://tools.runescape.wiki/osrs-dps/?monster=104\"}",
			SlayerMonster.class);
		assertEquals("https://tools.runescape.wiki/osrs-dps/?monster=104", DpsCalculatorUrl.fromMonster(hellhounds));
	}
}
