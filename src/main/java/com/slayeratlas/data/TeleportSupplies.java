package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class TeleportSupplies
{
	public static final GearItem HOUSE_TABLET = GearItem.named("Teleport to house (tablet)");

	public static final List<GearItem> PERMANENT = List.of(
		GearItem.named("Max cape"),
		GearItem.named("Construction cape"),
		GearItem.named("Crafting cape"),
		GearItem.named("Sailor's amulet"));

	public static final List<GearItem> TABLETS = List.of(
		HOUSE_TABLET,
		GearItem.named("Varrock teleport"),
		GearItem.named("Falador teleport"),
		GearItem.named("Lumbridge teleport"),
		GearItem.named("Camelot teleport"),
		GearItem.named("Ardougne teleport"));

	private TeleportSupplies()
	{
	}

	public static void strip(List<GearItem> items, boolean preserveSlots)
	{
		if (items == null)
		{
			return;
		}
		if (preserveSlots)
		{
			for (int index = 0; index < items.size(); index++)
			{
				if (isTeleport(items.get(index)))
				{
					items.set(index, null);
				}
			}
			return;
		}
		items.removeIf(TeleportSupplies::isTeleport);
	}

	public static boolean isTeleport(GearItem item)
	{
		return item != null && isTeleport(item.getName());
	}

	public static boolean isTeleport(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		if (isPermanent(name) || isHouseTeleportName(name))
		{
			return true;
		}
		for (GearItem item : TABLETS)
		{
			if (item != null && OwnedItemNames.matches(name, item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isPermanent(String name)
	{
		if (isPlainMaxCape(name) || isSkillcape(name, "construction") || isSkillcape(name, "crafting")
			|| isSailorAmulet(name))
		{
			return true;
		}
		for (GearItem item : PERMANENT)
		{
			if (item != null && OwnedItemNames.matches(name, item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isPlainMaxCape(String name)
	{
		return OwnedItemNames.normalize(name).equals("max cape");
	}

	private static boolean isSkillcape(String name, String skill)
	{
		return OwnedItemNames.normalize(name).equals(skill + " cape");
	}

	private static boolean isSailorAmulet(String name)
	{
		String folded = OwnedItemNames.normalize(name).replace("'", "");
		return folded.equals("sailors amulet")
			|| folded.equals("sailor amulet")
			|| folded.equals("amulet of the sailor");
	}

	private static boolean isHouseTeleportName(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.contains("teleport") && lower.contains("house");
	}
}
