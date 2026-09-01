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
		return InventoryLoadouts.filled(groupTogether(collapse(inventory, preserveSlots)), recommendation);
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

	static List<GearItem> groupTogether(List<GearItem> inventory)
	{
		List<GearItem> tools = new ArrayList<>();
		List<GearItem> potions = new ArrayList<>();
		List<GearItem> food = new ArrayList<>();
		if (inventory != null)
		{
			for (GearItem item : inventory)
			{
				if (item == null)
				{
					continue;
				}
				if (isFood(item))
				{
					food.add(item);
				}
				else if (item.getName() != null && isPotionLike(item.getName().toLowerCase(Locale.ROOT)))
				{
					potions.add(item);
				}
				else
				{
					tools.add(item);
				}
			}
		}
		List<GearItem> grouped = new ArrayList<>();
		grouped.addAll(groupCopies(tools));
		grouped.addAll(groupCopies(potions));
		grouped.addAll(groupCopies(food));
		return grouped;
	}

	private static List<GearItem> groupCopies(List<GearItem> items)
	{
		List<GearItem> grouped = new ArrayList<>();
		boolean[] used = new boolean[items.size()];
		for (int index = 0; index < items.size(); index++)
		{
			if (used[index])
			{
				continue;
			}
			GearItem item = items.get(index);
			grouped.add(item);
			used[index] = true;
			for (int later = index + 1; later < items.size(); later++)
			{
				if (!used[later] && sameItem(item, items.get(later)))
				{
					grouped.add(items.get(later));
					used[later] = true;
				}
			}
		}
		return grouped;
	}

	private static boolean sameItem(GearItem left, GearItem right)
	{
		return left != null && right != null && left.getName() != null && right.getName() != null
			&& OwnedItemNames.matches(left.getName(), right.getName());
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
