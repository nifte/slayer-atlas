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
