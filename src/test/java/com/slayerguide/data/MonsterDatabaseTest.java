package com.slayerguide.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
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
	public void prefersAssignedKonarLocation()
	{
		SlayerMonster spectres = database.findByTaskName("Aberrant spectres");
		MonsterLocation location = database.preferredLocation(spectres, "Catacombs of Kourend");
		assertNotNull(location);
		assertTrue(location.getName().toLowerCase().contains("catacombs"));
	}
}
