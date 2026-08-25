package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WikiItemUrlTest
{
	@Test
	public void buildsAWikiPageFromTheItemName()
	{
		assertEquals(
			"https://oldschool.runescape.wiki/w/Slayer_helmet_(i)",
			decodeable("Slayer helmet (i)"));
		assertTrue(WikiItemUrl.fromName("Osmumten's fang").contains("Osmumten"));
		assertTrue(WikiItemUrl.fromName("Osmumten's fang").startsWith("https://oldschool.runescape.wiki/w/"));
	}

	@Test
	public void emptyWhenMissing()
	{
		assertEquals("", WikiItemUrl.fromName(null));
		assertEquals("", WikiItemUrl.fromName(""));
	}

	private static String decodeable(String name)
	{
		String url = WikiItemUrl.fromName(name);
		return url.replace("%28", "(").replace("%29", ")");
	}
}
