package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class RankedGearLoadoutTest
{
	@Test
	public void keepsEmptySlotsInAWikiInventoryGrid()
	{
		List<GearItem> wiki = new ArrayList<>();
		wiki.add(GearItem.named("Fishing explosive"));
		wiki.add(null);
		wiki.add(GearItem.named("Prayer potion"));
		RankedGearLoadout ranked = new RankedGearLoadout(
			"Kraken/Strategies",
			"Magic",
			CombatStyle.MAGIC,
			true,
			new EnumMap<>(EquipmentSlot.class),
			List.of(),
			wiki);
		assertEquals(3, ranked.getWikiInventory().size());
		assertEquals("Fishing explosive", ranked.getWikiInventory().get(0).getName());
		assertNull(ranked.getWikiInventory().get(1));
		assertEquals("Prayer potion", ranked.getWikiInventory().get(2).getName());
	}

	@Test
	public void attachingAWikiInventoryWithEmptySlotsDoesNotThrow()
	{
		RankedGearLoadout ranked = new RankedGearLoadout(
			"Kraken/Strategies",
			CombatStyle.MAGIC,
			true,
			new EnumMap<>(EquipmentSlot.class),
			List.of());
		List<GearItem> wiki = new ArrayList<>();
		wiki.add(GearItem.named("Fishing explosive"));
		wiki.add(null);
		wiki.add(GearItem.named("Saturated heart"));
		RankedGearLoadout updated = ranked.withWikiInventory(wiki);
		assertNull(updated.getWikiInventory().get(1));
		assertEquals("Saturated heart", updated.getWikiInventory().get(2).getName());
	}

	@Test
	public void materializesAWikiGridThatContainsAnEmptySlot()
	{
		List<GearItem> wiki = new ArrayList<>();
		wiki.add(GearItem.named("Fishing explosive"));
		wiki.add(null);
		wiki.add(GearItem.named("Prayer potion"));
		while (wiki.size() < InventoryLoadouts.SIZE)
		{
			wiki.add(GearItem.named("Cooked sunlight antelope"));
		}
		Map<EquipmentSlot, List<GearItem>> ranks = new EnumMap<>(EquipmentSlot.class);
		ranks.put(EquipmentSlot.WEAPON, List.of(GearItem.named("Trident of the swamp")));
		RankedGearLoadout ranked = new RankedGearLoadout(
			"Kraken/Strategies",
			"Magic",
			CombatStyle.MAGIC,
			true,
			ranks,
			List.of(),
			wiki);
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findNamedPage("Kraken");
		List<GearLoadout> loadouts = GearLoadouts.forMonster(
			kraken,
			List.of(ranked),
			GearRecommendation.specialized());
		assertEquals("Fishing explosive", loadouts.get(0).getInventory().get(0).getName());
		assertEquals("Prayer potion(4)", loadouts.get(0).getInventory().get(1).getName());
		assertEquals(InventoryLoadouts.SIZE, loadouts.get(0).getInventory().size());
		for (GearItem item : loadouts.get(0).getInventory())
		{
			assertNotNull(item);
			assertNotNull(item.getName());
		}
	}

	@Test
	public void fillsOwnedGapsWhenMaterializingAWikiInventoryGrid()
	{
		List<GearItem> wiki = new ArrayList<>();
		wiki.add(GearItem.named("Fishing explosive"));
		wiki.add(null);
		wiki.add(GearItem.named("Saturated heart"));
		while (wiki.size() < InventoryLoadouts.SIZE)
		{
			wiki.add(GearItem.named("Cooked sunlight antelope"));
		}
		Map<EquipmentSlot, List<GearItem>> ranks = new EnumMap<>(EquipmentSlot.class);
		ranks.put(EquipmentSlot.WEAPON, List.of(GearItem.named("Trident of the swamp")));
		RankedGearLoadout ranked = new RankedGearLoadout(
			"Kraken/Strategies",
			"Magic",
			CombatStyle.MAGIC,
			true,
			ranks,
			List.of(),
			wiki);
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findNamedPage("Kraken");
		List<GearItem> items = GearLoadouts.forMonster(
			kraken,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Fishing explosive", "Shark"))))
			.get(0)
			.getInventory();
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertEquals("Fishing explosive", items.get(0).getName());
		for (GearItem item : items)
		{
			assertNotNull(item);
			assertNotNull(item.getName());
		}
		assertEquals(0, count(items, "Saturated heart"));
		assertEquals(0, count(items, "Cooked sunlight antelope"));
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
