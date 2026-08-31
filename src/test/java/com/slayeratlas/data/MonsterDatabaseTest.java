package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ui.DpsCalculatorUrl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class MonsterDatabaseTest
{
	private MonsterDatabase database;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
	}

	@Test
	public void loadsEveryAssignment()
	{
		assertTrue("Expected a full slayer assignment list.", database.getMonsters().size() >= 110);
		assertFalse(database.getLocationsById().isEmpty());
	}

	@Test
	public void requiredItemsAreNotSkillLevels()
	{
		for (SlayerMonster monster : database.getPages())
		{
			for (String item : monster.getRequiredItems())
			{
				assertFalse(
					monster.getName() + " required item is a skill level: " + item,
					SkillRequirement.isLevel(item));
			}
		}
	}

	@Test
	public void everyMonsterHasValidLocations()
	{
		for (SlayerMonster monster : database.getMonsters())
		{
			assertNotNull(monster.getId());
			assertNotNull(monster.getName());
			assertFalse(monster.getName(), monster.getLocationIds().isEmpty());
			assertNotNull(monster.getName(), monster.getWiki());
			for (String locationId : monster.getLocationIds())
			{
				assertNotNull(monster.getName() + " missing location " + locationId, database.getLocation(locationId));
			}
			if (monster.getRecommendedLocationId() != null)
			{
				assertNotNull(monster.getName(), database.getLocation(monster.getRecommendedLocationId()));
			}
		}
	}

	@Test
	public void locationCoordinatesArePresent()
	{
		for (MonsterLocation location : database.getLocationsById().values())
		{
			assertTrue(location.getName(), location.getX() > 0);
			assertTrue(location.getName(), location.getY() > 0);
			assertFalse(location.getName(), location.getTravel().isEmpty());
		}
	}

	@Test
	public void instanceRoomsPathToAWalkableEntrance()
	{
		assertPathOverride("kraken_boss", 2278, 3611, 0);
		assertPathOverride("hydra_lair", 1311, 3807, 0);
		assertPathOverride("karuulm_slayer_dungeon", 1311, 3807, 0);
		assertPathOverride("thermomy_lair", 2412, 3061, 0);
		assertPathOverride("sire_lair", 3028, 4833, 0);
		assertPathOverride("kbd_lair", 3017, 3849, 0);
		assertPathOverride("fight_caves", 2856, 3168, 0);
		assertPathOverride("inferno", 2856, 3168, 0);
		assertPathOverride("jorunn_cave", 2794, 3615, 0);
		assertPathOverride("gwd_zamorak", 2918, 3751, 0);
		assertPathOverride("gwd_armadyl", 2918, 3751, 0);
	}

	private void assertPathOverride(String id, int x, int y, int plane)
	{
		MonsterLocation location = database.getLocation(id);
		assertNotNull(id, location);
		assertTrue(id, location.getPathX() > 0);
		assertEquals(id, x, location.getPathX());
		assertEquals(id, y, location.getPathY());
		assertEquals(id, plane, location.getPathPlane());
		assertTrue(id, location.getX() != x || location.getY() != y || location.getPlane() != plane);
	}

	@Test
	public void matchesCommonTaskNames()
	{
		assertEquals("Aberrant spectres", database.findByTaskName("Aberrant spectres").getName());
		assertEquals("Aberrant spectres", database.findByTaskName("ABERRANT SPECTRES").getName());
		assertEquals("Aberrant spectres", database.findByTaskName("Aberrant spectre").getName());
		assertEquals("Nechryael", database.findByTaskName("Nechryaels").getName());
		assertEquals("Cave kraken", database.findByTaskName("Cave krakens").getName());
		assertEquals("Metal dragons", database.findByTaskName("Steel dragons").getName());
		assertEquals("Gargoyles", database.findByTaskName("Gargoyle").getName());
		assertEquals("Abyssal demons", database.findByTaskName("Abyssal demon").getName());
	}

	@Test
	public void searchFindsPartialNames()
	{
		List<SlayerMonster> dust = database.search("dust");
		assertFalse(dust.isEmpty());
		assertEquals("Dust devils", dust.get(0).getName());

		List<SlayerMonster> wyrm = database.search("wyrm");
		Set<String> names = new HashSet<>();
		for (SlayerMonster monster : wyrm)
		{
			names.add(monster.getName());
		}
		assertTrue(names.contains("Wyrms"));
	}

	@Test
	public void loadsNpcCombatLevelsSeparateFromAssignmentRequirement()
	{
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		assertEquals(Integer.valueOf(140), wyverns.getCombatLevelMin());
		assertEquals(Integer.valueOf(140), wyverns.getCombatLevelMax());
		assertEquals(Integer.valueOf(70), wyverns.getCombatRequirement());

		SlayerMonster spectres = database.findByTaskName("Aberrant spectres");
		assertEquals(Integer.valueOf(96), spectres.getCombatLevelMin());
		assertEquals(Integer.valueOf(169), spectres.getCombatLevelMax());
	}

	@Test
	public void everyMonsterHasCombatLevels()
	{
		for (SlayerMonster monster : database.getMonsters())
		{
			assertNotNull(monster.getName(), monster.getCombatLevelMin());
			assertNotNull(monster.getName(), monster.getCombatLevelMax());
			assertTrue(monster.getName(), monster.getCombatLevelMin() <= monster.getCombatLevelMax());
		}
	}

	@Test
	public void everyMonsterHasAnImageFile()
	{
		for (SlayerMonster monster : database.getMonsters())
		{
			assertNotNull(monster.getName(), monster.getImage());
			assertTrue(monster.getName(), monster.getImage().endsWith(".png"));
		}
		assertEquals("Skeletal Wyvern.png", database.findByTaskName("Skeletal Wyverns").getImage());
		assertEquals("Aberrant spectre.png", database.findByTaskName("Aberrant spectres").getImage());
		assertEquals("Monkey.png", database.findByTaskName("Monkeys").getImage());
		assertEquals("Pirate (Brimhaven).png", database.findByTaskName("Pirates").getImage());
		assertEquals("Zombie (Level 13).png", database.findByTaskName("Zombies").getImage());
		assertEquals("Cave crawler (1).png", database.findByTaskName("Cave crawlers").getImage());
	}

	@Test
	public void everyMonsterHasAWikiDpsLink()
	{
		for (SlayerMonster monster : database.getMonsters())
		{
			assertEquals(monster.getName(), monster.getDps(), DpsCalculatorUrl.fromMonster(monster));
			assertTrue(monster.getName(), monster.getDps().startsWith("https://tools.runescape.wiki/osrs-dps/?monster="));
		}
		assertEquals(
			"https://tools.runescape.wiki/osrs-dps/?monster=104",
			database.findByTaskName("Hellhounds").getDps());
		assertEquals(
			"https://tools.runescape.wiki/osrs-dps/?monster=468",
			database.findByTaskName("Skeletal Wyverns").getDps());
	}

	@Test
	public void prefersAssignedKonarLocation()
	{
		SlayerMonster spectres = database.findByTaskName("Aberrant spectres");
		MonsterLocation location = database.preferredLocation(spectres, "Catacombs of Kourend");
		assertNotNull(location);
		assertTrue(location.getName().toLowerCase().contains("catacombs"));
	}

	@Test
	public void keepsAlternativePagesOutOfTheAssignmentList()
	{
		assertTrue(database.getPages().size() > database.getMonsters().size());
		assertEquals("Black dragons", database.findByTaskName("King Black Dragon").getName());
		assertEquals("King Black Dragon", database.findNamedPage("King Black Dragon").getName());
		assertFalse(database.getMonster("king_black_dragon").isAssignment());
	}

	@Test
	public void everyAlternativePageHasLocationsAndCombat()
	{
		int alternatives = 0;
		for (SlayerMonster monster : database.getPages())
		{
			if (monster.isAssignment())
			{
				continue;
			}
			alternatives++;
			assertFalse(monster.getName(), monster.getLocationIds().isEmpty());
			assertNotNull(monster.getName(), monster.getCombatLevelMin());
			assertNotNull(monster.getName(), monster.getCombatLevelMax());
			assertNotNull(monster.getName(), monster.getWiki());
			assertNotNull(monster.getName(), monster.getImage());
			assertEquals(monster.getName(), monster.getDps(), DpsCalculatorUrl.fromMonster(monster));
			for (String locationId : monster.getLocationIds())
			{
				assertNotNull(monster.getName() + " missing location " + locationId, database.getLocation(locationId));
			}
		}
		assertTrue("Expected catalog pages for alternatives.", alternatives >= 100);
	}

	@Test
	public void searchCanFindAlternativePages()
	{
		assertEquals("King Black Dragon", database.search("King Black Dragon").get(0).getName());
		for (SlayerMonster monster : database.search(""))
		{
			assertTrue(monster.getName(), monster.isAssignment());
		}
	}

	@Test
	public void babyDragonPagesUseWikiImageFiles()
	{
		assertEquals("Baby blue dragon (1).png", database.findNamedPage("Baby blue dragon").getImage());
		assertEquals("Baby green dragon (1).png", database.findNamedPage("Baby green dragon").getImage());
		assertEquals("Baby red dragon (1).png", database.findNamedPage("Baby red dragon").getImage());
		assertEquals("Baby black dragon.png", database.findNamedPage("Baby black dragon").getImage());
	}
}
