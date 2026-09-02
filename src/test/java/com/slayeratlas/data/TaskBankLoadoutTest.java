package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void rewritesARememberedLoadoutToTheLastEquippedVariant()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		GearLoadout remembered = PlayerLoadouts.named(
			CombatStyle.MELEE,
			java.util.Map.of(EquipmentSlot.HEAD, "Slayer helmet (i)"),
			List.of());
		LoadoutSelection selection = new LoadoutSelection();
		selection.set(birds.getId(), CombatStyle.MELEE, false, remembered);
		OwnedItems owned = OwnedItems.withBank(
			java.util.Set.of("Black slayer helmet (i)", "Twisted slayer helmet (i)"),
			java.util.Map.of(OwnedItemNames.familyKey("Black slayer helmet (i)"), "Black slayer helmet (i)"));
		GearLoadout loadout = TaskBankLoadout.resolve(
			birds,
			selection,
			TaskLoadouts.none(),
			GearRecommendation.of(true, owned));
		assertEquals("Black slayer helmet (i)", loadout.worn(EquipmentSlot.HEAD).getName());
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(loadout);
		assertTrue(matcher.matches("Black slayer helmet (i)"));
		assertFalse(matcher.matches("Twisted slayer helmet (i)"));
	}

	@Test
	public void showsOwnedVariantsInTheBankWhenOnlyOwnedIsOff()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		GearLoadout remembered = PlayerLoadouts.named(
			CombatStyle.MELEE,
			java.util.Map.of(EquipmentSlot.WEAPON, "Osmumten's fang"),
			List.of("Ring of suffering (i)"));
		LoadoutSelection selection = new LoadoutSelection();
		selection.set(birds.getId(), CombatStyle.MELEE, false, remembered);
		OwnedItems owned = OwnedItems.withBank(java.util.Set.of(
			"Osmumten's fang (or)",
			"Ring of suffering (ri)"));
		GearLoadout loadout = TaskBankLoadout.resolve(
			birds,
			selection,
			TaskLoadouts.none(),
			GearRecommendation.of(false, owned));
		assertEquals("Osmumten's fang (or)", loadout.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Ring of suffering (ri)", loadout.getInventory().get(0).getName());
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(loadout);
		assertTrue(matcher.matches("Osmumten's fang (or)"));
		assertTrue(matcher.matches("Ring of suffering (ri)"));
	}

	@Test
	public void doesNotRewriteVariantsUntilABankSnapshotExists()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		GearLoadout remembered = PlayerLoadouts.named(
			CombatStyle.MELEE,
			java.util.Map.of(EquipmentSlot.WEAPON, "Osmumten's fang"),
			List.of());
		LoadoutSelection selection = new LoadoutSelection();
		selection.set(birds.getId(), CombatStyle.MELEE, false, remembered);
		GearLoadout loadout = TaskBankLoadout.resolve(
			birds,
			selection,
			TaskLoadouts.none(),
			GearRecommendation.of(false, OwnedItems.withoutBank(java.util.Set.of("Osmumten's fang (or)"))));
		assertSame(remembered, loadout);
	}

	@Test
	public void doesNotRewriteASavedLoadoutToAnotherVariant()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		GearLoadout saved = PlayerLoadouts.named(
			CombatStyle.MELEE,
			java.util.Map.of(EquipmentSlot.HEAD, "Twisted slayer helmet (i)"),
			List.of());
		LoadoutSelection selection = new LoadoutSelection();
		selection.set(birds.getId(), CombatStyle.MELEE, true, saved);
		OwnedItems owned = OwnedItems.withBank(
			java.util.Set.of("Black slayer helmet (i)", "Twisted slayer helmet (i)"),
			java.util.Map.of(OwnedItemNames.familyKey("Black slayer helmet (i)"), "Black slayer helmet (i)"));
		GearLoadout loadout = TaskBankLoadout.resolve(
			birds,
			selection,
			TaskLoadouts.none(),
			GearRecommendation.of(true, owned));
		assertSame(saved, loadout);
	}
}
