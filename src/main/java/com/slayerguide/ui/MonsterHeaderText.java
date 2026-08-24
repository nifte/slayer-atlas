package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
import java.util.Locale;

public final class MonsterHeaderText
{
	private MonsterHeaderText()
	{
	}

	public static String meta(SlayerMonster monster)
	{
		if (monster == null)
		{
			return "";
		}
		StringBuilder builder = new StringBuilder();
		appendCombatLevels(builder, monster.getCombatLevelMin(), monster.getCombatLevelMax());
		String type = monster.getAttribute();
		if (type != null && !type.isEmpty())
		{
			if (builder.length() > 0)
			{
				builder.append(' ');
			}
			builder.append(type.toLowerCase(Locale.ROOT));
		}
		return builder.toString();
	}

	private static void appendCombatLevels(StringBuilder builder, Integer min, Integer max)
	{
		if (min == null && max == null)
		{
			return;
		}
		int low = min != null ? min : max;
		int high = max != null ? max : min;
		if (high < low)
		{
			int swap = low;
			low = high;
			high = swap;
		}
		builder.append("lvl ").append(low);
		if (high != low)
		{
			builder.append('-').append(high);
		}
	}
}
