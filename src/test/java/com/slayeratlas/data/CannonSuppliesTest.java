package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

public class CannonSuppliesTest
{
	private MonsterDatabase database;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
	}

	@Test
	public void caveHorrorsAreCannonableAtEveryListedLocation()
	{
		assertTrue(CannonSupplies.needsCannon(database.findByTaskName("Cave horrors")));
	}

	@Test
	public void gargoylesSkipACannonBecauseSlayerTowerForbidsIt()
	{
		assertFalse(CannonableLocations.isCannonable(database.getLocation("slayer_tower")));
		assertFalse(CannonSupplies.needsCannon(database.findByTaskName("Gargoyles")));
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("Marble gargoyle")));
	}

	@Test
	public void hellhoundsSkipACannonBecauseGodWarsIsListed()
	{
		assertFalse(CannonSupplies.needsCannon(database.findByTaskName("Hellhounds")));
	}

	@Test
	public void elvesSkipACannonBecauseNotEveryCitySpotIsCannonable()
	{
		assertFalse(CannonSupplies.needsCannon(database.findByTaskName("Elves")));
	}

	@Test
	public void instancedBossAlternativesDoNotGetACannon()
	{
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("Kraken")));
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("Abyssal Sire")));
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("Cerberus")));
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("Alchemical Hydra")));
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("Shellbane gryphon")));
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("Grotesque Guardians")));
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("Vorkath")));
		assertFalse(CannonSupplies.needsCannon(database.findNamedPage("King Black Dragon")));
	}

	@Test
	public void caveKrakenStayWithoutACannonBecauseTheCoveIsWater()
	{
		assertFalse(CannonSupplies.needsCannon(database.findByTaskName("Cave kraken")));
	}

	@Test
	public void gryphonsGetACannonWhenBothShownCavesAreCannonable()
	{
		assertTrue(CannonSupplies.needsCannon(database.findByTaskName("Gryphons")));
	}

	@Test
	public void travelTextCanAllowOrForbidALocation()
	{
		assertTrue(CannonableLocations.isCannonable(database.getLocation("eastern_gryphon_dungeon")));
		assertTrue(CannonableLocations.isCannonable(database.getLocation("neypotzli")));
		assertFalse(CannonableLocations.isCannonable(database.getLocation("shellbane_gryphon_cave")));
		assertFalse(CannonableLocations.isCannonable(database.getLocation("charred_dungeon")));
		assertFalse(CannonableLocations.isCannonable(database.getLocation("tonali_cavern")));
		assertFalse(CannonableLocations.isCannonable(database.getLocation("slayer_tower")));
	}

	@Test
	public void prefersGraniteCannonballsOverSteel()
	{
		assertEquals(
			CannonSupplies.GRANITE_CANNONBALL,
			CannonSupplies.pickCannonballs(GearRecommendation.specialized()).getName());
		assertEquals(
			CannonSupplies.CANNONBALL,
			CannonSupplies.pickCannonballs(
				GearRecommendation.of(true, OwnedItems.withBank(java.util.Set.of(CannonSupplies.CANNONBALL))))
				.getName());
		assertTrue(CannonSupplies.isCannonball(CannonSupplies.GRANITE_CANNONBALL));
		assertTrue(CannonSupplies.isCannonball(CannonSupplies.CANNONBALL));
		assertFalse(CannonSupplies.isCannonPiece(CannonSupplies.GRANITE_CANNONBALL));
	}

	@Test
	public void stubMonstersWithoutLocationsDoNotGetACannon()
	{
		SlayerMonster stub = new Gson().fromJson("{\"name\":\"Test\"}", SlayerMonster.class);
		assertFalse(CannonSupplies.needsCannon(stub));
		assertFalse(CannonSupplies.needsCannon(null));
	}

	@Test
	public void everyPageFollowsTheAllDisplayedLocationsRule()
	{
		for (SlayerMonster monster : database.getPages())
		{
			assertTrue(
				monster.getName() + " cannon recommendation should match displayed locations",
				expectedCannon(monster) == CannonSupplies.needsCannon(monster));
		}
	}

	private boolean expectedCannon(SlayerMonster monster)
	{
		if (monster.getLocationIds() == null || monster.getLocationIds().isEmpty())
		{
			return false;
		}
		java.util.ArrayList<SlayerMonster> alternatives = new java.util.ArrayList<>();
		if (monster.getAlternatives() != null)
		{
			for (String label : monster.getAlternatives())
			{
				SlayerMonster alternative = AlternativeMonsters.find(database, label, monster);
				if (alternative != null)
				{
					alternatives.add(alternative);
				}
			}
		}
		int checked = 0;
		for (String locationId : monster.getLocationIds())
		{
			if (!TaskLocations.include(monster, locationId, alternatives))
			{
				continue;
			}
			if (!CannonableLocations.isCannonable(locationId, database.getLocation(locationId)))
			{
				return false;
			}
			checked++;
		}
		return checked > 0;
	}
}
