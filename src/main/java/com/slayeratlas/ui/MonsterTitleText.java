package com.slayeratlas.ui;

import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.TaskMatcher;

public final class MonsterTitleText
{
	private MonsterTitleText()
	{
	}

	public static String display(SlayerMonster monster, CurrentSlayerTask task)
	{
		String name = MonsterName.display(monster == null ? null : monster.getName());
		if (monster == null || task == null || !task.hasTask()
			|| !TaskMatcher.matchesMonster(task.getName(), monster))
		{
			return name;
		}
		int killed = Math.max(0, task.getInitialAmount() - task.getRemaining());
		return killed + "/" + task.getInitialAmount() + " " + name;
	}
}
