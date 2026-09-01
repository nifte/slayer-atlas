package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.SlayerMonster;
import org.junit.Test;

public class CurrentTaskTextTest
{
	@Test
	public void emptyWhenNoTask()
	{
		assertEquals("", CurrentTaskText.label(null, null));
		assertEquals("", CurrentTaskText.label(new CurrentSlayerTask(null, null, 0, 0), null));
		assertEquals("", CurrentTaskText.label(new CurrentSlayerTask("Skeletal Wyverns", null, 0, 31), null));
	}

	@Test
	public void appendsCurrentToTaskName()
	{
		CurrentSlayerTask task = new CurrentSlayerTask("skeletal wyverns", "Asgarnia", 31, 40);
		assertEquals("Skeletal Wyverns (Current)", CurrentTaskText.label(task, null));
	}

	@Test
	public void prefersCatalogMonsterName()
	{
		CurrentSlayerTask task = new CurrentSlayerTask("skeletal wyverns", null, 31, 31);
		SlayerMonster monster = new Gson().fromJson("{\"name\":\"Skeletal Wyverns\"}", SlayerMonster.class);
		assertEquals("Skeletal Wyverns (Current)", CurrentTaskText.label(task, monster));
	}
}
