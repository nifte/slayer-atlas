package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class WikiPageNamesTest
{
	@Test
	public void includesStrategyAndSlayerTaskPages()
	{
		SlayerMonster wyverns = new Gson().fromJson(
			"{\"name\":\"Skeletal Wyverns\",\"wiki\":\"https://oldschool.runescape.wiki/w/Skeletal_Wyvern\"}",
			SlayerMonster.class);
		List<String> candidates = WikiPageNames.candidates(wyverns);
		assertTrue(WikiPageNames.matches("Skeletal Wyvern/Strategies", candidates));
		assertTrue(WikiPageNames.matches("Slayer task/Skeletal Wyverns", candidates));
		assertFalse(WikiPageNames.matches("Slayer task/Fossil Island wyverns", candidates));
	}

	@Test
	public void usesExistingSlayerTaskWikiPath()
	{
		SlayerMonster spectres = new Gson().fromJson(
			"{\"name\":\"Aberrant spectres\",\"wiki\":\"https://oldschool.runescape.wiki/w/Slayer_task/Aberrant_spectres\"}",
			SlayerMonster.class);
		assertTrue(WikiPageNames.matches("Slayer task/Aberrant spectres", WikiPageNames.candidates(spectres)));
		assertTrue(WikiPageNames.matches("Aberrant spectre/Strategies", WikiPageNames.candidates(spectres)));
	}

	@Test
	public void assignmentPagesDoNotUseAlternativeBossStrategies()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster bears = database.findByTaskName("Bears");
		assertTrue(WikiPageNames.matches("Slayer task/Bears", bears));
		assertTrue(WikiPageNames.matches("Bear/Strategies", bears));
		assertFalse(WikiPageNames.matches("Callisto/Strategies", bears));
		assertFalse(WikiPageNames.matches("Artio/Strategies", bears));

		SlayerMonster cave = database.findByTaskName("Cave kraken");
		assertTrue(WikiPageNames.matches("Slayer task/Cave krakens", cave));
		assertTrue(WikiPageNames.matches("Cave kraken/Strategies", cave));
		assertFalse(WikiPageNames.matches("Kraken/Strategies", cave));
		assertFalse(WikiPageNames.matches("Dust devil/Strategies", cave));

		SlayerMonster dragons = database.findByTaskName("Black dragons");
		assertFalse(WikiPageNames.matches("King Black Dragon/Strategies", dragons));
		assertFalse(WikiPageNames.matches("KBD/Strategies", dragons));
	}

	@Test
	public void alternativePagesKeepTheirOwnBossStrategies()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertTrue(WikiPageNames.matches("Callisto/Strategies", database.findNamedPage("Callisto")));
		assertTrue(WikiPageNames.matches("Kraken/Strategies", database.findNamedPage("Kraken")));
		assertTrue(WikiPageNames.matches("King Black Dragon/Strategies", database.findNamedPage("King Black Dragon")));
	}

	@Test
	public void frostDragonsUseTheSingularStrategiesPage()
	{
		SlayerMonster frost = new MonsterDatabase(new Gson()).findByTaskName("Frost dragons");
		assertTrue(WikiPageNames.matches("Frost dragon/Strategies", frost));
		assertTrue(WikiPageNames.matches("Frost_dragon/Strategies", frost));
		List<String> pages = WikiPageNames.inventoryPages(frost);
		assertTrue(pages.get(0).toLowerCase().endsWith("/strategies"));
	}

	@Test
	public void inventoryPagesPreferStrategiesThenSlayerTaskPages()
	{
		SlayerMonster spectres = new Gson().fromJson(
			"{\"name\":\"Aberrant spectres\",\"wiki\":\"https://oldschool.runescape.wiki/w/Slayer_task/Aberrant_spectres\"}",
			SlayerMonster.class);
		List<String> pages = WikiPageNames.inventoryPages(spectres);
		assertTrue(pages.get(0).toLowerCase().endsWith("/strategies"));
		assertTrue(WikiPageNames.matches("Aberrant spectres/Strategies", pages));
		assertTrue(WikiPageNames.matches("Slayer task/Aberrant spectres", pages));
		assertTrue(pages.size() <= 6);
	}

	@Test
	public void mergePrefersWikiThenFillsRecommendedStyles()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"name\":\"Hydras\",\"recommendedStyle\":\"Melee or ranged\"}",
			SlayerMonster.class);
		List<GearLoadout> wiki = List.of(BisLoadouts.melee());
		List<GearLoadout> merged = GearLoadouts.forMonster(monster, wiki);
		assertEquals(2, merged.size());
		assertEquals(CombatStyle.MELEE, merged.get(0).getStyle());
		assertEquals(CombatStyle.RANGED, merged.get(1).getStyle());
	}
}
