package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
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
		assertEquals(2, count(items, "Divine super combat potion(4)"));
		assertEquals(1, count(items, InventoryLoadouts.FOOD));
		assertEquals(25, count(items, "Prayer potion(4)"));
		assertEquals(0, count(items, "Super restore(4)"));
		assertEquals(0, count(items, "Goading potion(4)"));
		assertEquals(0, count(items, "Shark"));
	}

	@Test
	public void usesSaturatedHeartInsteadOfMagicPotions()
	{
		SlayerMonster dust = new Gson().fromJson(
			"{\"name\":\"Dust devils\",\"recommendedStyle\":\"Magic\",\"recommendedPotions\":[\"Forgotten brew or magic potion\"]}",
			SlayerMonster.class);
		List<GearItem> items = GearLoadouts.forMonster(dust, List.of()).get(0).getInventory();
		assertEquals("Saturated heart", items.get(0).getName());
		assertEquals(0, count(items, "Imbued heart"));
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
		assertEquals(2, count(items, "Divine bastion potion(4)"));
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
		assertEquals("Saturated heart", items.get(0).getName());
	}

	@Test
	public void doesNotRecommendImbuedHeartWhenTheWikiAlreadyHasSaturatedHeart()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			dust,
			List.of(),
			List.of(GearItem.named("Saturated heart")),
			GearRecommendation.specialized());
		assertEquals(1, count(items, "Saturated heart"));
		assertEquals(0, count(items, "Imbued heart"));
	}

	@Test
	public void keepsSaturatedHeartInsteadOfImbuedHeartWhenBothAreOwned()
	{
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findNamedPage("Kraken");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			kraken,
			List.of(),
			krakenWikiGrid(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Fishing explosive",
				"Imbued heart",
				"Saturated heart",
				"Prayer potion(4)",
				"Shark"))));
		assertEquals(1, count(items, "Saturated heart"));
		assertEquals(0, count(items, "Imbued heart"));
	}

	@Test
	public void showsTheOwnedSaturatedHeartWhenTheWikiListsImbuedHeart()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			dust,
			List.of(),
			List.of(GearItem.named("Imbued heart")),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Saturated heart"))));
		assertEquals(1, count(items, "Saturated heart"));
		assertEquals(0, count(items, "Imbued heart"));
	}

	@Test
	public void recommendsSaturatedHeartWhenThatIsTheOwnedHeart()
	{
		SlayerMonster bears = new MonsterDatabase(new Gson()).findByTaskName("Bears");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			bears,
			List.of(),
			List.of(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Saturated heart"))));
		assertEquals(1, count(items, "Saturated heart"));
		assertEquals(0, count(items, "Imbued heart"));
	}

	@Test
	public void recommendsImbuedHeartOnlyWhenThatIsTheOwnedHeart()
	{
		SlayerMonster bears = new MonsterDatabase(new Gson()).findByTaskName("Bears");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			bears,
			List.of(),
			List.of(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Imbued heart"))));
		assertEquals(1, count(items, "Imbued heart"));
		assertEquals(0, count(items, "Saturated heart"));
	}

	@Test
	public void includesSuperRestoreWhenTheMonsterDrainsStats()
	{
		SlayerMonster spectres = new MonsterDatabase(new Gson()).findByTaskName("Aberrant spectres");
		List<GearItem> items = GearLoadouts.forMonster(spectres, List.of()).get(0).getInventory();
		assertEquals(4, count(items, "Super restore(4)"));
	}

	@Test
	public void includesSuperRestoreWithSaradominBrews()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"name\":\"Test\",\"recommendedPotions\":[\"Saradomin brew\"]}",
			SlayerMonster.class);
		List<GearItem> items = InventoryLoadouts.forMonster(CombatStyle.MELEE, monster, List.of());
		assertEquals(2, count(items, "Saradomin brew(4)"));
		assertEquals(4, count(items, "Super restore(4)"));
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
		assertEquals(2, count(items, "Divine super combat potion(4)"));
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
		assertEquals(2, count(items, "Divine bastion potion(4)"));
		assertEquals(0, count(items, "Divine ranging potion(4)"));
	}

	@Test
	public void addsAntifiresWhenADragonUsesAWikiDefender()
	{
		SlayerMonster dragon = new Gson().fromJson(
			"{\"name\":\"Test dragons\",\"attribute\":\"Draconic\"}",
			SlayerMonster.class);
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			dragon,
			List.of(),
			List.of(),
			GearRecommendation.specialized(),
			OffhandGear.MELEE);
		assertEquals(2, count(items, "Extended super antifire(4)"));
	}

	@Test
	public void includesDragonAntifireInAFullInventory()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		List<GearItem> items = GearLoadouts.forMonster(dragons, List.of()).get(0).getInventory();
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertEquals(2, count(items, "Extended super antifire(4)"));
		assertFalse(names(items).contains("Shark"));
	}

	@Test
	public void usesPrayerPotsInsteadOfFoodWhenProtectionFullyBlocks()
	{
		List<GearItem> dust = inventoryFor(
			new MonsterDatabase(new Gson()).findByTaskName("Dust devils"),
			CombatStyle.MAGIC);
		assertEquals(0, count(dust, InventoryLoadouts.FOOD));
		assertTrue(count(dust, "Prayer potion(4)") >= 20);
	}

	@Test
	public void packsFoodWhenDragonfireHitsThroughPrayer()
	{
		List<GearItem> dragons = GearLoadouts.forMonster(
			new MonsterDatabase(new Gson()).findByTaskName("Black dragons"),
			List.of()).get(0).getInventory();
		assertTrue(count(dragons, InventoryLoadouts.FOOD) >= 4);
		assertTrue(count(dragons, "Prayer potion(4)") >= 8);
	}

	@Test
	public void packsFoodOnTheKrakenBoss()
	{
		List<GearItem> kraken = inventoryFor(
			new MonsterDatabase(new Gson()).findNamedPage("Kraken"),
			CombatStyle.MAGIC);
		assertTrue(count(kraken, InventoryLoadouts.FOOD) >= 4);
		assertEquals(1, count(kraken, "Fishing explosive"));
	}

	@Test
	public void includesKrakenAndZygomiteSpecialsInInventory()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		List<GearItem> kraken = inventoryFor(database.findByTaskName("Cave kraken"), CombatStyle.MAGIC);
		assertEquals(1, count(kraken, "Fishing explosive"));
		assertEquals(1, count(kraken, "Saturated heart"));
		assertEquals(0, count(kraken, "Imbued heart"));
		assertEquals(InventoryLoadouts.SIZE, kraken.size());

		List<GearItem> zygomites = inventoryFor(database.findByTaskName("Zygomites"), CombatStyle.MELEE);
		assertEquals(1, count(zygomites, "Fungicide spray"));
		assertEquals(1, count(zygomites, "Fungicide"));
		assertEquals(3, GearLoadouts.forMonster(database.findByTaskName("Zygomites"), List.of()).size());
	}

	@Test
	public void usesAWikiInventoryGridInsteadOfGeneratedFill()
	{
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findNamedPage("Kraken");
		List<GearItem> wiki = krakenWikiGrid();
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			kraken,
			List.of(GearItem.named("Trident of the swamp")),
			wiki,
			GearRecommendation.specialized());
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertEquals("Fishing explosive", items.get(0).getName());
		assertEquals("Saturated heart", items.get(1).getName());
		assertEquals("Prayer potion(4)", items.get(7).getName());
		assertEquals("Divine rune pouch", items.get(27).getName());
		assertEquals(null, items.get(6));
		assertEquals(9, count(items, "Prayer potion(4)"));
		assertEquals(11, count(items, "Cooked sunlight antelope"));
		assertEquals(2, count(items, "Bracelet of slaughter"));
		assertEquals(0, count(items, "Trident of the swamp"));
		assertEquals(0, count(items, "Imbued heart"));
		assertEquals(0, count(items, InventoryLoadouts.FOOD));
		assertEquals(0, count(items, "Goading potion(4)"));
	}

	@Test
	public void dropsImbuedHeartFromAWikiGridThatAlsoHasSaturatedHeart()
	{
		List<GearItem> wiki = new java.util.ArrayList<>(krakenWikiGrid());
		wiki.set(6, GearItem.named("Imbued heart"));
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findNamedPage("Kraken");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			kraken,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		assertEquals(1, count(items, "Saturated heart"));
		assertEquals(0, count(items, "Imbued heart"));
		assertEquals(null, items.get(6));
	}

	@Test
	public void fillsOwnedGapsInAWikiInventoryGrid()
	{
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findNamedPage("Kraken");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			kraken,
			List.of(),
			krakenWikiGrid(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Fishing explosive",
				"Imbued heart",
				"Prayer potion(4)",
				"Shark"))));
		assertEquals(InventoryLoadouts.SIZE, items.size());
		assertEquals(0, nullCount(items));
		assertTrue(count(items, "Fishing explosive") >= 1);
		assertEquals(1, count(items, "Imbued heart"));
		assertEquals(0, count(items, "Saturated heart"));
		assertEquals(0, count(items, "Volatile Nightmare staff"));
		assertEquals(0, count(items, "Elidinis' ward (f)"));
		assertEquals(0, count(items, "Bracelet of slaughter"));
		assertTrue(count(items, "Prayer potion(4)") >= 1);
		assertTrue(count(items, "Shark") >= 1);
	}

	@Test
	public void usesWikiInventoryItemsThenFillsTheRest()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<GearItem> wiki = List.of(
			GearItem.named("Sanfew serum"),
			GearItem.named("Shark"));
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			birds,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		assertEquals("Sanfew serum", items.get(0).getName());
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void doesNotRecommendUnownedFoodOnceABankSnapshotExists()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			birds,
			List.of(),
			List.of(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Shark", "Prayer potion"))));
		assertEquals(0, count(items, InventoryLoadouts.FOOD));
		assertTrue(count(items, "Shark") >= 1);
		assertEquals(0, nullCount(items));
	}

	@Test
	public void skipsUnownedWikiInventoryItemsOnceABankSnapshotExists()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<GearItem> wiki = List.of(
			GearItem.named("Sanfew serum"),
			GearItem.named("Teleport to house"));
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			birds,
			List.of(),
			wiki,
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Teleport to house"))));
		assertEquals(0, count(items, "Sanfew serum"));
		assertEquals(1, count(items, "Teleport to house"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void padsExactSlotsWithoutDroppingHoles()
	{
		List<GearItem> items = new java.util.ArrayList<>();
		items.add(GearItem.named("Trout"));
		items.add(null);
		items.add(GearItem.named("Trout"));
		List<GearItem> slots = InventoryLoadouts.slots(items);
		assertEquals(InventoryLoadouts.SIZE, slots.size());
		assertEquals("Trout", slots.get(0).getName());
		assertNull(slots.get(1));
		assertEquals("Trout", slots.get(2).getName());
		assertEquals(InventoryLoadouts.SIZE - 2, nullCount(slots));
	}

	@Test
	public void usesOwnedOrnamentalWikiInventoryItems()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<GearItem> wiki = List.of(GearItem.named("Toxic blowpipe"));
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			birds,
			List.of(),
			wiki,
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Blazing blowpipe"))));
		assertEquals(1, count(items, "Blazing blowpipe"));
		assertEquals(0, count(items, "Toxic blowpipe"));
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

	private static List<GearItem> krakenWikiGrid()
	{
		return WikiInventoryText.parse(
			"{{Inventory\n"
				+ "|align = right\n"
				+ "|Fishing explosive\\200|Saturated heart|Bracelet of slaughter|Bracelet of slaughter\n"
				+ "|Volatile Nightmare staff|Elidinis' ward (f)||Prayer potion\n"
				+ "|Prayer potion|Prayer potion|Prayer potion|Prayer potion\n"
				+ "|Prayer potion|Prayer potion|Prayer potion|Prayer potion\n"
				+ "|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope\n"
				+ "|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope\n"
				+ "|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope|Divine rune pouch\n"
				+ "}}");
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

	private static int nullCount(List<GearItem> items)
	{
		int total = 0;
		for (GearItem item : items)
		{
			if (item == null)
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
