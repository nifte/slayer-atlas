package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class BankTabTitleTest
{
	private MonsterDatabase database;
	private SlayerMonster demons;
	private SlayerMonster kraken;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
		demons = database.findByTaskName("Black demons");
		kraken = database.findByTaskName("Cave kraken");
	}

	@Test
	public void namesAMultiStyleMonsterWithTheActiveLoadout()
	{
		assertEquals(
			"Black demons (Melee)",
			BankTabTitle.of(demons, new LoadoutSelection(), TaskLoadouts.none(), GearRecommendation.specialized()));
	}

	@Test
	public void usesTheSelectedStyleWhenSeveralLoadoutsExist()
	{
		LoadoutSelection selection = new LoadoutSelection();
		selection.set(
			demons.getId(),
			CombatStyle.RANGED,
			false,
			PlayerLoadouts.named(CombatStyle.RANGED, java.util.Map.of(), List.of()));
		assertEquals(
			"Black demons (Ranged)",
			BankTabTitle.of(demons, selection, TaskLoadouts.none(), GearRecommendation.specialized()));
	}

	@Test
	public void usesSavedWhenTheSavedLoadoutIsActive()
	{
		LoadoutSelection selection = new LoadoutSelection();
		GearLoadout saved = PlayerLoadouts.named(CombatStyle.MELEE, java.util.Map.of(), List.of("Shark"));
		TaskLoadouts loadouts = TaskLoadouts.memory(saved);
		loadouts.save(demons.getId(), saved);
		selection.set(demons.getId(), CombatStyle.MELEE, true, saved);
		assertEquals(
			"Black demons (Saved)",
			BankTabTitle.of(demons, selection, loadouts, GearRecommendation.specialized()));
	}

	@Test
	public void omitsTheLoadoutWhenOnlyOneIsAvailable()
	{
		assertEquals(
			"Cave kraken",
			BankTabTitle.of(kraken, new LoadoutSelection(), TaskLoadouts.none(), GearRecommendation.specialized()));
	}

	@Test
	public void namesASingleStyleMonsterWhenASavedLoadoutAddsASecondOption()
	{
		GearLoadout saved = PlayerLoadouts.named(CombatStyle.MAGIC, java.util.Map.of(), List.of("Shark"));
		TaskLoadouts loadouts = TaskLoadouts.memory(saved);
		loadouts.save(kraken.getId(), saved);
		assertEquals(
			"Cave kraken (Saved)",
			BankTabTitle.of(kraken, new LoadoutSelection(), loadouts, GearRecommendation.specialized()));
	}

	@Test
	public void fallsBackToSlayerAtlasWithoutAMonster()
	{
		assertEquals(
			"Slayer Atlas",
			BankTabTitle.of(null, new LoadoutSelection(), TaskLoadouts.none(), GearRecommendation.specialized()));
	}
}
