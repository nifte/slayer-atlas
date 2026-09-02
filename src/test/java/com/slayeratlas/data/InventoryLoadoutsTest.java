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
		assertEquals(1, count(items, "Saturated heart"));
		assertEquals(0, count(items, "Imbued heart"));
		assertEquals("Saturated heart", items.get(items.size() - 2).getName());
		assertEquals("Divine rune pouch", items.get(items.size() - 1).getName());
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
	public void omitsGoadingPotionsOnBurstableMagicTasksByDefault()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> items = inventoryFor(dust, CombatStyle.MAGIC);
		assertEquals(0, count(items, "Goading potion(4)"));
	}

	@Test
	public void includesGoadingPotionsOnBurstableMagicTasks()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> items = inventoryFor(dust, CombatStyle.MAGIC, withGoading());
		assertEquals(2, count(items, "Goading potion(4)"));
		assertEquals("Goading potion(4)", items.get(0).getName());
		assertEquals(1, count(items, "Saturated heart"));
		assertEquals(1, count(items, "Divine rune pouch"));
	}

	@Test
	public void omitsGoadingPotionsOnMeleeEvenWhenTheSettingIsOn()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> items = inventoryFor(dust, CombatStyle.MELEE, withGoading());
		assertEquals(0, count(items, "Goading potion(4)"));
	}

	@Test
	public void replacesWikiStackingItemsWithGoadingOnABurstGrid()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> wiki = burstWikiGrid();
		List<GearItem> off = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			dust,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		assertEquals(1, count(off, "Venator bow"));
		assertEquals(1, count(off, "Dragon dart"));
		assertEquals(0, count(off, "Goading potion(4)"));
		List<GearItem> on = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			dust,
			List.of(),
			wiki,
			withGoading());
		assertEquals(0, count(on, "Venator bow"));
		assertEquals(0, count(on, "Dragon dart"));
		assertEquals(2, count(on, "Goading potion(4)"));
	}

	@Test
	public void keepsWikiStackingItemsWhenGoadingIsEnabledButUnowned()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MAGIC,
			dust,
			List.of(),
			List.of(GearItem.named("Venator bow"), GearItem.named("Dragon dart")),
			GearRecommendation.of(true, true, OwnedItems.withBank(Set.of(
				"Venator bow",
				"Dragon dart",
				"Saturated heart",
				"Divine rune pouch",
				"Prayer potion"))));
		assertEquals(0, count(items, "Goading potion(4)"));
		assertEquals(1, count(items, "Venator bow"));
		assertEquals(1, count(items, "Dragon dart"));
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
	public void includesAntipoisonOnCaveSlimes()
	{
		SlayerMonster slimes = new MonsterDatabase(new Gson()).findByTaskName("Cave slimes");
		List<GearItem> items = GearLoadouts.forMonster(slimes, List.of()).get(0).getInventory();
		assertEquals(2, count(items, "Anti-venom+(4)"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void putsCrystalChimeInAFullWarpedWikiGrid()
	{
		SlayerMonster warped = new MonsterDatabase(new Gson()).findByTaskName("Warped creatures");
		List<GearItem> wiki = new java.util.ArrayList<>();
		wiki.add(GearItem.named("Super combat potion(4)"));
		while (wiki.size() < InventoryLoadouts.SIZE)
		{
			wiki.add(GearItem.named("Shark"));
		}
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			warped,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		assertEquals(1, count(items, "Crystal chime"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void putsAntipoisonInAFullCaveSlimeWikiGrid()
	{
		SlayerMonster slimes = new MonsterDatabase(new Gson()).findByTaskName("Cave slimes");
		List<GearItem> wiki = new java.util.ArrayList<>();
		wiki.add(GearItem.named("Super combat potion(4)"));
		wiki.add(GearItem.named("Super combat potion(4)"));
		while (wiki.size() < InventoryLoadouts.SIZE - 1)
		{
			wiki.add(GearItem.named("Prayer potion(4)"));
		}
		wiki.add(GearItem.named("Herb sack"));
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			slimes,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		assertEquals(2, count(items, "Anti-venom+(4)"));
		assertEquals(1, count(items, "Herb sack"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
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
	public void neverRecommendsPartialPotionDosesFromWikiInventory()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		List<GearItem> wiki = new java.util.ArrayList<>();
		wiki.add(GearItem.named("Dragon claws"));
		wiki.add(GearItem.named("Extended super antifire(4)"));
		wiki.add(GearItem.named("Extended super antifire(4)"));
		wiki.add(GearItem.named("Extended super antifire(2)"));
		wiki.add(GearItem.named("Super restore mix(1)"));
		wiki.add(GearItem.named("Prayer potion(3)"));
		while (wiki.size() < 16)
		{
			wiki.add(GearItem.named("Karambwan"));
		}
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.RANGED,
			dragons,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		assertEquals(0, count(items, "Extended super antifire(2)"));
		assertEquals(3, count(items, "Extended super antifire(4)"));
		assertEquals(0, count(items, "Super restore mix(1)"));
		assertEquals(1, count(items, "Super restore mix(2)"));
		assertEquals(0, count(items, "Prayer potion(3)"));
		assertEquals(1, count(items, "Prayer potion(4)"));
	}

	@Test
	public void usesPrayerPotsInsteadOfFoodWhenProtectionFullyBlocks()
	{
		List<GearItem> dust = inventoryFor(
			new MonsterDatabase(new Gson()).findByTaskName("Dust devils"),
			CombatStyle.MAGIC);
		assertEquals(0, count(dust, InventoryLoadouts.FOOD));
		assertTrue(count(dust, "Prayer potion(4)") >= 14);
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
		assertEquals(0, count(kraken, "Fishing explosive"));
		assertEquals(1, count(kraken, "Saturated heart"));
		assertEquals(0, count(kraken, "Imbued heart"));
		assertEquals(InventoryLoadouts.SIZE, kraken.size());

		List<GearItem> zygomites = inventoryFor(database.findByTaskName("Zygomites"), CombatStyle.MELEE);
		assertEquals(1, count(zygomites, "Fungicide spray"));
		assertEquals(1, count(zygomites, "Fungicide"));
		assertEquals(3, GearLoadouts.forMonster(database.findByTaskName("Zygomites"), List.of()).size());

		List<GearItem> warped = inventoryFor(database.findByTaskName("Warped creatures"), CombatStyle.MELEE);
		assertEquals(1, count(warped, "Crystal chime"));
	}

	@Test
	public void skipsRockHammerWhenInventoryAlreadyHasAGargoyleFinisher()
	{
		SlayerMonster gargoyles = new MonsterDatabase(new Gson()).findByTaskName("Gargoyles");
		List<GearItem> withGranite = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			gargoyles,
			List.of(CrushWeapons.GRANITE));
		assertEquals(0, count(withGranite, "Rock hammer"));
		List<GearItem> withThrown = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			gargoyles,
			List.of(GearItem.named("Rock thrownhammer")));
		assertEquals(0, count(withThrown, "Rock hammer"));
		List<GearItem> withMace = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			gargoyles,
			List.of(CrushWeapons.MACE));
		assertEquals(1, count(withMace, "Rock hammer"));
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
	public void replacesWikiAnglerfishFillerWithMoonlightAntelope()
	{
		SlayerMonster araxxor = new MonsterDatabase(new Gson()).findNamedPage("Araxxor");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			araxxor,
			List.of(),
			araxxorWikiGrid(false),
			GearRecommendation.specialized());
		assertEquals(0, count(items, "Anglerfish"));
		assertEquals(0, count(items, InventoryLoadouts.COMBO_FOOD));
		assertTrue(count(items, InventoryLoadouts.FOOD) >= 16);
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void replacesWikiAnglerfishWithMarlinWhenKarambwansArePresent()
	{
		SlayerMonster araxxor = new MonsterDatabase(new Gson()).findNamedPage("Araxxor");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			araxxor,
			List.of(),
			araxxorWikiGrid(true),
			GearRecommendation.specialized());
		assertEquals(0, count(items, "Anglerfish"));
		assertEquals(0, count(items, InventoryLoadouts.FOOD));
		assertTrue(count(items, InventoryLoadouts.COMBO_FOOD) >= 8);
		assertTrue(count(items, "Cooked karambwan") >= 8);
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void keepsOwnedAnglerfishWhenFilteringToOwnedFood()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			birds,
			List.of(),
			List.of(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Anglerfish", "Prayer potion"))));
		assertEquals(0, count(items, InventoryLoadouts.FOOD));
		assertEquals(0, count(items, InventoryLoadouts.COMBO_FOOD));
		assertTrue(count(items, "Anglerfish") >= 1);
	}

	@Test
	public void usesMarlinWhenAShortWikiInventoryAlsoHasKarambwans()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		List<GearItem> wiki = List.of(
			GearItem.named("Cooked karambwan"),
			GearItem.named("Cooked karambwan"));
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			dragons,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		assertEquals(0, count(items, InventoryLoadouts.FOOD));
		assertEquals(0, count(items, "Anglerfish"));
		assertTrue(count(items, InventoryLoadouts.COMBO_FOOD) >= 4);
		assertEquals(2, count(items, "Cooked karambwan"));
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
		assertEquals("Sanfew serum(4)", items.get(0).getName());
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
		assertEquals(0, count(items, "Teleport to house"));
		assertEquals(0, count(items, "Teleport to house (tablet)"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void omitsWikiHouseTeleportsFromBothLoadouts()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<GearItem> wiki = List.of(GearItem.named("Teleport to house"));
		List<GearItem> defaults = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			birds,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		List<GearItem> owned = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			birds,
			List.of(),
			wiki,
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Teleport to house",
				"Construction cape (t)",
				"Sailor's amulet",
				"Shark",
				"Prayer potion"))));
		assertNoTeleport(defaults);
		assertNoTeleport(owned);
	}

	@Test
	public void keepsWikiSpecialsIncludingTheSlayerRing()
	{
		SlayerMonster gargoyles = new MonsterDatabase(new Gson()).findByTaskName("Gargoyles");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			gargoyles,
			List.of(),
			gargoyleWikiInventory(),
			GearRecommendation.specialized());
		assertNoTeleport(items);
		assertEquals(1, count(items, "Slayer ring"));
		assertTrue(count(items, "Rock hammer") >= 1);
		assertEquals(1, count(items, "Divine rune pouch"));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void showsAnOwnedEternalSlayerRingInsteadOfTheWikiRing()
	{
		SlayerMonster gargoyles = new MonsterDatabase(new Gson()).findByTaskName("Gargoyles");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			gargoyles,
			List.of(),
			gargoyleWikiInventory(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Slayer ring (eternal)",
				"Teleport to house",
				"Rock hammer",
				"Divine rune pouch",
				"Prayer potion",
				"Shark"))));
		assertEquals(1, count(items, "Slayer ring (eternal)"));
		assertEquals(0, count(items, "Slayer ring"));
		assertNoTeleport(items);
		assertTrue(count(items, "Rock hammer") >= 1);
		assertEquals(1, count(items, "Divine rune pouch"));
	}

	@Test
	public void omitsUnownedWikiSpecialsFromTheOwnedOnlyLoadout()
	{
		SlayerMonster gargoyles = new MonsterDatabase(new Gson()).findByTaskName("Gargoyles");
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			gargoyles,
			List.of(),
			gargoyleWikiInventory(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Construction cape (t)",
				"Rock hammer",
				"Rune pouch",
				"Prayer potion",
				"Shark"))));
		assertEquals(0, count(items, "Slayer ring"));
		assertEquals(0, count(items, "Slayer ring (eternal)"));
		assertNoTeleport(items);
		assertTrue(count(items, "Rock hammer") >= 1);
		assertEquals(1, count(items, "Rune pouch"));
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

	@Test
	public void includesACannonOnCaveHorrors()
	{
		SlayerMonster horrors = new MonsterDatabase(new Gson()).findByTaskName("Cave horrors");
		List<GearItem> items = GearLoadouts.forMonster(horrors, List.of()).get(0).getInventory();
		assertTrue(hasCannon(items));
		assertEquals(1, count(items, CannonSupplies.CANNONBALL));
		assertEquals(InventoryLoadouts.SIZE, items.size());
	}

	@Test
	public void omitsACannonWhenAListedLocationCannotUseOne()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertFalse(hasCannon(GearLoadouts.forMonster(database.findByTaskName("Hellhounds"), List.of())
			.get(0)
			.getInventory()));
		assertFalse(hasCannon(GearLoadouts.forMonster(database.findByTaskName("Elves"), List.of())
			.get(0)
			.getInventory()));
		assertFalse(hasCannon(GearLoadouts.forMonster(database.findByTaskName("Gargoyles"), List.of())
			.get(0)
			.getInventory()));
	}

	@Test
	public void keepsCannonballsWithTheCannonOnAFullWikiGrid()
	{
		SlayerMonster horrors = new MonsterDatabase(new Gson()).findByTaskName("Cave horrors");
		List<GearItem> wiki = new java.util.ArrayList<>();
		for (int index = 0; index < InventoryLoadouts.SIZE; index++)
		{
			wiki.add(GearItem.named("Prayer potion(4)"));
		}
		List<GearItem> items = InventoryLoadouts.forMonster(
			CombatStyle.MELEE,
			horrors,
			List.of(),
			wiki,
			GearRecommendation.specialized());
		assertTrue(hasCannon(items));
		assertEquals(1, count(items, CannonSupplies.CANNONBALL));
	}

	@Test
	public void omitsACannonOnInstancedBossAlternatives()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertFalse(hasCannon(inventoryFor(database.findNamedPage("Kraken"), CombatStyle.MAGIC)));
		assertFalse(hasCannon(GearLoadouts.forMonster(database.findNamedPage("Abyssal Sire"), List.of())
			.get(0)
			.getInventory()));
		assertFalse(hasCannon(GearLoadouts.forMonster(database.findNamedPage("Shellbane gryphon"), List.of())
			.get(0)
			.getInventory()));
	}

	private static boolean hasCannon(List<GearItem> items)
	{
		int pieces = 0;
		for (GearItem item : items)
		{
			if (item != null && CannonSupplies.isCannonItem(item.getName())
				&& !CannonSupplies.CANNONBALL.equals(item.getName()))
			{
				pieces++;
			}
		}
		return pieces == 4 && count(items, CannonSupplies.CANNONBALL) == 1;
	}

	private static List<GearItem> inventoryFor(SlayerMonster monster, CombatStyle style)
	{
		return inventoryFor(monster, style, GearRecommendation.specialized());
	}

	private static List<GearItem> inventoryFor(
		SlayerMonster monster,
		CombatStyle style,
		GearRecommendation recommendation)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of(), recommendation))
		{
			if (loadout.getStyle() == style)
			{
				return loadout.getInventory();
			}
		}
		throw new AssertionError("No " + style + " loadout");
	}

	private static GearRecommendation withGoading()
	{
		return GearRecommendation.of(false, true, OwnedItems.none());
	}

	private static List<GearItem> burstWikiGrid()
	{
		return WikiInventoryText.parse(
			"{{Inventory\n"
				+ "|Venator bow|Dragon dart|Prayer potion|Prayer potion\n"
				+ "|Prayer potion|Prayer potion|Prayer potion|Prayer potion\n"
				+ "|Prayer potion|Prayer potion|Prayer potion|Prayer potion\n"
				+ "|Prayer potion|Prayer potion|Prayer potion|Divine rune pouch\n"
				+ "}}");
	}

	private static List<GearItem> araxxorWikiGrid(boolean comboEat)
	{
		List<GearItem> wiki = new java.util.ArrayList<>();
		wiki.add(GearItem.named("Super combat potion(4)"));
		wiki.add(GearItem.named("Super combat potion(4)"));
		wiki.add(GearItem.named("Anti-venom+(4)"));
		wiki.add(GearItem.named("Anti-venom+(4)"));
		wiki.add(GearItem.named("Prayer potion(4)"));
		int karambwans = comboEat ? 8 : 0;
		for (int index = 0; index < karambwans; index++)
		{
			wiki.add(GearItem.named("Cooked karambwan"));
		}
		while (wiki.size() < InventoryLoadouts.SIZE)
		{
			wiki.add(GearItem.named("Anglerfish"));
		}
		return wiki;
	}

	private static List<GearItem> gargoyleWikiInventory()
	{
		return WikiInventoryText.parse(
			"{{Inventory|align=right\n"
				+ "|Divine super combat potion|Divine super combat potion|Prayer potion|Prayer potion\n"
				+ "|Prayer potion|Prayer potion|Prayer potion|Prayer potion\n"
				+ "|{{Cheap food}}|{{Cheap food}}|{{Cheap food}}\n"
				+ "|25=Teleport to house (tablet)|26=Slayer ring|27=Rock hammer|28=Divine rune pouch\n"
				+ "}}");
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

	private static void assertNoTeleport(List<GearItem> items)
	{
		assertEquals(0, count(items, "Teleport to house"));
		assertEquals(0, count(items, "Teleport to house (tablet)"));
		assertEquals(0, count(items, "Teleport to House"));
		assertEquals(0, count(items, "Construction cape"));
		assertEquals(0, count(items, "Construction cape (t)"));
		assertEquals(0, count(items, "Max cape"));
		assertEquals(0, count(items, "Crafting cape"));
		assertEquals(0, count(items, "Sailor's amulet"));
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
