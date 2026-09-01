package com.slayeratlas.data;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class LoadoutItemNames
{
	private LoadoutItemNames()
	{
	}

	public static List<String> of(GearLoadout loadout)
	{
		Set<String> names = new LinkedHashSet<>();
		if (loadout == null)
		{
			return List.of();
		}
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (!slot.onWornGrid())
			{
				continue;
			}
			add(names, loadout.worn(slot));
		}
		if (loadout.getInventory() != null)
		{
			for (GearItem item : loadout.getInventory())
			{
				add(names, item);
			}
		}
		return List.copyOf(names);
	}

	private static void add(Set<String> names, GearItem item)
	{
		if (item == null || item.getName() == null || item.getName().isEmpty())
		{
			return;
		}
		names.add(item.getName());
	}
}
