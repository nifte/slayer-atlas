package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class UniqueInventoryTest
{
	@Test
	public void keepsOnlyOneRockHammer()
	{
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			List.of(
				GearItem.named("Rock hammer"),
				GearItem.named("Dragon warhammer"),
				GearItem.named("Rock hammer")),
			GearRecommendation.specialized(),
			false);
		assertEquals(1, count(items, "Rock hammer"));
		assertEquals(1, count(items, "Dragon warhammer"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void keepsOnlyOnePairOfDragonClaws()
	{
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			List.of(
				GearItem.named("Dragon claws"),
				GearItem.named("Toxic blowpipe"),
				GearItem.named("Dragon claws")),
			GearRecommendation.specialized(),
			false);
		assertEquals(1, count(items, "Dragon claws"));
		assertEquals(1, count(items, "Toxic blowpipe"));
	}

	@Test
	public void collapsesChargedClawVariantsToOneSlot()
	{
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			List.of(
				GearItem.named("Dragon claws"),
				GearItem.named("Dragon claws (or)")),
			GearRecommendation.specialized(),
			false);
		assertEquals(1, countNamed(items, "Dragon claws"));
	}

	@Test
	public void keepsOnlyOneFishingExplosiveStack()
	{
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			List.of(
				GearItem.named("Dragon warhammer"),
				GearItem.named("Fishing explosive"),
				GearItem.named("Fishing explosive"),
				GearItem.named("Imbued heart")),
			GearRecommendation.specialized(),
			false);
		assertEquals(1, count(items, "Fishing explosive"));
		assertEquals(1, count(items, "Dragon warhammer"));
		assertEquals(1, count(items, "Imbued heart"));
	}

	@Test
	public void keepsMultiplePrayerPotionsAndFood()
	{
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			List.of(
				GearItem.named("Prayer potion(4)"),
				GearItem.named("Prayer potion(4)"),
				GearItem.named("Sanfew serum"),
				GearItem.named("Sanfew serum"),
				GearItem.named("Manta ray"),
				GearItem.named("Manta ray"),
				GearItem.named("Manta ray")),
			GearRecommendation.specialized(),
			false);
		assertEquals(2, count(items, "Prayer potion(4)"));
		assertEquals(2, count(items, "Sanfew serum(4)"));
		assertTrue(count(items, "Manta ray") >= 3);
	}

	@Test
	public void keepsDistinctWeaponSwitches()
	{
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			List.of(
				GearItem.named("Dragon warhammer"),
				GearItem.named("Dragon claws")),
			GearRecommendation.specialized(),
			false);
		assertEquals(1, count(items, "Dragon warhammer"));
		assertEquals(1, count(items, "Dragon claws"));
	}

	@Test
	public void fillsHolesAfterCollapsingDuplicateToolsInAWikiGrid()
	{
		List<GearItem> inventory = new ArrayList<>();
		inventory.add(GearItem.named("Dragon claws"));
		inventory.add(GearItem.named("Rock hammer"));
		inventory.add(GearItem.named("Toxic blowpipe"));
		inventory.add(GearItem.named("Dragon claws"));
		inventory.add(GearItem.named("Dragon warhammer"));
		inventory.add(GearItem.named("Rock hammer"));
		inventory.add(GearItem.named("Prayer potion(4)"));
		while (inventory.size() < InventoryLoadouts.SIZE)
		{
			inventory.add(GearItem.named("Manta ray"));
		}
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			inventory,
			GearRecommendation.specialized(),
			true);
		assertEquals("Dragon claws", items.get(0).getName());
		assertEquals("Rock hammer", items.get(1).getName());
		assertEquals("Toxic blowpipe", items.get(2).getName());
		assertEquals("Dragon warhammer", items.get(3).getName());
		assertEquals("Prayer potion(4)", items.get(4).getName());
		assertEquals(1, count(items, "Dragon claws"));
		assertEquals(1, count(items, "Rock hammer"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertTrue(count(items, "Manta ray") >= 16);
		assertNoEmptySlots(items);
	}

	@Test
	public void collapsesTwoFishingExplosivesToOneStackWithoutAHole()
	{
		List<GearItem> inventory = new ArrayList<>();
		inventory.add(GearItem.named("Dragon warhammer"));
		inventory.add(GearItem.named("Fishing explosive"));
		inventory.add(GearItem.named("Fishing explosive"));
		inventory.add(GearItem.named("Sanfew serum"));
		inventory.add(GearItem.named("Sanfew serum"));
		while (inventory.size() < InventoryLoadouts.SIZE)
		{
			inventory.add(GearItem.named("Manta ray"));
		}
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			inventory,
			GearRecommendation.specialized(),
			true);
		assertEquals("Fishing explosive", items.get(1).getName());
		assertEquals("Sanfew serum(4)", items.get(2).getName());
		assertEquals(1, count(items, "Fishing explosive"));
		assertEquals(2, count(items, "Sanfew serum(4)"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertNoEmptySlots(items);
	}

	@Test
	public void keepsTwoSlaughterBracelets()
	{
		List<GearItem> items = UniqueInventory.withoutDuplicates(
			List.of(
				GearItem.named("Bracelet of slaughter"),
				GearItem.named("Bracelet of slaughter")),
			GearRecommendation.specialized(),
			false);
		assertEquals(2, count(items, "Bracelet of slaughter"));
	}

	private static void assertNoEmptySlots(List<GearItem> items)
	{
		for (GearItem item : items)
		{
			assertNotNull(item);
			assertNotNull(item.getName());
		}
	}

	private static int count(List<GearItem> items, String name)
	{
		int total = 0;
		for (GearItem item : items)
		{
			if (item != null && name.equals(item.getName()))
			{
				total++;
			}
		}
		return total;
	}

	private static int countNamed(List<GearItem> items, String name)
	{
		int total = 0;
		for (GearItem item : items)
		{
			if (item != null && item.getName() != null && OwnedItemNames.matches(item.getName(), name))
			{
				total++;
			}
		}
		return total;
	}
}
