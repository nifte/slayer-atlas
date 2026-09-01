package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class UniqueInventory
{
	private UniqueInventory()
	{
	}

	public static List<GearItem> withoutDuplicates(
		List<GearItem> inventory,
		GearRecommendation recommendation,
		boolean preserveSlots)
	{
		List<GearItem> collapsed = collapse(inventory, preserveSlots);
		if (preserveSlots)
		{
			return collapsed;
		}
		return InventoryLoadouts.filled(collapsed, recommendation);
	}

	static List<GearItem> collapse(List<GearItem> inventory, boolean preserveSlots)
	{
		List<GearItem> result = new ArrayList<>();
		if (inventory == null)
		{
			return result;
		}
		List<GearItem> kept = new ArrayList<>();
		for (GearItem item : inventory)
		{
			if (item == null)
			{
				if (preserveSlots)
				{
					result.add(null);
				}
				continue;
			}
			if (!allowsCopies(item) && alreadyKept(kept, item))
			{
				if (preserveSlots)
				{
					result.add(null);
				}
				continue;
			}
			kept.add(item);
			result.add(item);
		}
		return result;
	}

	static boolean allowsCopies(GearItem item)
	{
		if (item == null || item.getName() == null || item.getName().isEmpty())
		{
			return false;
		}
		String lower = item.getName().toLowerCase(Locale.ROOT);
		if (lower.startsWith("blighted "))
		{
			return allowsCopies(GearItem.named(item.getName().substring("blighted ".length()).trim()));
		}
		return isPotionLike(lower) || isFood(item) || isChargeJewelry(lower);
	}

	private static boolean alreadyKept(List<GearItem> kept, GearItem item)
	{
		if (item.getName() == null)
		{
			return false;
		}
		for (GearItem existing : kept)
		{
			if (existing != null && existing.getName() != null
				&& OwnedItemNames.matches(item.getName(), existing.getName()))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isPotionLike(String lower)
	{
		return lower.contains("potion")
			|| lower.contains("restore")
			|| lower.contains("brew")
			|| lower.contains("serum")
			|| lower.contains("antifire")
			|| lower.contains("venom")
			|| lower.contains("antidote")
			|| lower.contains("antipoison")
			|| lower.contains("stamina")
			|| lower.contains("waterskin");
	}

	private static boolean isFood(GearItem item)
	{
		for (GearItem food : OwnedSupplies.FOOD)
		{
			if (food != null && food.getName() != null && OwnedItemNames.matches(item.getName(), food.getName()))
			{
				return true;
			}
		}
		String lower = item.getName().toLowerCase(Locale.ROOT);
		return lower.contains("cooked") || lower.contains("karambwan");
	}

	private static boolean isChargeJewelry(String lower)
	{
		return lower.contains("bracelet of slaughter") || lower.contains("expeditious bracelet");
	}
}
