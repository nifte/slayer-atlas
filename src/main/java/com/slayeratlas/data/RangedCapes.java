package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RangedCapes
{
	public static final GearItem BLESSED_QUIVER = GearItem.named("Blessed dizana's quiver");
	public static final GearItem QUIVER = GearItem.named("Dizana's quiver");
	public static final GearItem ASSEMBLER = GearItem.named("Ava's assembler");
	public static final GearItem ACCUMULATOR = GearItem.named("Ava's accumulator");
	public static final GearItem ATTRACTOR = GearItem.named("Ava's attractor");

	private static final List<GearItem> LADDER = List.of(
		BLESSED_QUIVER,
		QUIVER,
		ASSEMBLER,
		ACCUMULATOR,
		ATTRACTOR);

	private RangedCapes()
	{
	}

	public static RankedGearLoadout promote(RankedGearLoadout ranked)
	{
		if (ranked == null || ranked.getStyle() != CombatStyle.RANGED)
		{
			return ranked;
		}
		List<GearItem> capes = ranked.ranks(EquipmentSlot.CAPE);
		List<GearItem> ordered = rank(capes);
		if (ordered.equals(capes))
		{
			return ranked;
		}
		Map<EquipmentSlot, List<GearItem>> updated = new EnumMap<>(EquipmentSlot.class);
		updated.putAll(ranked.getRanks());
		updated.put(EquipmentSlot.CAPE, ordered);
		return ranked.withRanks(updated);
	}

	static List<GearItem> rank(List<GearItem> capes)
	{
		List<GearItem> source = capes == null ? List.of() : capes;
		List<GearItem> ranked = new ArrayList<>();
		for (GearItem preferred : LADDER)
		{
			GearItem existing = find(source, preferred.getName());
			ranked.add(existing != null ? existing : preferred);
		}
		for (GearItem item : source)
		{
			if (item != null && find(ranked, item.getName()) == null)
			{
				ranked.add(item);
			}
		}
		return ranked;
	}

	private static GearItem find(List<GearItem> items, String name)
	{
		for (GearItem item : items)
		{
			if (item != null && item.getName() != null && OwnedItemNames.matches(item.getName(), name))
			{
				return item;
			}
		}
		return null;
	}
}
