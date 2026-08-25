package com.slayeratlas.ui;

import com.slayeratlas.data.SlayerMonster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MonsterHeaderText
{
	private MonsterHeaderText()
	{
	}

	public static List<String> lines(SlayerMonster monster)
	{
		if (monster == null)
		{
			return Collections.emptyList();
		}
		List<String> lines = new ArrayList<>();
		if (monster.getSlayerLevel() > 0)
		{
			lines.add("Slayer level: " + monster.getSlayerLevel());
		}
		String combat = combatLevel(monster.getCombatLevelMin(), monster.getCombatLevelMax());
		if (!combat.isEmpty())
		{
			lines.add(combat);
		}
		String type = monster.getAttribute();
		if (type != null && !type.isEmpty())
		{
			lines.add("Type: " + type);
		}
		return lines;
	}

	private static String combatLevel(Integer min, Integer max)
	{
		if (min == null && max == null)
		{
			return "";
		}
		int low = min != null ? min : max;
		int high = max != null ? max : min;
		if (high < low)
		{
			int swap = low;
			low = high;
			high = swap;
		}
		StringBuilder builder = new StringBuilder("Combat level: ").append(low);
		if (high != low)
		{
			builder.append('-').append(high);
		}
		return builder.toString();
	}
}
