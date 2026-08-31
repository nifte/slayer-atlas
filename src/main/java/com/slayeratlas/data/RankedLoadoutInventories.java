package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RankedLoadoutInventories
{
	private RankedLoadoutInventories()
	{
	}

	public static List<String> pageNames(List<RankedGearLoadout> ranked)
	{
		Set<String> pages = new LinkedHashSet<>();
		if (ranked == null)
		{
			return List.of();
		}
		for (RankedGearLoadout loadout : ranked)
		{
			if (loadout == null || loadout.getPageName() == null || loadout.getPageName().isEmpty())
			{
				continue;
			}
			pages.add(loadout.getPageName());
		}
		return new ArrayList<>(pages);
	}

	public static List<RankedGearLoadout> withPageInventories(
		List<RankedGearLoadout> ranked,
		Map<String, List<GearItem>> inventories)
	{
		if (ranked == null || ranked.isEmpty())
		{
			return List.of();
		}
		List<RankedGearLoadout> updated = new ArrayList<>();
		for (RankedGearLoadout loadout : ranked)
		{
			if (loadout == null)
			{
				continue;
			}
			List<GearItem> items = inventories == null ? null : inventories.get(loadout.getPageName());
			updated.add(items == null ? loadout : loadout.withWikiInventory(items));
		}
		return updated;
	}
}
