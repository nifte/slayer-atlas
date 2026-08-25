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
		Map<CombatStyle, GearLoadout> byStyle = new EnumMap<>(CombatStyle.class);
		if (wikiLoadouts != null)
		{
			for (GearLoadout loadout : wikiLoadouts)
			{
				if (loadout == null || loadout.getStyle() == null)
				{
					continue;
				}
				GearLoadout existing = byStyle.get(loadout.getStyle());
				if (existing == null || loadout.isPrimary() && !existing.isPrimary())
				{
					byStyle.put(loadout.getStyle(), specialize(loadout, monster));
				}
			}
		}
		List<CombatStyle> requested = new ArrayList<>(
			CombatStyles.parse(monster == null ? null : monster.getRecommendedStyle()));
		if (requested.isEmpty())
		{
			requested.add(CombatStyle.MELEE);
		}
		if (byStyle.isEmpty())
		{
			for (CombatStyle style : requested)
			{
				byStyle.put(style, specialize(BisLoadouts.forStyle(style), monster));
			}
		}
		else
		{
			for (CombatStyle style : requested)
			{
				byStyle.putIfAbsent(style, specialize(BisLoadouts.forStyle(style), monster));
			}
		}
		List<GearLoadout> ordered = new ArrayList<>();
		for (CombatStyle style : CombatStyle.values())
		{
			GearLoadout loadout = byStyle.get(style);
			if (loadout != null)
			{
				ordered.add(loadout);
			}
		}
		return ordered;
	}

	private static GearLoadout specialize(GearLoadout loadout, SlayerMonster monster)
	{
		return withMonsterInventory(
			OffhandGear.apply(SlayerHelmet.apply(DragonbaneGear.apply(loadout, monster)), monster),
			monster);
	}

	private static GearLoadout withMonsterInventory(GearLoadout loadout, SlayerMonster monster)
	{
		return loadout.withInventory(
			InventoryLoadouts.forMonster(loadout.getStyle(), monster, loadout.getInventory()));
	}
}
