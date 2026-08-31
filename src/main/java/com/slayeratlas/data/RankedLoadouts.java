package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RankedLoadouts
{
	private RankedLoadouts()
	{
	}

	public static RankedGearLoadout fromLoadout(GearLoadout loadout)
	{
		if (loadout == null)
		{
			return null;
		}
		Map<EquipmentSlot, List<GearItem>> ranks = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (!slot.onWornGrid())
			{
				continue;
			}
			GearItem item = loadout.worn(slot);
			if (item != null)
			{
				ranks.put(slot, List.of(item));
			}
		}
		return new RankedGearLoadout("", loadout.getStyle(), loadout.isPrimary(), ranks, loadout.getInventory());
	}

	public static RankedGearLoadout prependSpecials(RankedGearLoadout ranked, SlayerMonster monster)
	{
		if (ranked == null)
		{
			return null;
		}
		Map<EquipmentSlot, List<GearItem>> ranks = new EnumMap<>(EquipmentSlot.class);
		for (Map.Entry<EquipmentSlot, List<GearItem>> entry : ranked.getRanks().entrySet())
		{
			ranks.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		prepend(ranks, EquipmentSlot.HEAD, SlayerHelmet.IMBUED);
		prependAll(ranks, EquipmentSlot.WEAPON, specialWeapons(ranked.getStyle(), monster));
		if (ranked.getStyle() == CombatStyle.RANGED && DemonbaneGear.applies(monster))
		{
			prepend(ranks, EquipmentSlot.AMMO, DemonbaneGear.DRAGON_ARROW);
		}
		prependAll(ranks, EquipmentSlot.AMMO, LeafBladedGear.ammo(ranked.getStyle(), monster));
		prependAll(ranks, EquipmentSlot.NECK, UndeadGear.necks(monster));
		return ranked.withRanks(ranks);
	}

	private static List<GearItem> specialWeapons(CombatStyle style, SlayerMonster monster)
	{
		List<GearItem> weapons = new ArrayList<>();
		weapons.addAll(LeafBladedGear.ranks(style, monster));
		weapons.addAll(VampyreGear.ranks(style, monster));
		weapons.addAll(DemonbaneGear.ranks(style, monster));
		weapons.addAll(DragonbaneGear.ranks(style, monster));
		weapons.addAll(KalphiteGear.ranks(style, monster));
		weapons.addAll(CrushWeapons.ranks(style, monster));
		return weapons;
	}

	private static void prependAll(Map<EquipmentSlot, List<GearItem>> ranks, EquipmentSlot slot, List<GearItem> items)
	{
		if (items == null)
		{
			return;
		}
		for (int index = items.size() - 1; index >= 0; index--)
		{
			prepend(ranks, slot, items.get(index));
		}
	}

	private static void prepend(Map<EquipmentSlot, List<GearItem>> ranks, EquipmentSlot slot, GearItem item)
	{
		if (item == null)
		{
			return;
		}
		List<GearItem> current = ranks.computeIfAbsent(slot, key -> new ArrayList<>());
		List<GearItem> updated = new ArrayList<>();
		updated.add(item);
		for (GearItem existing : current)
		{
			if (existing != null && !item.getName().equalsIgnoreCase(existing.getName()))
			{
				updated.add(existing);
			}
		}
		ranks.put(slot, updated);
	}
}
