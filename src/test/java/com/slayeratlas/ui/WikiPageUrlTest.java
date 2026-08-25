package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WikiPageUrlTest
{
	@Test
	public void buildsAnOsrsWikiLink()
	{
		assertEquals(
			"https://oldschool.runescape.wiki/w/Black_Heather",
			WikiPageUrl.forTitle("Black Heather"));
		assertEquals("", WikiPageUrl.forTitle(""));
		assertEquals("", WikiPageUrl.forTitle(null));
	}
}
