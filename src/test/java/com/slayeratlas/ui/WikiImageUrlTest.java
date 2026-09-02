package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.data.SlayerMonster;
import java.util.List;
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

	@Test
	public void requestsAThumbnailWidth()
	{
		String url = WikiImageUrl.fromFileName("Cave crawler (1).png", 96);
		assertTrue(url.contains("Cave_crawler_(1).png") || url.contains("Cave_crawler_%281%29.png"));
		assertTrue(url.contains("width=96"));
	}

	@Test
	public void numberedVariantFollowsWikiCombatVersions()
	{
		assertEquals("Baby blue dragon (1).png", WikiImageUrl.firstVariant("Baby blue dragon.png"));
		assertEquals("Baby green dragon (1).png", WikiImageUrl.firstVariant("Baby green dragon.png"));
		assertEquals("", WikiImageUrl.firstVariant("Cave crawler (1).png"));
		assertEquals("", WikiImageUrl.firstVariant("Baby black dragon (1).png"));
		assertEquals("", WikiImageUrl.firstVariant("Prayer potion(4).png"));
		assertEquals("", WikiImageUrl.firstVariant(""));
	}

	@Test
	public void retriesTheUndosedWikiFileForPotionIcons()
	{
		List<String> names = WikiImageUrl.fetchNames("Prayer potion(4).png");
		assertEquals("Prayer potion(4).png", names.get(0));
		assertTrue(names.contains("Prayer potion.png"));
		assertEquals("", WikiImageUrl.withoutDose("Shark.png"));
	}

	@Test
	public void retriesProperNounCapitalizationForWikiItemFiles()
	{
		List<String> names = WikiImageUrl.fetchNames("Bow of faerdhinen.png");
		assertEquals("Bow of faerdhinen.png", names.get(0));
		assertEquals("Bow of faerdhinen (1).png", names.get(1));
		assertTrue(names.contains("Bow of Faerdhinen.png"));
		assertTrue(WikiImageUrl.fetchNames("Imbued saradomin cape.png").contains("Imbued Saradomin cape.png"));
	}

	@Test
	public void retriesTightParenWikiFiles()
	{
		List<String> spaced = WikiImageUrl.fetchNames("Salve amulet (ei).png");
		assertEquals("Salve amulet (ei).png", spaced.get(0));
		assertEquals("Salve amulet(ei).png", spaced.get(1));
		List<String> tight = WikiImageUrl.fetchNames("Salve amulet(ei).png");
		assertEquals("Salve amulet(ei).png", tight.get(0));
		assertTrue(tight.contains("Salve amulet (ei).png"));
	}

	@Test
	public void retriesUnlockedWikiFileForLockedItems()
	{
		List<String> names = WikiImageUrl.fetchNames("Ava's assembler (l).png");
		assertEquals("Ava's assembler (l).png", names.get(0));
		assertTrue(names.contains("Ava's assembler.png"));
	}
}
