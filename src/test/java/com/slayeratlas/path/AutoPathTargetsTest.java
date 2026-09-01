package com.slayeratlas.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.MonsterLocation;
import com.slayeratlas.data.SlayerMonster;
import java.util.Collection;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import org.junit.Before;
import org.junit.Test;

public class AutoPathTargetsTest
{
	private MonsterDatabase database;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
	}

	@Test
	public void pathsToKonarsAssignedLocation()
	{
		SlayerMonster spectres = database.findByTaskName("Aberrant spectres");
		Collection<WorldPoint> targets = AutoPathTargets.of(database, spectres, "Catacombs of Kourend");
		MonsterLocation catacombs = database.getLocation("catacombs_kourend");

		assertEquals(1, targets.size());
		assertEquals(LocationPath.target(catacombs), targets.iterator().next());
	}

	@Test
	public void pathsToEveryListedLocationWhenNoKonarAreaIsAssigned()
	{
		SlayerMonster spectres = database.findByTaskName("Aberrant spectres");
		List<MonsterLocation> locations = database.locationsFor(spectres);
		Collection<WorldPoint> targets = AutoPathTargets.of(database, spectres, null);

		assertTrue(locations.size() > 1);
		assertEquals(locations.size(), targets.size());
		for (MonsterLocation location : locations)
		{
			assertTrue(targets.contains(LocationPath.target(location)));
		}
		assertEquals(targets, AutoPathTargets.of(database, spectres, ""));
	}

	@Test
	public void returnsNothingWhenTheMonsterIsMissing()
	{
		assertTrue(AutoPathTargets.of(database, null, null).isEmpty());
	}
}
