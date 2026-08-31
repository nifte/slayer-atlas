package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class MeleeWeapons
{
	public static final GearItem RAPIER = GearItem.named("Ghrazi rapier");
	public static final GearItem MACE = GearItem.named("Inquisitor's mace");
	public static final GearItem SAELDOR = GearItem.named("Blade of saeldor");
	public static final GearItem FANG = GearItem.named("Osmumten's fang");

	private static final List<GearItem> ABOVE_FANG = List.of(RAPIER, MACE, SAELDOR);

	private MeleeWeapons()
	{
	}

	public static RankedGearLoadout promote(RankedGearLoadout ranked)
	{
		if (ranked == null || ranked.getStyle() != CombatStyle.MELEE)
		{
			return ranked;
		}
		List<GearItem> weapons = ranked.ranks(EquipmentSlot.WEAPON);
		List<GearItem> ordered = demoteFang(weapons);
		if (ordered.equals(weapons))
		{
			return ranked;
		}
		Map<EquipmentSlot, List<GearItem>> updated = new EnumMap<>(EquipmentSlot.class);
		updated.putAll(ranked.getRanks());
		updated.put(EquipmentSlot.WEAPON, ordered);
		return ranked.withRanks(updated);
	}

	static List<GearItem> demoteFang(List<GearItem> weapons)
	{
		if (weapons == null || weapons.isEmpty() || !containsFang(weapons))
		{
			return weapons == null ? List.of() : weapons;
		}
		List<GearItem> better = betterThanFang(weapons);
		List<GearItem> ordered = new ArrayList<>();
		boolean placedFang = false;
		for (GearItem item : weapons)
		{
			if (isFang(item))
			{
				if (!placedFang)
				{
					ordered.addAll(better);
					ordered.add(item);
					placedFang = true;
				}
				continue;
			}
			if (isBetterThanFang(item))
			{
				continue;
			}
			ordered.add(item);
		}
		return ordered;
	}

	private static List<GearItem> betterThanFang(List<GearItem> weapons)
	{
		List<GearItem> better = new ArrayList<>();
		for (GearItem preferred : ABOVE_FANG)
		{
			GearItem existing = find(weapons, preferred.getName());
			better.add(existing != null ? existing : preferred);
		}
		return better;
	}

	private static boolean containsFang(List<GearItem> weapons)
	{
		for (GearItem item : weapons)
		{
			if (isFang(item))
			{
				return true;
			}
		}
		return false;
	}

	private static GearItem find(List<GearItem> weapons, String name)
	{
		for (GearItem item : weapons)
		{
			if (item != null && item.getName() != null && OwnedItemNames.matches(item.getName(), name))
			{
				return item;
			}
		}
		return null;
	}

	private static boolean isBetterThanFang(GearItem item)
	{
		for (GearItem preferred : ABOVE_FANG)
		{
			if (item != null && item.getName() != null
				&& OwnedItemNames.matches(item.getName(), preferred.getName()))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isFang(GearItem item)
	{
		return item != null && item.getName() != null
			&& item.getName().toLowerCase(Locale.ROOT).contains("osmumten's fang");
	}
}
