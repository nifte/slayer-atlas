package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class SpecialInventoryItemsTest
{
	@Test
	public void includesFishingExplosivesForKraken()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertFalse(names(database.findByTaskName("Cave kraken")).contains("Fishing explosive"));
		assertTrue(names(database.findNamedPage("Kraken")).contains("Fishing explosive"));
	}

	@Test
	public void includesFungicideForZygomites()
	{
		List<String> names = names(new MonsterDatabase(new Gson()).findByTaskName("Zygomites"));
		assertTrue(names.contains("Fungicide spray"));
		assertTrue(names.contains("Fungicide"));
	}

	@Test
	public void includesFinishersForOtherSlayerUniques()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertTrue(names(database.findByTaskName("Lizards")).contains("Ice cooler"));
		assertTrue(names(database.findByTaskName("Rockslugs")).contains("Bag of salt"));
		assertWornGraniteSkipsInventoryRockHammer(database.findByTaskName("Gargoyles"));
		assertTrue(names(database.findByTaskName("Mogres")).contains("Fishing explosive"));
		assertTrue(names(database.findByTaskName("Warped creatures")).contains("Crystal chime"));
	}

	@Test
	public void skipsRockHammerWhenTheLoadoutAlreadyHasAGargoyleFinisher()
	{
		SlayerMonster gargoyles = new MonsterDatabase(new Gson()).findByTaskName("Gargoyles");
		assertFalse(names(gargoyles, List.of(CrushWeapons.GRANITE)).contains("Rock hammer"));
		assertFalse(names(gargoyles, List.of(GearItem.named("Rock thrownhammer"))).contains("Rock hammer"));
		assertTrue(names(gargoyles, List.of(CrushWeapons.MACE)).contains("Rock hammer"));
		assertTrue(names(gargoyles).contains("Rock hammer"));
	}

	@Test
	public void ignoresNegatedRequiredItemMentions()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertFalse(names(database.findNamedPage("Sea mogre")).contains("Fishing explosive"));
		assertFalse(names(database.findNamedPage("Sulphur Lizard")).contains("Ice cooler"));
	}

	private static void assertWornGraniteSkipsInventoryRockHammer(SlayerMonster monster)
	{
		GearLoadout loadout = GearLoadouts.forMonster(monster, List.of()).get(0);
		assertEquals("Granite hammer", loadout.worn(EquipmentSlot.WEAPON).getName());
		assertFalse(inventoryNames(loadout).contains("Rock hammer"));
	}

	private static String inventoryNames(GearLoadout loadout)
	{
		StringBuilder names = new StringBuilder();
		for (GearItem item : loadout.getInventory())
		{
			if (item != null && item.getName() != null)
			{
				names.append(item.getName()).append(',');
			}
		}
		return names.toString();
	}

	private static List<String> names(SlayerMonster monster)
	{
		return names(monster, List.of());
	}

	private static List<String> names(SlayerMonster monster, List<GearItem> alreadyHave)
	{
		java.util.ArrayList<String> names = new java.util.ArrayList<>();
		for (GearItem item : SpecialInventoryItems.forMonster(monster, alreadyHave))
		{
			names.add(item.getName());
		}
		return names;
	}
}
