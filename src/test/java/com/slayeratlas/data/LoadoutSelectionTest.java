package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class LoadoutSelectionTest
{
	@Test
	public void remembersTheLastLoadoutPerMonster()
	{
		LoadoutSelection selection = new LoadoutSelection();
		GearLoadout melee = PlayerLoadouts.named(CombatStyle.MELEE, java.util.Map.of(), List.of("Shark"));
		GearLoadout ranged = PlayerLoadouts.named(CombatStyle.RANGED, java.util.Map.of(), List.of("Trout"));
		selection.set("birds", CombatStyle.MELEE, false, melee);
		selection.set("gargoyles", CombatStyle.RANGED, false, ranged);
		assertSame(melee, selection.loadout("birds"));
		assertEquals(CombatStyle.MELEE, selection.style("birds"));
		assertFalse(selection.saved("birds"));
		assertSame(ranged, selection.loadout("gargoyles"));
		assertNull(selection.loadout("unknown"));
	}

	@Test
	public void noneIgnoresUpdates()
	{
		LoadoutSelection selection = LoadoutSelection.none();
		selection.set("birds", CombatStyle.MELEE, true, PlayerLoadouts.named(CombatStyle.MELEE, java.util.Map.of(), List.of()));
		assertNull(selection.loadout("birds"));
		assertFalse(selection.saved("birds"));
	}

	@Test
	public void notifiesWhenTheSelectedLoadoutChanges()
	{
		LoadoutSelection selection = new LoadoutSelection();
		AtomicInteger changes = new AtomicInteger();
		selection.setOnChange(changes::incrementAndGet);
		selection.set("birds", CombatStyle.RANGED, false, PlayerLoadouts.named(CombatStyle.RANGED, java.util.Map.of(), List.of()));
		assertEquals(1, changes.get());
	}
}
