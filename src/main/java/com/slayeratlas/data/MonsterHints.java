package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class MonsterHints
{
	private MonsterHints()
	{
	}

	public static boolean undead(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		if ("undead".equalsIgnoreCase(monster.getAttribute()))
		{
			return true;
		}
		return contains(blob(monster), "undead", "salve amulet");
	}

	public static boolean leafBladed(SlayerMonster monster)
	{
		return contains(blob(monster), "leaf-blad", "leaf blad", "broad bolt", "broad arrow", "magic dart");
	}

	public static boolean kalphite(SlayerMonster monster)
	{
		return contains(blob(monster), "kalphite", "scabarite");
	}

	public static boolean vampyre(SlayerMonster monster)
	{
		return contains(blob(monster), "vampyr", "vyre", "blisterwood", "hallowed flail");
	}

	public static boolean crush(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		String style = lower(monster.getRecommendedStyle());
		if (style.contains("crush"))
		{
			return true;
		}
		String weakness = lower(monster.getWeakness());
		return weakness.startsWith("crush") || weakness.contains("melee (crush)")
			|| weakness.contains("especially crush");
	}

	static String blob(SlayerMonster monster)
	{
		if (monster == null)
		{
			return "";
		}
		return lower(monster.getName())
			+ " "
			+ lower(monster.getAttribute())
			+ " "
			+ lower(monster.getWeakness())
			+ " "
			+ lower(monster.getRecommendedStyle())
			+ " "
			+ lower(monster.getNotes())
			+ " "
			+ join(monster.getRequiredItems())
			+ " "
			+ join(monster.getRecommendedEquipment());
	}

	private static boolean contains(String text, String... needles)
	{
		for (String needle : needles)
		{
			if (text.contains(needle))
			{
				return true;
			}
		}
		return false;
	}

	private static String join(List<String> values)
	{
		if (values == null || values.isEmpty())
		{
			return "";
		}
		return String.join(" ", values).toLowerCase(Locale.ROOT);
	}

	private static String lower(String value)
	{
		return value == null ? "" : value.toLowerCase(Locale.ROOT);
	}
}
