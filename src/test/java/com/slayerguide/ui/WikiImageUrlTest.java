package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayerguide.data.SlayerMonster;
import org.junit.Test;

public class WikiImageUrlTest
{
	@Test
	public void usesCatalogImageFile()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"name\":\"Skeletal Wyverns\",\"image\":\"Skeletal Wyvern.png\"}",
			SlayerMonster.class);
		assertEquals("Skeletal Wyvern.png", WikiImageUrl.fileName(monster));
		assertEquals(
			"https://oldschool.runescape.wiki/w/Special:Redirect/file/Skeletal_Wyvern.png",
			WikiImageUrl.fromFileName(WikiImageUrl.fileName(monster)));
	}

	@Test
	public void emptyWhenMissing()
	{
		assertEquals("", WikiImageUrl.fileName(null));
		assertEquals("", WikiImageUrl.fromFileName(""));
	}

	@Test
	public void wikiRedirectIsHttps()
	{
		String url = WikiImageUrl.fromFileName("Abyssal demon.png");
		assertTrue(url.startsWith("https://oldschool.runescape.wiki/w/Special:Redirect/file/"));
		assertTrue(url.contains("Abyssal_demon.png"));
	}
}
