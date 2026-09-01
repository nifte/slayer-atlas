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
		return InventoryLoadouts.filled(
			padFoodBeforeSpecial(groupTogether(collapse(inventory, preserveSlots)), recommendation),
			recommendation);
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
		List<GearItem> weapons = new ArrayList<>();
		List<GearItem> ammo = new ArrayList<>();
		List<GearItem> boosts = new ArrayList<>();
		List<GearItem> restores = new ArrayList<>();
		List<GearItem> food = new ArrayList<>();
		List<GearItem> special = new ArrayList<>();
		if (inventory != null)
		{
			for (GearItem item : inventory)
			{
				if (item == null)
				{
					continue;
				}
				switch (kind(item))
				{
					case WEAPON:
						weapons.add(item);
						break;
					case AMMO:
						ammo.add(item);
						break;
					case BOOST:
						boosts.add(item);
						break;
					case RESTORE:
						restores.add(item);
						break;
					case FOOD:
						food.add(item);
						break;
					case SPECIAL:
						special.add(item);
						break;
				}
			}
		}
		List<GearItem> grouped = new ArrayList<>();
		grouped.addAll(groupCopies(weapons));
		grouped.addAll(groupCopies(boosts));
		grouped.addAll(groupCopies(restores));
		grouped.addAll(groupCopies(food));
		grouped.addAll(groupCopies(ammo));
		grouped.addAll(groupCopies(special));
		return grouped;
	}

	private static List<GearItem> padFoodBeforeSpecial(List<GearItem> grouped, GearRecommendation recommendation)
	{
		List<GearItem> leading = new ArrayList<>();
		List<GearItem> trailing = new ArrayList<>();
		for (GearItem item : grouped)
		{
			Kind kind = kind(item);
			if (kind == Kind.AMMO || kind == Kind.SPECIAL)
			{
				trailing.add(item);
			}
			else
			{
				leading.add(item);
			}
		}
		GearItem food = paddingFood(leading, recommendation);
		int room = Math.max(0, InventoryLoadouts.SIZE - trailing.size());
		while (leading.size() > room && !leading.isEmpty())
		{
			int drop = lastFoodIndex(leading);
			leading.remove(drop < 0 ? leading.size() - 1 : drop);
		}
		while (leading.size() < room)
		{
			leading.add(food);
		}
		leading.addAll(trailing);
		if (leading.size() <= InventoryLoadouts.SIZE)
		{
			return leading;
		}
		return new ArrayList<>(leading.subList(0, InventoryLoadouts.SIZE));
	}

	private static GearItem paddingFood(List<GearItem> items, GearRecommendation recommendation)
	{
		for (GearItem item : items)
		{
			if (isFood(item))
			{
				return item;
			}
		}
		GearItem picked = OwnedSupplies.pick(OwnedSupplies.FOOD, recommendation);
		return picked != null ? picked : GearItem.named(InventoryLoadouts.FOOD);
	}

	private static int lastFoodIndex(List<GearItem> items)
	{
		for (int index = items.size() - 1; index >= 0; index--)
		{
			if (isFood(items.get(index)))
			{
				return index;
			}
		}
		return -1;
	}

	private enum Kind
	{
		WEAPON,
		AMMO,
		BOOST,
		RESTORE,
		FOOD,
		SPECIAL
	}

	private static Kind kind(GearItem item)
	{
		if (item == null || item.getName() == null || item.getName().isEmpty())
		{
			return Kind.SPECIAL;
		}
		if (isFood(item))
		{
			return Kind.FOOD;
		}
		String lower = item.getName().toLowerCase(Locale.ROOT);
		if (isHeart(lower))
		{
			return Kind.SPECIAL;
		}
		if (isPotionLike(lower))
		{
			return isRestorePotion(lower) ? Kind.RESTORE : Kind.BOOST;
		}
		if (isAmmo(lower))
		{
			return Kind.AMMO;
		}
		if (isWeapon(lower))
		{
			return Kind.WEAPON;
		}
		return Kind.SPECIAL;
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

	private static boolean isRestorePotion(String lower)
	{
		return lower.contains("prayer")
			|| lower.contains("restore")
			|| lower.contains("serum")
			|| lower.contains("saradomin brew")
			|| lower.contains("zamorak brew");
	}

	private static boolean isHeart(String lower)
	{
		return lower.endsWith(" heart");
	}

	private static boolean isAmmo(String lower)
	{
		return containsAny(lower, "bolt", "arrow", "javelin", "thrownaxe", "cannonball")
			|| (lower.contains("dart") && !lower.contains("blowpipe"))
			|| lower.contains("knife");
	}

	private static boolean isWeapon(String lower)
	{
		if (lower.contains("pickaxe") || lower.contains("rock hammer"))
		{
			return false;
		}
		return containsAny(
			lower,
			"warhammer",
			"claws",
			"blowpipe",
			"whip",
			"scimitar",
			"sword",
			"dagger",
			"rapier",
			"mace",
			"maul",
			"godsword",
			"scythe",
			"bow",
			"crossbow",
			"ballista",
			"staff",
			"trident",
			"wand",
			"halberd",
			"hasta",
			"spear",
			"flail",
			"bludgeon",
			"battleaxe",
			"greataxe",
			"hammer",
			"axe",
			"fang",
			"saeldor",
			"bulwark",
			"atlatl",
			"tentacle",
			"emberlight",
			"arclight",
			"darklight",
			"voidwaker",
			"keris",
			"lance",
			"chinchompa",
			"salamander",
			"colossal",
			"soulreaper",
			"macuahuitl",
			"shadow",
			"sanguinesti",
			"defender",
			"buckler",
			"ward",
			"shield",
			"tome",
			"blade");
	}

	private static boolean containsAny(String lower, String... needles)
	{
		for (String needle : needles)
		{
			if (lower.contains(needle))
			{
				return true;
			}
		}
		return false;
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
