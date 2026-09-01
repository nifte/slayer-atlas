package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class TaskBankLoadoutTest
{
	@Test
	public void prefersTheRememberedSelectionForTheMonster()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		GearLoadout remembered = PlayerLoadouts.named(CombatStyle.RANGED, java.util.Map.of(), List.of("Trout"));
		LoadoutSelection selection = new LoadoutSelection();
		selection.set(birds.getId(), CombatStyle.RANGED, false, remembered);
		assertSame(remembered, TaskBankLoadout.resolve(birds, selection, TaskLoadouts.none(), GearRecommendation.specialized()));
	}

	@Test
	public void usesASavedLoadoutWhenNothingHasBeenSelectedYet()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		GearLoadout saved = PlayerLoadouts.named(CombatStyle.MELEE, java.util.Map.of(), List.of("Shark"));
		TaskLoadouts loadouts = TaskLoadouts.memory(saved);
		loadouts.save(birds.getId(), saved);
		assertSame(saved, TaskBankLoadout.resolve(birds, new LoadoutSelection(), loadouts, GearRecommendation.specialized()));
	}

	@Test
	public void fallsBackToTheFirstRecommendedStyle()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		GearLoadout loadout = TaskBankLoadout.resolve(
			birds,
			new LoadoutSelection(),
			TaskLoadouts.none(),
			GearRecommendation.specialized());
		assertNotNull(loadout);
		assertEquals(CombatStyle.MELEE, loadout.getStyle());
	}

	@Test
	public void returnsNothingWithoutAMonster()
	{
		assertNull(TaskBankLoadout.resolve(null, new LoadoutSelection(), TaskLoadouts.none(), GearRecommendation.specialized()));
	}
}
