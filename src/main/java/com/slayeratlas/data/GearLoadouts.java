package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class GearLoadouts
{
	private GearLoadouts()
	{
	}

	public static List<GearLoadout> forMonster(SlayerMonster monster, List<GearLoadout> wikiLoadouts)
	{
		return forMonster(monster, ranked(wikiLoadouts), GearRecommendation.specialized());
	}

	public static List<GearLoadout> forMonster(
		SlayerMonster monster,
		List<RankedGearLoadout> wikiRanked,
		GearRecommendation recommendation)
	{
		return forMonster(monster, wikiRanked, recommendation, List.of());
	}

	public static List<GearLoadout> forMonster(
		SlayerMonster monster,
		List<RankedGearLoadout> wikiRanked,
		GearRecommendation recommendation,
		List<GearItem> sharedInventory)
	{
		GearRecommendation rec = recommendation == null ? GearRecommendation.specialized() : recommendation;
		Map<CombatStyle, RankedGearLoadout> byStyle = new EnumMap<>(CombatStyle.class);
		if (wikiRanked != null)
		{
			for (RankedGearLoadout loadout : wikiRanked)
			{
				if (loadout == null || loadout.getStyle() == null)
				{
					continue;
				}
				RankedGearLoadout existing = byStyle.get(loadout.getStyle());
				if (existing == null || loadout.isPrimary() && !existing.isPrimary())
				{
					byStyle.put(loadout.getStyle(), loadout);
				}
			}
		}
		List<CombatStyle> requested = new ArrayList<>(CombatStyles.eligible(monster));
		if (CombatStyles.blocksRanged(monster))
		{
			byStyle.remove(CombatStyle.RANGED);
		}
		if (byStyle.isEmpty())
		{
			for (CombatStyle style : requested)
			{
				byStyle.put(style, BisRanks.forStyle(style, monster, rec));
			}
		}
		else
		{
			for (CombatStyle style : requested)
			{
				byStyle.putIfAbsent(style, BisRanks.forStyle(style, monster, rec));
			}
		}
		List<GearLoadout> ordered = new ArrayList<>();
		for (CombatStyle style : CombatStyle.values())
		{
			RankedGearLoadout ranked = byStyle.get(style);
			if (ranked != null)
			{
				ordered.add(materialize(ranked, monster, rec, sharedInventory));
			}
		}
		return ordered;
	}

	private static List<RankedGearLoadout> ranked(List<GearLoadout> wikiLoadouts)
	{
		List<RankedGearLoadout> ranked = new ArrayList<>();
		if (wikiLoadouts == null)
		{
			return ranked;
		}
		for (GearLoadout loadout : wikiLoadouts)
		{
			RankedGearLoadout converted = RankedLoadouts.fromLoadout(loadout);
			if (converted != null)
			{
				ranked.add(converted);
			}
		}
		return ranked;
	}

	private static GearLoadout materialize(
		RankedGearLoadout ranked,
		SlayerMonster monster,
		GearRecommendation recommendation,
		List<GearItem> sharedInventory)
	{
		ranked = RangedCapes.promote(MeleeWeapons.promote(BisRanks.merge(ranked, monster, recommendation)));
		GearLoadout loadout;
		if (!recommendation.onlyOwned())
		{
			loadout = specialize(pick(ranked, recommendation), monster, recommendation);
		}
		else if (recommendation.filterToOwned())
		{
			loadout = pick(RankedLoadouts.prependSpecials(ranked, monster), recommendation);
		}
		else
		{
			loadout = complete(SlayerHelmet.apply(pick(ranked, recommendation)), monster, recommendation);
		}
		loadout = OffhandGear.withoutOffhandIfTwoHanded(loadout);
		loadout = RequiredGear.apply(loadout, monster);
		return withMonsterInventory(loadout, monster, ranked, recommendation, sharedInventory);
	}

	private static GearLoadout pick(RankedGearLoadout ranked, GearRecommendation recommendation)
	{
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (!slot.onWornGrid())
			{
				continue;
			}
			GearItem item = OwnedGearPicker.pick(
				ranked.ranks(slot),
				recommendation.owned(),
				recommendation.filterToOwned());
			if (item != null)
			{
				worn.put(slot, item);
			}
		}
		List<GearItem> extras = new ArrayList<>();
		GearItem special = OwnedGearPicker.pick(
			ranked.getSpecials(),
			recommendation.owned(),
			recommendation.filterToOwned());
		if (special != null)
		{
			extras.add(special);
		}
		return new GearLoadout(ranked.getStyle(), ranked.isPrimary(), worn, extras);
	}

	private static GearLoadout specialize(
		GearLoadout loadout,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		if (LeafBladedGear.applies(monster))
		{
			loadout = LeafBladedGear.apply(loadout, monster);
		}
		else if (VampyreGear.applies(monster))
		{
			loadout = VampyreGear.apply(loadout, monster);
		}
		else if (DemonbaneGear.applies(monster))
		{
			loadout = DemonbaneGear.apply(loadout, monster);
		}
		else if (DragonbaneGear.applies(monster))
		{
			loadout = DragonbaneGear.apply(loadout, monster);
		}
		else if (KalphiteGear.applies(monster))
		{
			loadout = KalphiteGear.apply(loadout, monster);
		}
		else if (CrushWeapons.applies(monster))
		{
			loadout = CrushWeapons.apply(loadout, monster);
		}
		loadout = UndeadGear.apply(loadout, monster);
		return complete(SlayerHelmet.apply(loadout), monster, recommendation);
	}

	private static GearLoadout complete(
		GearLoadout loadout,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		GearLoadout bis = BisLoadouts.forStyle(loadout.getStyle());
		loadout = OffhandGear.apply(loadout, monster, recommendation);
		loadout = fillMissing(loadout, bis);
		loadout = AmmoWeapons.apply(loadout, monster);
		return fillMissing(loadout, bis);
	}

	private static GearLoadout fillMissing(GearLoadout loadout, GearLoadout bis)
	{
		GearLoadout filled = loadout;
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (!slot.onWornGrid() || filled.worn(slot) != null || bis.worn(slot) == null)
			{
				continue;
			}
			if (slot == EquipmentSlot.SHIELD && OffhandGear.isTwoHanded(filled.worn(EquipmentSlot.WEAPON)))
			{
				continue;
			}
			filled = filled.withWorn(slot, bis.worn(slot));
		}
		return filled;
	}

	private static GearLoadout withMonsterInventory(
		GearLoadout loadout,
		SlayerMonster monster,
		RankedGearLoadout ranked,
		GearRecommendation recommendation,
		List<GearItem> sharedInventory)
	{
		List<GearItem> wikiInventory = ranked == null ? List.of() : ranked.getWikiInventory();
		if (wikiInventory.isEmpty() && sharedInventory != null)
		{
			wikiInventory = sharedInventory;
		}
		List<GearItem> inventory = InventoryLoadouts.forMonster(
			loadout.getStyle(),
			monster,
			loadout.getInventory(),
			wikiInventory,
			recommendation,
			loadout.worn(EquipmentSlot.SHIELD));
		boolean preserveSlots = InventoryLoadouts.isWikiGrid(wikiInventory) && !recommendation.filterToOwned();
		return loadout.withInventory(
			InventoryLoadouts.filled(
				UniqueInventory.withoutDuplicates(
					EquippedInventory.withoutWorn(inventory, loadout, recommendation, preserveSlots),
					recommendation,
					preserveSlots),
				recommendation));
	}
}
