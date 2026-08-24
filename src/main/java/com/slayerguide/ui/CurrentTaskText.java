package com.slayerguide.ui;

import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.SlayerMonster;

public final class CurrentTaskText
{
	private CurrentTaskText()
	{
	}

	public static String label(CurrentSlayerTask task, SlayerMonster monster)
	{
		if (task == null || !task.hasTask())
		{
			return "Current Task: none";
		}
		String name = task.getName();
		if (monster != null && monster.getName() != null && !monster.getName().isEmpty())
		{
			name = monster.getName();
		}
		return "Current Task: " + task.getRemaining() + " " + name;
	}
}
