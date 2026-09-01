package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class SavedLoadoutStoreTest
{
	@Test
	public void savesAndLoadsALoadoutForOneMonster() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-loadouts");
		SavedLoadoutStore store = new SavedLoadoutStore(directory, new Gson());
		GearLoadout loadout = named("Bronze sword", "Trout", null, "Prayer potion(4)");
		store.save(111L, "birds", loadout);
		assertTrue(store.exists(111L, "birds"));
		GearLoadout loaded = store.load(111L, "birds");
		assertEquals(CombatStyle.MELEE, loaded.getStyle());
		assertEquals("Bronze sword", loaded.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Trout", loaded.getInventory().get(0).getName());
		assertNull(loaded.getInventory().get(1));
		assertEquals("Prayer potion(4)", loaded.getInventory().get(2).getName());
		assertEquals(List.of(), loaded.getPrayers());
	}

	@Test
	public void savesAndLoadsQuickPrayersWithTheLoadout() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-loadouts");
		SavedLoadoutStore store = new SavedLoadoutStore(directory, new Gson());
		GearLoadout loadout = PlayerLoadouts.named(
			CombatStyle.MELEE,
			Map.of(EquipmentSlot.WEAPON, "Bronze sword"),
			List.of("Trout"),
			List.of("Protect from Melee", "Piety"));
		store.save(111L, "birds", loadout);
		assertEquals(
			List.of("Protect from Melee", "Piety"),
			store.load(111L, "birds").getPrayers());
	}

	@Test
	public void doesNotShareLoadoutsAcrossAccountsOrMonsters() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-loadouts");
		SavedLoadoutStore store = new SavedLoadoutStore(directory, new Gson());
		store.save(111L, "birds", named("Bronze sword", "Trout"));
		assertNull(store.load(222L, "birds"));
		assertNull(store.load(111L, "abyssal_demons"));
		assertFalse(store.exists(222L, "birds"));
		assertEquals("Bronze sword", store.load(111L, "birds").worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void clearRemovesOnlyThatMonster() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-loadouts");
		SavedLoadoutStore store = new SavedLoadoutStore(directory, new Gson());
		store.save(111L, "birds", named("Bronze sword", "Trout"));
		store.save(111L, "abyssal_demons", named("Abyssal whip", "Shark"));
		store.clear(111L, "birds");
		assertFalse(store.exists(111L, "birds"));
		assertEquals("Abyssal whip", store.load(111L, "abyssal_demons").worn(EquipmentSlot.WEAPON).getName());
	}

	private static GearLoadout named(String weapon, String... inventory)
	{
		Map<EquipmentSlot, String> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.WEAPON, weapon);
		List<String> items = new ArrayList<>();
		if (inventory != null)
		{
			for (String name : inventory)
			{
				items.add(name);
			}
		}
		return PlayerLoadouts.named(CombatStyle.MELEE, worn, items);
	}
}
