package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.EquipmentInventorySlot;
import org.junit.Test;

public class PlayerLoadoutsTest
{
	@Test
	public void mapsWornGameSlotsOntoTheEquipmentDoll()
	{
		assertEquals(EquipmentSlot.HEAD, PlayerLoadouts.fromGame(EquipmentInventorySlot.HEAD));
		assertEquals(EquipmentSlot.CAPE, PlayerLoadouts.fromGame(EquipmentInventorySlot.CAPE));
		assertEquals(EquipmentSlot.NECK, PlayerLoadouts.fromGame(EquipmentInventorySlot.AMULET));
		assertEquals(EquipmentSlot.WEAPON, PlayerLoadouts.fromGame(EquipmentInventorySlot.WEAPON));
		assertEquals(EquipmentSlot.BODY, PlayerLoadouts.fromGame(EquipmentInventorySlot.BODY));
		assertEquals(EquipmentSlot.SHIELD, PlayerLoadouts.fromGame(EquipmentInventorySlot.SHIELD));
		assertEquals(EquipmentSlot.LEGS, PlayerLoadouts.fromGame(EquipmentInventorySlot.LEGS));
		assertEquals(EquipmentSlot.HANDS, PlayerLoadouts.fromGame(EquipmentInventorySlot.GLOVES));
		assertEquals(EquipmentSlot.FEET, PlayerLoadouts.fromGame(EquipmentInventorySlot.BOOTS));
		assertEquals(EquipmentSlot.RING, PlayerLoadouts.fromGame(EquipmentInventorySlot.RING));
		assertEquals(EquipmentSlot.AMMO, PlayerLoadouts.fromGame(EquipmentInventorySlot.AMMO));
		assertNull(PlayerLoadouts.fromGame(null));
	}

	@Test
	public void buildsALoadoutFromWornAndInventoryNames()
	{
		Map<EquipmentSlot, String> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.WEAPON, "Bronze sword");
		worn.put(EquipmentSlot.SHIELD, "");
		List<String> inventory = new java.util.ArrayList<>();
		inventory.add("Trout");
		inventory.add(null);
		inventory.add("Prayer potion(4)");
		GearLoadout loadout = PlayerLoadouts.named(CombatStyle.MELEE, worn, inventory);
		assertEquals(CombatStyle.MELEE, loadout.getStyle());
		assertEquals("Bronze sword", loadout.worn(EquipmentSlot.WEAPON).getName());
		assertNull(loadout.worn(EquipmentSlot.SHIELD));
		assertEquals("Trout", loadout.getInventory().get(0).getName());
		assertNull(loadout.getInventory().get(1));
		assertEquals("Prayer potion(4)", loadout.getInventory().get(2).getName());
	}

	@Test
	public void defaultsMissingStyleToMelee()
	{
		assertEquals(CombatStyle.MELEE, PlayerLoadouts.named(null, Map.of(), List.of()).getStyle());
	}
}
