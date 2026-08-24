package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;

public final class MonsterHeaderText
{
	private MonsterHeaderText()
	{
	}

	public static String meta(SlayerMonster monster)
	{
		StringBuilder builder = new StringBuilder("Slayer ");
		builder.append(monster.getSlayerLevel());
		if (monster.getCombatRequirement() != null)
		{
			builder.append(" · Combat ").append(monster.getCombatRequirement());
		}
		if (monster.getAttribute() != null && !monster.getAttribute().isEmpty())
		{
			builder.append(" · ").append(monster.getAttribute());
		}
		return builder.toString();
	}
}
