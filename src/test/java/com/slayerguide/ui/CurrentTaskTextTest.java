package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.SlayerMonster;
import org.junit.Test;

public class CurrentTaskTextTest
{
	@Test
	public void noneWhenNoTask()
	{
		assertEquals("Current Task: none", CurrentTaskText.label(null, null));
		assertEquals("Current Task: none", CurrentTaskText.label(new CurrentSlayerTask(null, null, 0, 0), null));
		assertEquals("Current Task: none", CurrentTaskText.label(new CurrentSlayerTask("Skeletal Wyverns", null, 0, 31), null));
	}

	@Test
	public void includesRemainingAndTaskName()
	{
		CurrentSlayerTask task = new CurrentSlayerTask("skeletal wyverns", "Asgarnia", 31, 40);
		assertEquals("Current Task: 31 skeletal wyverns", CurrentTaskText.label(task, null));
	}

	@Test
	public void prefersCatalogMonsterName()
	{
		CurrentSlayerTask task = new CurrentSlayerTask("skeletal wyverns", null, 31, 31);
		SlayerMonster monster = new Gson().fromJson("{\"name\":\"Skeletal Wyverns\"}", SlayerMonster.class);
		assertEquals("Current Task: 31 Skeletal Wyverns", CurrentTaskText.label(task, monster));
	}
}
