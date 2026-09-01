package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import org.junit.Test;

public class MonsterTitleTextTest
{
	@Test
	public void usesTheMonsterNameWhenThereIsNoMatchingTask()
	{
		SlayerMonster monster = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		assertEquals("Skeletal Wyverns", MonsterTitleText.display(monster, null));
		assertEquals("Skeletal Wyverns", MonsterTitleText.display(monster, new CurrentSlayerTask(null, null, 0, 0)));
		assertEquals(
			"Skeletal Wyverns",
			MonsterTitleText.display(monster, new CurrentSlayerTask("Abyssal demons", null, 40, 40)));
	}

	@Test
	public void prefixesKilledAndAssignedForTheCurrentTask()
	{
		SlayerMonster monster = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		CurrentSlayerTask task = new CurrentSlayerTask("skeletal wyverns", "Asgarnia", 31, 40);
		assertEquals("Skeletal Wyverns (9/40)", MonsterTitleText.display(monster, task));
	}

	@Test
	public void emptyWhenThereIsNoMonster()
	{
		assertEquals("", MonsterTitleText.display(null, new CurrentSlayerTask("Skeletal Wyverns", null, 31, 40)));
	}
}
