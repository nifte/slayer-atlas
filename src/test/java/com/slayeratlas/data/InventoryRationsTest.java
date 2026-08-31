package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import org.junit.Test;

public class InventoryRationsTest
{
	@Test
	public void usesNoFoodWhenAnOverheadFullyBlocksTheMonster()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertEquals(0, InventoryRations.foodSlots(database.findByTaskName("Dust devils")));
		assertEquals(0, InventoryRations.foodSlots(database.findByTaskName("Cave kraken")));
		assertEquals(0, InventoryRations.foodSlots(database.findByTaskName("Nechryael")));
		assertTrue(InventoryRations.fullyNegated(database.findByTaskName("Dust devils")));
	}

	@Test
	public void packsFoodOnWyvernsBecauseRangedAndBreathHitThroughMeleePrayer()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster skeletal = database.findByTaskName("Skeletal Wyverns");
		SlayerMonster fossil = database.findByTaskName("Fossil Island wyverns");
		assertFalse(InventoryRations.fullyNegated(skeletal));
		assertFalse(InventoryRations.fullyNegated(fossil));
		int skeletalFood = InventoryRations.foodSlots(skeletal);
		int fossilFood = InventoryRations.foodSlots(fossil);
		assertTrue(skeletalFood >= 6);
		assertTrue(skeletalFood <= 16);
		assertTrue(fossilFood >= skeletalFood);
	}

	@Test
	public void scalesFoodWhenDragonfireHitsThroughPrayer()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		assertFalse(InventoryRations.fullyNegated(dragons));
		int food = InventoryRations.foodSlots(dragons);
		assertTrue(food >= 4);
		assertTrue(food <= 10);
	}

	@Test
	public void usesLittleFoodWhenNoOverheadIsNeeded()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertEquals(1, InventoryRations.foodSlots(database.findByTaskName("Birds")));
		int crabs = InventoryRations.foodSlots(database.findByTaskName("Crabs"));
		assertTrue(crabs > 1);
		assertTrue(crabs <= 8);
	}

	@Test
	public void treatsAlternatingPrayersAsFullyBlockable()
	{
		SlayerMonster hydras = new MonsterDatabase(new Gson()).findByTaskName("Hydras");
		assertTrue(InventoryRations.fullyNegated(hydras));
		assertEquals(0, InventoryRations.foodSlots(hydras));
	}

	@Test
	public void packsFoodOnTheKrakenBossBecauseItHitsThroughProtectFromMagic()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster cave = database.findByTaskName("Cave kraken");
		SlayerMonster boss = database.findNamedPage("Kraken");
		assertTrue(InventoryRations.fullyNegated(cave));
		assertEquals(0, InventoryRations.foodSlots(cave));
		assertFalse(InventoryRations.fullyNegated(boss));
		int food = InventoryRations.foodSlots(boss);
		assertTrue(food >= 4);
		assertTrue(food <= 16);
	}
}
