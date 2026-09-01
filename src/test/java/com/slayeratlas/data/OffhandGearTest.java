package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class OffhandGearTest
{
	@Test
	public void treatsBowsAndScythesAsTwoHanded()
	{
		assertTrue(OffhandGear.isTwoHanded("Venator bow"));
		assertTrue(OffhandGear.isTwoHanded("Bow of faerdhinen"));
		assertTrue(OffhandGear.isTwoHanded("Scorching bow"));
		assertTrue(OffhandGear.isTwoHanded("Hallowfell"));
		assertTrue(OffhandGear.isTwoHanded("Tumeken's shadow"));
		assertTrue(OffhandGear.isTwoHanded("Dragon hunter lance"));
	}

	@Test
	public void treatsRapiersCrossbowsAndWandsAsOneHanded()
	{
		assertFalse(OffhandGear.isTwoHanded("Ghrazi rapier"));
		assertFalse(OffhandGear.isTwoHanded("Osmumten's fang"));
		assertFalse(OffhandGear.isTwoHanded("Emberlight"));
		assertFalse(OffhandGear.isTwoHanded("Purging staff"));
		assertFalse(OffhandGear.isTwoHanded("Dragon hunter crossbow"));
		assertFalse(OffhandGear.isTwoHanded("Dragon hunter wand"));
		assertFalse(OffhandGear.isTwoHanded("Kodai wand"));
		assertFalse(OffhandGear.isTwoHanded("Eye of Ayak"));
		assertFalse(OffhandGear.isTwoHanded("Zaryte crossbow"));
	}

	@Test
	public void usesDragonfireOffhandsOnDraconicTasks()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		assertEquals("Dragonfire shield", OffhandGear.forMonster(CombatStyle.MELEE, dragons).getName());
		assertEquals("Dragonfire ward", OffhandGear.forMonster(CombatStyle.RANGED, dragons).getName());
		assertEquals("Ancient wyvern shield", OffhandGear.forMonster(CombatStyle.MAGIC, dragons).getName());
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		assertEquals("Ancient wyvern shield", OffhandGear.forMonster(CombatStyle.MELEE, wyverns).getName());
	}

	@Test
	public void keepsAWikiDefenderInsteadOfForcingDragonfire()
	{
		SlayerMonster frost = new MonsterDatabase(new Gson()).findByTaskName("Frost dragons");
		GearLoadout wiki = new GearLoadout(
			CombatStyle.MELEE,
			true,
			java.util.Map.of(EquipmentSlot.SHIELD, OffhandGear.MELEE),
			List.of());
		assertEquals("Avernic defender", OffhandGear.apply(wiki, frost).worn(EquipmentSlot.SHIELD).getName());
		assertTrue(OffhandGear.prefersWikiRanks(List.of(OffhandGear.MELEE, OffhandGear.DRAGONFIRE_SHIELD)));
		assertFalse(OffhandGear.prefersWikiRanks(List.of(OffhandGear.DRAGONFIRE_SHIELD, OffhandGear.MELEE)));
	}
}
