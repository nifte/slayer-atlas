package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class TaskBankLoadout
{
	private TaskBankLoadout()
	{
	}

	public static GearLoadout resolve(
		SlayerMonster monster,
		LoadoutSelection selection,
		TaskLoadouts taskLoadouts,
		GearRecommendation recommendation)
	{
		if (monster == null)
		{
			return null;
		}
		GearRecommendation rec = recommendation == null ? GearRecommendation.specialized() : recommendation;
		if (selection != null)
		{
			GearLoadout remembered = selection.loadout(monster.getId());
			if (remembered != null)
			{
				if (selection.saved(monster.getId()))
				{
					return remembered;
				}
				return withOwnedDisplay(remembered, rec);
			}
		}
		GearLoadout saved = taskLoadouts == null ? null : taskLoadouts.load(monster.getId());
		if (saved != null)
		{
			return saved;
		}
		List<GearLoadout> loadouts = GearLoadouts.forMonster(monster, List.of(), rec);
		return loadouts.isEmpty() ? null : loadouts.get(0);
	}

	private static GearLoadout withOwnedDisplay(GearLoadout loadout, GearRecommendation recommendation)
	{
		OwnedItems owned = recommendation == null ? null : recommendation.owned();
		if (loadout == null || owned == null || !recommendation.filterToOwned())
		{
			return loadout;
		}
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (!slot.onWornGrid())
			{
				continue;
			}
			GearItem item = loadout.worn(slot);
			if (item != null)
			{
				worn.put(slot, owned.shownAs(item));
			}
		}
		List<GearItem> inventory = new ArrayList<>();
		if (loadout.getInventory() != null)
		{
			for (GearItem item : loadout.getInventory())
			{
				inventory.add(item == null ? null : owned.shownAs(item));
			}
		}
		return new GearLoadout(loadout.getStyle(), loadout.isPrimary(), worn, inventory, loadout.getPrayers());
	}
}
