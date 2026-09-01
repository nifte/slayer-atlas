package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class LoadoutItemNamesTest
{
	@Test
	public void collectsWornAndInventoryNamesAndSkipsEmptySlots()
	{
		Map<EquipmentSlot, String> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.WEAPON, "Bronze sword");
		worn.put(EquipmentSlot.SHIELD, "");
		List<String> inventory = List.of("Trout", "", "Prayer potion(4)", "Trout");
		GearLoadout loadout = PlayerLoadouts.named(CombatStyle.MELEE, worn, inventory);
		assertEquals(List.of("Bronze sword", "Trout", "Prayer potion(4)"), LoadoutItemNames.of(loadout));
	}

	@Test
	public void returnsNothingWhenThereIsNoLoadout()
	{
		assertTrue(LoadoutItemNames.of(null).isEmpty());
	}
}
