package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
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
		assertEquals("Avernic defender", melee.worn(EquipmentSlot.SHIELD).getName());
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
	public void pairsDragonHunterCrossbowWithAStyleOffhandWhenSuperAntifireFullyProtects()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		GearLoadout ranged = loadoutFor(dragons, CombatStyle.RANGED);
		assertEquals("Dragon hunter crossbow", ranged.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Twisted buckler", ranged.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void doesNotSwapWeaponsOnNonDragons()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		assertFalse(DragonbaneGear.applies(birds));
		assertEquals(
			"Ghrazi rapier",
			GearLoadouts.forMonster(birds, List.of()).get(0).worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void keepsWikiFangWhenDragonHunterLanceIsNotOwned()
	{
		RankedGearLoadout ranked = fangWiki();
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		GearLoadout melee = GearLoadouts.forMonster(
			dragons,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Osmumten's fang"))))
			.get(0);
		assertEquals("Osmumten's fang", melee.worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void prefersOwnedDragonHunterLanceOverWikiFang()
	{
		RankedGearLoadout ranked = fangWiki();
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		GearLoadout melee = GearLoadouts.forMonster(
			dragons,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Dragon hunter lance"))))
			.get(0);
		assertEquals("Dragon hunter lance", melee.worn(EquipmentSlot.WEAPON).getName());
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

	private static RankedGearLoadout fangWiki()
	{
		String json = "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Osmumten's fang]]\"]"
			+ "}}";
		return WikiEquipmentTable.parse(new Gson(), "Black dragon/Strategies", json).toRanked();
	}
}
