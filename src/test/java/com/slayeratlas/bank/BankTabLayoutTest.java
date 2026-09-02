package com.slayeratlas.bank;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.EquipmentSlot;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.PlayerLoadouts;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class BankTabLayoutTest
{
	@Test
	public void placesEquipmentOnTheWornDoll()
	{
		assertEquals(1, BankTabLayout.equipmentIndex(EquipmentSlot.HEAD));
		assertEquals(8, BankTabLayout.equipmentIndex(EquipmentSlot.CAPE));
		assertEquals(9, BankTabLayout.equipmentIndex(EquipmentSlot.NECK));
		assertEquals(10, BankTabLayout.equipmentIndex(EquipmentSlot.AMMO));
		assertEquals(16, BankTabLayout.equipmentIndex(EquipmentSlot.WEAPON));
		assertEquals(17, BankTabLayout.equipmentIndex(EquipmentSlot.BODY));
		assertEquals(18, BankTabLayout.equipmentIndex(EquipmentSlot.SHIELD));
		assertEquals(25, BankTabLayout.equipmentIndex(EquipmentSlot.LEGS));
		assertEquals(32, BankTabLayout.equipmentIndex(EquipmentSlot.HANDS));
		assertEquals(33, BankTabLayout.equipmentIndex(EquipmentSlot.FEET));
		assertEquals(34, BankTabLayout.equipmentIndex(EquipmentSlot.RING));
		assertEquals(-1, BankTabLayout.equipmentIndex(EquipmentSlot.TWO_HAND));
	}

	@Test
	public void placesInventoryInFourColumnsToTheRight()
	{
		assertEquals(4, BankTabLayout.inventoryIndex(0));
		assertEquals(5, BankTabLayout.inventoryIndex(1));
		assertEquals(7, BankTabLayout.inventoryIndex(3));
		assertEquals(12, BankTabLayout.inventoryIndex(4));
		assertEquals(55, BankTabLayout.inventoryIndex(27));
		assertEquals(-1, BankTabLayout.inventoryIndex(28));
		assertEquals(BankItemGrid.x(4), BankTabLayout.x(BankTabLayout.inventoryIndex(0)));
		assertEquals(BankItemGrid.y(12), BankTabLayout.y(BankTabLayout.inventoryIndex(4)));
	}

	@Test
	public void leavesAGapBetweenEquipmentAndInventory()
	{
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			int index = BankTabLayout.equipmentIndex(slot);
			if (index >= 0)
			{
				assertEquals(slot.column(), index % BankItemGrid.COLUMNS);
				assertTrue(index % BankItemGrid.COLUMNS <= 2);
			}
		}
		for (int slot = 0; slot < BankTabLayout.INVENTORY_SIZE; slot++)
		{
			assertTrue(BankTabLayout.inventoryIndex(slot) % BankItemGrid.COLUMNS >= 4);
		}
	}

	@Test
	public void assignsBankItemsToLoadoutSlots()
	{
		GearLoadout loadout = PlayerLoadouts.named(
			CombatStyle.MELEE,
			Map.of(
				EquipmentSlot.HEAD, "Slayer helmet (i)",
				EquipmentSlot.WEAPON, "Dragon warhammer",
				EquipmentSlot.RING, "Berserker ring (i)"),
			List.of("Super combat potion(4)", "Shark", "Shark"));
		int[] grids = BankTabLayout.gridIndexes(
			loadout,
			List.of(
				"Shark",
				"Slayer helmet (i)",
				"Super combat potion(4)",
				"Dragon warhammer",
				"Bones"));
		assertArrayEquals(
			new int[] {
				BankTabLayout.inventoryIndex(1),
				BankTabLayout.equipmentIndex(EquipmentSlot.HEAD),
				BankTabLayout.inventoryIndex(0),
				BankTabLayout.equipmentIndex(EquipmentSlot.WEAPON),
				BankTabLayout.EXTRAS_START
			},
			grids);
	}

	@Test
	public void prefersWornWhenTheSameItemIsAlsoInInventory()
	{
		GearLoadout loadout = PlayerLoadouts.named(
			CombatStyle.MELEE,
			Map.of(EquipmentSlot.RING, "Berserker ring (i)"),
			List.of("Berserker ring (i)"));
		int[] grids = BankTabLayout.gridIndexes(loadout, List.of("Berserker ring (i)"));
		assertArrayEquals(new int[] {BankTabLayout.equipmentIndex(EquipmentSlot.RING)}, grids);
	}

	@Test
	public void matchesChargeVariantsToTheLoadoutSlot()
	{
		GearLoadout loadout = PlayerLoadouts.named(
			CombatStyle.MELEE,
			Map.of(),
			Arrays.asList("Prayer potion(4)", null, "Shark"));
		int[] grids = BankTabLayout.gridIndexes(loadout, List.of("Prayer potion(3)"));
		assertArrayEquals(new int[] {BankTabLayout.inventoryIndex(0)}, grids);
	}

	@Test
	public void sizesTheScrollAreaToTheLowestPlacedItem()
	{
		assertEquals(BankItemGrid.scrollHeight(35), BankTabLayout.scrollHeight(34));
		assertEquals(BankItemGrid.scrollHeight(57), BankTabLayout.scrollHeight(56));
	}
}
