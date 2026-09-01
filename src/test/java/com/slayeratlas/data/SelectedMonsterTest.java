package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.google.gson.Gson;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class SelectedMonsterTest
{
	@Test
	public void prefersTheViewedMonsterOverTheCurrentTask()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster task = database.findByTaskName("Dust devils");
		SlayerMonster viewed = database.findByTaskName("Abyssal demons");
		SelectedMonster selected = new SelectedMonster();
		assertSame(task, selected.or(task));
		selected.set(viewed);
		assertSame(viewed, selected.get());
		assertSame(viewed, selected.or(task));
	}

	@Test
	public void fallsBackToTheTaskWhenNothingIsViewed()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster task = database.findByTaskName("Dust devils");
		SelectedMonster selected = new SelectedMonster();
		selected.set(database.findByTaskName("Abyssal demons"));
		selected.set(null);
		assertNull(selected.get());
		assertSame(task, selected.or(task));
	}

	@Test
	public void noneIgnoresUpdates()
	{
		SelectedMonster selected = SelectedMonster.none();
		selected.set(new MonsterDatabase(new Gson()).findByTaskName("Dust devils"));
		assertNull(selected.get());
	}

	@Test
	public void notifiesWhenTheViewedMonsterChanges()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SelectedMonster selected = new SelectedMonster();
		AtomicInteger changes = new AtomicInteger();
		selected.setOnChange(changes::incrementAndGet);
		SlayerMonster dust = database.findByTaskName("Dust devils");
		selected.set(dust);
		selected.set(dust);
		selected.set(database.findByTaskName("Abyssal demons"));
		assertEquals(2, changes.get());
	}
}
