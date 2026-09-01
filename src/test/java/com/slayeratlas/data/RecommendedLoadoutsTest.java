package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

public class RecommendedLoadoutsTest
{
	private MonsterDatabase database;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
	}

	@Test
	public void treatsBabyBlueDragonsAsTheSameSetupAsBlueDragons()
	{
		assertTrue(RecommendedLoadouts.same(
			database.findByTaskName("Blue dragons"),
			database.findNamedPage("Baby blue dragon")));
	}

	@Test
	public void treatsVorkathAsADifferentSetupFromBlueDragons()
	{
		assertFalse(RecommendedLoadouts.same(
			database.findByTaskName("Blue dragons"),
			database.findNamedPage("Vorkath")));
	}

	@Test
	public void treatsTheKrakenBossAsADifferentSetupFromCaveKraken()
	{
		assertFalse(RecommendedLoadouts.same(
			database.findByTaskName("Cave kraken"),
			database.findNamedPage("Kraken")));
	}

	@Test
	public void treatsElderChaosDruidsAsTheSameSetupAsChaosDruids()
	{
		assertTrue(RecommendedLoadouts.same(
			database.findByTaskName("Chaos druids"),
			database.findNamedPage("Elder Chaos druid")));
	}

	@Test
	public void treatsNullAsOnlyMatchingNull()
	{
		assertTrue(RecommendedLoadouts.same(null, null));
		assertFalse(RecommendedLoadouts.same(database.findByTaskName("Cave kraken"), null));
	}
}
