package com.slayeratlas.ui;

import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.SlayerMonster;

public final class CurrentTaskText
{
	private CurrentTaskText()
	{
	}

	public static String label(CurrentSlayerTask task, SlayerMonster monster)
	{
		if (task == null || !task.hasTask())
		{
			return "";
		}
		String name = task.getName();
		if (monster != null && monster.getName() != null && !monster.getName().isEmpty())
		{
			name = monster.getName();
		}
		int killed = Math.max(0, task.getInitialAmount() - task.getRemaining());
		return killed + "/" + task.getInitialAmount() + " " + MonsterName.display(name) + " (Current)";
	}
}
