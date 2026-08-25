package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import org.junit.Test;

public class AlternativeMonstersTest
{
	@Test
	public void stripsParentheticalNotesFromTheLookupName()
	{
		assertEquals("Araxyte", AlternativeMonsters.lookupName("Araxyte (separate task)"));
		assertEquals("Black Heather", AlternativeMonsters.lookupName("Black Heather"));
		assertEquals("Araxyte.png", AlternativeMonsters.imageFile("Araxyte (separate task)"));
		assertEquals("araxyte", AlternativeMonsters.slug("Araxyte (separate task)"));
	}

	@Test
	public void findsADifferentCatalogTask()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster spiders = database.findByTaskName("Spiders");
		SlayerMonster araxytes = AlternativeMonsters.find(database, "Araxyte (separate task)", spiders);
		assertEquals("Araxytes", araxytes.getName());
		assertTrue(araxytes.isAssignment());
	}

	@Test
	public void findsADedicatedPageEvenWhenItIsAnAliasOfTheCurrentTask()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster spiders = database.findByTaskName("Spiders");
		SlayerMonster venenatis = AlternativeMonsters.find(database, "Venenatis", spiders);
		assertEquals("Venenatis", venenatis.getName());
		assertFalse(venenatis.isAssignment());
		assertEquals("venenatis_den", venenatis.getRecommendedLocationId());
	}

	@Test
	public void findsANamedNpcPage()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster bandits = database.findByTaskName("Bandits");
		SlayerMonster heather = AlternativeMonsters.find(database, "Black Heather", bandits);
		assertEquals("Black Heather", heather.getName());
		assertEquals("black_heather", heather.getId());
		assertEquals("bandit_camp_wildy", heather.getRecommendedLocationId());
		assertEquals(Integer.valueOf(34), heather.getCombatLevelMin());
	}

	@Test
	public void resolvesKnownAlternativesToTheCatalogTask()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster spiders = database.findByTaskName("Spiders");
		assertEquals(
			"Araxytes",
			AlternativeMonsters.resolve(database, "Araxyte (separate task)", spiders).getName());
	}

	@Test
	public void resolvesCurrentTaskAliasesToACatalogPage()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster dragons = database.findByTaskName("Black dragons");
		SlayerMonster kbd = AlternativeMonsters.resolve(
			database,
			"King Black Dragon (not on Krystilia tasks)",
			dragons);
		assertEquals("King Black Dragon", kbd.getName());
		assertEquals("king_black_dragon", kbd.getId());
		assertEquals("Draconic", kbd.getAttribute());
		assertEquals(Integer.valueOf(276), kbd.getCombatLevelMin());
		assertEquals("kbd_lair", kbd.getRecommendedLocationId());
		assertFalse(kbd.isAssignment());
		assertNotNull(database.getLocation("kbd_lair"));
	}

	@Test
	public void inheritsHellhoundContextForCerberus()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster dogs = database.findByTaskName("Dogs");
		SlayerMonster cerberus = AlternativeMonsters.resolve(database, "Cerberus (hellhound family, not dogs)", dogs);
		assertEquals("Cerberus", cerberus.getName());
		assertEquals(91, cerberus.getSlayerLevel());
		assertEquals("cerberus_lair", cerberus.getRecommendedLocationId());
	}
}
