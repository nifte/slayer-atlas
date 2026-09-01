package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class PoisonSupplies
{
	private PoisonSupplies()
	{
	}

	public static boolean needsPotion(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		if (mentionsCure(join(monster.getRequiredItems())))
		{
			return true;
		}
		if (mentionsCure(join(monster.getRecommendedPotions()))
			&& !optionalOnly(join(monster.getRecommendedPotions())))
		{
			return true;
		}
		return mentionsCure(monster.getNotes()) && !optional(monster.getNotes());
	}

	static boolean mentionsCure(String text)
	{
		if (text == null || text.isEmpty())
		{
			return false;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		return lower.contains("antipoison")
			|| lower.contains("anti-venom")
			|| lower.contains("antivenom")
			|| lower.contains("antidote");
	}

	private static boolean optionalOnly(String text)
	{
		return mentionsCure(text) && optional(text);
	}

	private static boolean optional(String text)
	{
		return text != null && text.toLowerCase(Locale.ROOT).contains("optional");
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
