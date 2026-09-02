package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class TaskLocationsTest
{
	private MonsterDatabase database;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
	}

	@Test
	public void hidesTheKrakenBossFromCaveKrakenBecauseInventoryDiffers()
	{
		assertEquals(List.of("kraken_cove"), ids("Cave kraken"));
		assertEquals(List.of("kraken_boss"), ids(database.findNamedPage("Kraken")));
	}

	@Test
	public void hidesVorkathFromBlueDragonsButKeepsSharedBabyBlueCaves()
	{
		List<String> ids = ids("Blue dragons");
		assertFalse(ids.contains("vorkath_isle"));
		assertTrue(ids.contains("heroes_guild"));
		assertTrue(ids.contains("taverley_dungeon"));
		assertTrue(ids.contains("corsair_cove_dungeon"));
		assertEquals(List.of("vorkath_isle"), ids(database.findNamedPage("Vorkath")));
	}

	@Test
	public void keepsASharedSpotWhenTheAlternateUsesTheSameSetup()
	{
		assertTrue(ids("Chaos druids").contains("chaos_temple_wildy"));
	}

	@Test
	public void hidesBossLairsWhenTheAlternateUsesADifferentSetup()
	{
		assertFalse(ids("Abyssal demons").contains("sire_lair"));
		assertFalse(ids("Gargoyles").contains("dusk_lair"));
		assertFalse(ids("Hydras").contains("hydra_lair"));
	}

	@Test
	public void keepsWyrmscraigOnWyrmsEvenThoughLavaStrykewyrmsUseADifferentSetup()
	{
		List<String> ids = ids("Wyrms");
		assertTrue(ids.contains("karuulm_slayer_dungeon"));
		assertTrue(ids.contains("wyrmscraig"));
		assertFalse(ids.contains("charred_dungeon"));
		assertFalse(ids.contains("neypotzli"));
	}

	@Test
	public void stillListsTheAssignmentWhenLocationsAreMissing()
	{
		assertTrue(TaskLocations.resolve(null, database).isEmpty());
		assertTrue(TaskLocations.resolve(database.findByTaskName("Cave kraken"), null).isEmpty());
	}

	private List<String> ids(String taskName)
	{
		return ids(database.findByTaskName(taskName));
	}

	private List<String> ids(SlayerMonster monster)
	{
		List<String> ids = new ArrayList<>();
		for (MonsterLocation location : database.locationsFor(monster))
		{
			ids.add(location.getId());
		}
		return ids;
	}
}
