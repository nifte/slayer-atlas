package com.slayeratlas.data;

import java.util.Locale;

public enum CombatStyle
{
	MELEE("Melee"),
	RANGED("Ranged"),
	MAGIC("Magic");

	private final String displayName;

	CombatStyle(String displayName)
	{
		this.displayName = displayName;
	}

	public String displayName()
	{
		return displayName;
	}

	public static CombatStyle fromCaption(String caption)
	{
		if (caption == null || caption.isEmpty())
		{
			return null;
		}
		String lower = caption.toLowerCase(Locale.ROOT);
		int rangedAt = indexOfStyle(lower, "rang");
		int magicAt = firstIndex(lower, "magic", "mage", "burst", "barrage");
		int meleeAt = indexOfStyle(lower, "melee");
		int best = Integer.MAX_VALUE;
		CombatStyle match = null;
		if (rangedAt >= 0 && rangedAt < best)
		{
			best = rangedAt;
			match = RANGED;
		}
		if (magicAt >= 0 && magicAt < best)
		{
			best = magicAt;
			match = MAGIC;
		}
		if (meleeAt >= 0 && meleeAt < best)
		{
			match = MELEE;
		}
		return match;
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

	private static int indexOfStyle(String lower, String needle)
	{
		return lower.indexOf(needle);
	}
}
