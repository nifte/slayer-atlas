package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class GoadingSupplies
{
	public static final String POTION = "Goading potion";
	public static final int COPIES = 2;

	private GoadingSupplies()
	{
	}

	public static boolean viable(CombatStyle style, SlayerMonster monster)
	{
		return style == CombatStyle.MAGIC && isBurstable(monster);
	}

	public static boolean include(CombatStyle style, SlayerMonster monster, GearRecommendation recommendation)
	{
		if (recommendation == null || !recommendation.useGoadingPotions() || !viable(style, monster))
		{
			return false;
		}
		return !recommendation.filterToOwned() || recommendation.owned().contains(GearItem.named(POTION));
	}

	public static boolean isStackingItem(GearItem item)
	{
		return item != null && isStackingItem(item.getName());
	}

	public static boolean isStackingItem(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		if (OwnedItemNames.matches(name, "Venator bow"))
		{
			return true;
		}
		String lower = name.toLowerCase(Locale.ROOT);
		if (lower.contains("chinchompa"))
		{
			return true;
		}
		return lower.contains("dart") && !lower.contains("blowpipe");
	}

	public static boolean isPotion(GearItem item)
	{
		return item != null && item.getName() != null
			&& item.getName().toLowerCase(Locale.ROOT).contains("goading");
	}

	private static boolean isBurstable(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		return mentionsBurst(monster.getRecommendedStyle())
			|| mentionsBurst(monster.getWeakness())
			|| mentionsBurst(monster.getNotes())
			|| mentionsBurst(join(monster.getRecommendedEquipment()));
	}

	private static boolean mentionsBurst(String text)
	{
		if (text == null || text.isEmpty())
		{
			return false;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		return lower.contains("burst") || lower.contains("barrage");
	}

	private static String join(List<String> values)
	{
		if (values == null || values.isEmpty())
		{
			return "";
		}
		return String.join(" ", values);
	}
}
