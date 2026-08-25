package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class DragonbaneGearTest
{
	@Test
	public void usesALanceOnDraconicMeleeTasks()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		assertTrue(DragonbaneGear.applies(dragons));
		GearLoadout melee = GearLoadouts.forMonster(dragons, List.of()).get(0);
		assertEquals("Dragon hunter lance", melee.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Amulet of rancour", melee.worn(EquipmentSlot.NECK).getName());
		assertEquals("Avernic treads (max)", melee.worn(EquipmentSlot.FEET).getName());
		assertNull(melee.worn(EquipmentSlot.SHIELD));
	}

	@Test
	public void pairsWyvernsWithALanceAndAncientWyvernShield()
	{
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		GearLoadout melee = GearLoadouts.forMonster(wyverns, List.of()).get(0);
		assertEquals("Dragon hunter lance", melee.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Ancient wyvern shield", melee.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void pairsDragonHunterCrossbowWithADragonfireWard()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		GearLoadout ranged = loadoutFor(dragons, CombatStyle.RANGED);
		assertEquals("Dragon hunter crossbow", ranged.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Dragonfire ward", ranged.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void doesNotSwapWeaponsOnNonDragons()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		assertFalse(DragonbaneGear.applies(birds));
		assertEquals(
			"Osmumten's fang",
			GearLoadouts.forMonster(birds, List.of()).get(0).worn(EquipmentSlot.WEAPON).getName());
	}

	private static GearLoadout loadoutFor(SlayerMonster monster, CombatStyle style)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			if (loadout.getStyle() == style)
			{
				return loadout;
			}
		}
		throw new AssertionError("No " + style + " loadout");
	}
}
