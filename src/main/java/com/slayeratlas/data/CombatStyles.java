package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public final class CombatStyles
{
	private CombatStyles()
	{
	}

	public static List<CombatStyle> parse(String text)
	{
		if (text == null || text.isEmpty())
		{
			return Collections.emptyList();
		}
		String lower = text.toLowerCase(Locale.ROOT);
		TreeMap<Integer, CombatStyle> found = new TreeMap<>();
		putIfMentioned(found, lower, "rang", CombatStyle.RANGED);
		putIfMentioned(found, firstIndex(lower, "magic", "mage", "burst", "barrage"), CombatStyle.MAGIC);
		putIfMentioned(found, lower, "melee", CombatStyle.MELEE);
		return new ArrayList<>(found.values());
	}

	public static List<CombatStyle> eligible(SlayerMonster monster)
	{
		if (monster == null)
		{
			return List.of(CombatStyle.MELEE);
		}
		List<CombatStyle> requested;
		if (isAnyStyle(monster.getWeakness()) || isAnyStyle(monster.getRecommendedStyle())
			|| MonsterHints.leafBladed(monster))
		{
			requested = List.of(CombatStyle.MELEE, CombatStyle.RANGED, CombatStyle.MAGIC);
		}
		else
		{
			CombatStyle only = onlyStyle(monster.getWeakness());
			if (only == null)
			{
				only = onlyStyle(monster.getRecommendedStyle());
			}
			if (only != null)
			{
				requested = List.of(only);
			}
			else
			{
				requested = parse(monster.getRecommendedStyle());
				if (requested.isEmpty())
				{
					requested = parse(monster.getWeakness());
				}
				if (requested.isEmpty())
				{
					requested = List.of(CombatStyle.MELEE);
				}
			}
		}
		return withoutBlockedStyles(monster, requested);
	}

	public static boolean blocksRanged(SlayerMonster monster)
	{
		return RequiredGear.cape(monster) != null;
	}

	static List<CombatStyle> withoutBlockedStyles(SlayerMonster monster, List<CombatStyle> styles)
	{
		if (styles == null || styles.isEmpty() || !blocksRanged(monster))
		{
			return styles;
		}
		List<CombatStyle> allowed = new ArrayList<>();
		for (CombatStyle style : styles)
		{
			if (style != CombatStyle.RANGED)
			{
				allowed.add(style);
			}
		}
		if (allowed.isEmpty())
		{
			return List.of(CombatStyle.MELEE);
		}
		return allowed;
	}

	private static boolean isAnyStyle(String text)
	{
		if (text == null || text.isEmpty())
		{
			return false;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		return lower.contains("any combat style")
			|| lower.contains("any style")
			|| lower.contains("all combat styles");
	}

	private static CombatStyle onlyStyle(String text)
	{
		if (text == null || text.isEmpty() || !text.toLowerCase(Locale.ROOT).contains("only"))
		{
			return null;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		if (lower.contains("magic dart") || lower.contains("leaf-blad") || lower.contains("broad"))
		{
			return null;
		}
		List<CombatStyle> found = parse(text);
		if (found.size() != 1)
		{
			return null;
		}
		return found.get(0);
	}

	private static void putIfMentioned(TreeMap<Integer, CombatStyle> found, String lower, String needle, CombatStyle style)
	{
		putIfMentioned(found, lower.indexOf(needle), style);
	}

	private static void putIfMentioned(TreeMap<Integer, CombatStyle> found, int index, CombatStyle style)
	{
		if (index >= 0)
		{
			found.putIfAbsent(index, style);
		}
	}

	private static int firstIndex(String lower, String... needles)
	{
		int best = Integer.MAX_VALUE;
		for (String needle : needles)
		{
			int index = lower.indexOf(needle);
			if (index >= 0 && index < best)
			{
				best = index;
			}
		}
		return best == Integer.MAX_VALUE ? -1 : best;
	}
}
