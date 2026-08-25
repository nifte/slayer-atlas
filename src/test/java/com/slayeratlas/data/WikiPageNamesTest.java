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
