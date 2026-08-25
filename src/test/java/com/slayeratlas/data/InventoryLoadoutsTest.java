package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class InventoryLoadoutsTest
{
	@Test
	public void fillsAMeleeInventoryWithDivinePotsAndBisFood()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<GearItem> items = GearLoadouts.forMonster(birds, List.of()).get(0).getInventory();
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertEquals("Divine super combat potion(4)", items.get(0).getName());
		assertEquals(4, count(items, "Prayer potion(4)"));
		assertEquals(0, count(items, "Super restore(4)"));
		assertEquals(0, count(items, "Goading potion(4)"));
		assertTrue(count(items, InventoryLoadouts.FOOD) > 1);
		assertEquals(0, count(items, "Shark"));
	}

	@Test
	public void usesImbuedHeartInsteadOfMagicPotions()
	{
		SlayerMonster dust = new Gson().fromJson(
			"{\"name\":\"Dust devils\",\"recommendedStyle\":\"Magic\",\"recommendedPotions\":[\"Forgotten brew or magic potion\"]}",
			SlayerMonster.class);
		List<GearItem> items = GearLoadouts.forMonster(dust, List.of()).get(0).getInventory();
		assertEquals("Imbued heart", items.get(0).getName());
		assertEquals(1, count(items, "Divine rune pouch"));
		assertEquals(0, count(items, "Forgotten brew(4)"));
		assertEquals(0, count(items, "Magic potion(4)"));
		assertEquals(0, count(items, "Super restore(4)"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void usesDivineBastionInsteadOfRangingPotions()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		List<GearItem> items = inventoryFor(dragons, CombatStyle.RANGED);
		assertEquals(1, count(items, "Divine bastion potion(4)"));
		assertEquals(0, count(items, "Divine ranging potion(4)"));
	}

	@Test
	public void includesADivineRunePouchOnMagicSetups()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> magic = inventoryFor(dust, CombatStyle.MAGIC);
		assertEquals(1, count(magic, "Divine rune pouch"));
		assertEquals(0, count(inventoryFor(dust, CombatStyle.MELEE), "Divine rune pouch"));
	}

	@Test
	public void includesGoadingPotionsOnBurstableMagicTasks()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> items = inventoryFor(dust, CombatStyle.MAGIC);
		assertEquals(2, count(items, "Goading potion(4)"));
		assertEquals("Imbued heart", items.get(0).getName());
	}

	@Test
	public void includesSuperRestoreWhenTheMonsterDrainsStats()
	{
		SlayerMonster spectres = new MonsterDatabase(new Gson()).findByTaskName("Aberrant spectres");
		List<GearItem> items = GearLoadouts.forMonster(spectres, List.of()).get(0).getInventory();
		assertEquals(2, count(items, "Super restore(4)"));
	}

	@Test
	public void includesSuperRestoreWithSaradominBrews()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"name\":\"Test\",\"recommendedPotions\":[\"Saradomin brew\"]}",
			SlayerMonster.class);
		List<GearItem> items = InventoryLoadouts.forMonster(CombatStyle.MELEE, monster, List.of());
		assertEquals(1, count(items, "Saradomin brew(4)"));
		assertEquals(2, count(items, "Super restore(4)"));
	}

	@Test
	public void fillsWyvernInventoryWithoutWornGearOrBrokenPotionNames()
	{
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		List<GearItem> extras = List.of(
			GearItem.named("Avernic defender"),
			GearItem.named("Divine super combat"));
		List<GearItem> items = InventoryLoadouts.forMonster(CombatStyle.MELEE, wyverns, extras);
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertEquals(0, count(items, "Avernic defender"));
		assertEquals(0, count(items, "Divine super combat"));
		assertEquals(1, count(items, "Divine super combat potion(4)"));
		for (GearItem item : items)
		{
			assertTrue(item != null && item.getName() != null && !item.getName().isEmpty());
		}
	}

	@Test
	public void fillsEverySlotWhenExtrasHaveGaps()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<GearItem> extras = new java.util.ArrayList<>();
		extras.add(null);
		extras.add(GearItem.named("N/A"));
		extras.add(GearItem.named("Divine ranging potion(4)"));
		List<GearItem> items = InventoryLoadouts.forMonster(CombatStyle.RANGED, birds, extras);
		assertEquals(InventoryLoadouts.SIZE, items.size());
		for (GearItem item : items)
		{
			assertTrue(item != null && item.getName() != null && !item.getName().isEmpty());
		}
		assertEquals(1, count(items, "Divine bastion potion(4)"));
		assertEquals(0, count(items, "Divine ranging potion(4)"));
	}

	@Test
	public void includesDragonAntifireInAFullInventory()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		List<GearItem> items = GearLoadouts.forMonster(dragons, List.of()).get(0).getInventory();
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertTrue(count(items, "Extended super antifire(4)") == 1);
		assertFalse(names(items).contains("Shark"));
	}

	private static List<GearItem> inventoryFor(SlayerMonster monster, CombatStyle style)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			if (loadout.getStyle() == style)
			{
				return loadout.getInventory();
			}
		}
		throw new AssertionError("No " + style + " loadout");
	}

	private static int count(List<GearItem> items, String name)
	{
		int total = 0;
		for (GearItem item : items)
		{
			if (name.equals(item.getName()))
			{
				total++;
			}
		}
		return total;
	}

	private static String names(List<GearItem> items)
	{
		StringBuilder text = new StringBuilder();
		for (GearItem item : items)
		{
			text.append(item.getName()).append(',');
		}
		return text.toString();
	}
}
