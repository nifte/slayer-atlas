package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class EquippedInventoryTest
{
	@Test
	public void dropsAWardThatIsAlreadyEquippedInTheShieldSlot()
	{
		List<GearItem> inventory = List.of(
			GearItem.named("Dragon warhammer"),
			GearItem.named("Elidinis' ward (f)"),
			GearItem.named("Imbued heart"));
		List<GearItem> items = EquippedInventory.withoutWorn(
			inventory,
			magicLoadout("Tumeken's shadow", "Elidinis' ward (f)"),
			GearRecommendation.specialized(),
			false);
		assertEquals(0, count(items, "Elidinis' ward (f)"));
		assertEquals(1, count(items, "Dragon warhammer"));
		assertEquals(1, count(items, "Imbued heart"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void dropsAWeaponSwitchThatMatchesTheEquippedWeapon()
	{
		List<GearItem> inventory = List.of(
			GearItem.named("Volatile Nightmare staff"),
			GearItem.named("Dragon warhammer"));
		List<GearItem> items = EquippedInventory.withoutWorn(
			inventory,
			magicLoadout("Volatile Nightmare staff", "Elidinis' ward (f)"),
			GearRecommendation.specialized(),
			false);
		assertEquals(0, count(items, "Volatile Nightmare staff"));
		assertEquals(1, count(items, "Dragon warhammer"));
	}

	@Test
	public void keepsADifferentWeaponSwitchThanTheEquippedStaff()
	{
		List<GearItem> inventory = List.of(
			GearItem.named("Dragon warhammer"),
			GearItem.named("Volatile Nightmare staff"));
		List<GearItem> items = EquippedInventory.withoutWorn(
			inventory,
			magicLoadout("Tumeken's shadow", "Elidinis' ward (f)"),
			GearRecommendation.specialized(),
			false);
		assertEquals(1, count(items, "Dragon warhammer"));
		assertEquals(1, count(items, "Volatile Nightmare staff"));
	}

	@Test
	public void dropsChargedAndCosmeticVariantsOfTheEquippedPiece()
	{
		List<GearItem> inventory = List.of(
			GearItem.named("Sanguinesti staff (uncharged)"),
			GearItem.named("Dragon defender (t)"));
		List<GearItem> items = EquippedInventory.withoutWorn(
			inventory,
			loadout(CombatStyle.MELEE, "Sanguinesti staff", "Dragon defender"),
			GearRecommendation.specialized(),
			false);
		assertEquals(0, count(items, "Sanguinesti staff (uncharged)"));
		assertEquals(0, count(items, "Dragon defender (t)"));
	}

	@Test
	public void fillsTheHoleAfterStrippingAnEquippedWardFromAWikiGrid()
	{
		List<GearItem> inventory = new ArrayList<>();
		inventory.add(GearItem.named("Dragon warhammer"));
		inventory.add(GearItem.named("Elidinis' ward (f)"));
		inventory.add(GearItem.named("Prayer potion(4)"));
		while (inventory.size() < InventoryLoadouts.SIZE)
		{
			inventory.add(GearItem.named("Shark"));
		}
		List<GearItem> items = EquippedInventory.withoutWorn(
			inventory,
			magicLoadout("Tumeken's shadow", "Elidinis' ward (f)"),
			GearRecommendation.specialized(),
			true);
		assertEquals("Dragon warhammer", items.get(0).getName());
		assertEquals("Prayer potion(4)", items.get(1).getName());
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertEquals(0, count(items, "Elidinis' ward (f)"));
		assertTrue(count(items, "Shark") >= 1);
		assertNoEmptySlots(items);
	}

	private static GearLoadout magicLoadout(String weapon, String shield)
	{
		return loadout(CombatStyle.MAGIC, weapon, shield);
	}

	private static GearLoadout loadout(CombatStyle style, String weapon, String shield)
	{
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.WEAPON, GearItem.named(weapon));
		worn.put(EquipmentSlot.SHIELD, GearItem.named(shield));
		return new GearLoadout(style, true, worn, List.of());
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
}
