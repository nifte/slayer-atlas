package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;

public final class EquippedInventory
{
	private EquippedInventory()
	{
	}

	public static List<GearItem> withoutWorn(
		List<GearItem> inventory,
		GearLoadout loadout,
		GearRecommendation recommendation,
		boolean preserveSlots)
	{
		return InventoryLoadouts.filled(
			strip(inventory, wornItems(loadout), preserveSlots),
			recommendation);
	}

	static List<GearItem> strip(List<GearItem> inventory, List<GearItem> worn, boolean preserveSlots)
	{
		List<GearItem> result = new ArrayList<>();
		if (inventory == null)
		{
			return result;
		}
		for (GearItem item : inventory)
		{
			if (isWorn(item, worn))
			{
				if (preserveSlots)
				{
					result.add(null);
				}
				continue;
			}
			result.add(item);
		}
		return result;
	}

	static boolean isWorn(GearItem item, List<GearItem> worn)
	{
		if (item == null || item.getName() == null || item.getName().isEmpty() || worn == null)
		{
			return false;
		}
		for (GearItem equipped : worn)
		{
			if (equipped != null && equipped.getName() != null
				&& OwnedItemNames.matches(item.getName(), equipped.getName()))
			{
				return true;
			}
		}
		return false;
	}

	static List<GearItem> wornItems(GearLoadout loadout)
	{
		List<GearItem> worn = new ArrayList<>();
		if (loadout == null)
		{
			return worn;
		}
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (!slot.onWornGrid())
			{
				continue;
			}
			GearItem item = loadout.worn(slot);
			if (item != null)
			{
				worn.add(item);
			}
		}
		return worn;
	}
}
