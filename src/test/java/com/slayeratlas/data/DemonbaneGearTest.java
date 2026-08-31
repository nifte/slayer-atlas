package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class DemonbaneGearTest
{
	@Test
	public void usesEmberlightOnDemonMeleeTasks()
	{
		SlayerMonster demons = new MonsterDatabase(new Gson()).findByTaskName("Black demons");
		assertTrue(DemonbaneGear.applies(demons));
		GearLoadout melee = GearLoadouts.forMonster(demons, List.of()).get(0);
		assertEquals("Emberlight", melee.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Avernic defender", melee.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void pairsScorchingBowWithDragonArrows()
	{
		SlayerMonster demons = new MonsterDatabase(new Gson()).findByTaskName("Black demons");
		GearLoadout ranged = loadoutFor(demons, CombatStyle.RANGED);
		assertEquals("Scorching bow", ranged.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Dragon arrow", ranged.worn(EquipmentSlot.AMMO).getName());
		assertEquals("Twisted buckler", ranged.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void usesPurgingStaffOnDemonMagic()
	{
		SlayerMonster nechs = new MonsterDatabase(new Gson()).findByTaskName("Nechryael");
		assertTrue(DemonbaneGear.applies(nechs));
		GearLoadout magic = loadoutFor(nechs, CombatStyle.MAGIC);
		assertEquals("Purging staff", magic.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Elidinis' ward (f)", magic.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void replacesWikiFangWithEmberlight()
	{
		String json = "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"head\":[\" [[Slayer helmet (i)]]\"],"
			+ "\"cape\":[\" [[Infernal cape]]\"],"
			+ "\"neck\":[\" [[Amulet of torture]]\"],"
			+ "\"ammo\":[\" [[Rada's blessing 4]]\"],"
			+ "\"weapon\":[\" [[Osmumten's fang]]\"],"
			+ "\"body\":[\" [[Torva platebody]]\"],"
			+ "\"shield\":[\" [[Avernic defender]]\"],"
			+ "\"legs\":[\" [[Torva platelegs]]\"],"
			+ "\"hands\":[\" [[Ferocious gloves]]\"],"
			+ "\"feet\":[\" [[Primordial boots]]\"],"
			+ "\"ring\":[\" [[Ultor ring]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Black demon/Strategies", json).toLoadout();
		SlayerMonster demons = new MonsterDatabase(new Gson()).findByTaskName("Black demons");
		GearLoadout melee = GearLoadouts.forMonster(demons, List.of(wiki)).get(0);
		assertEquals("Emberlight", melee.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Amulet of rancour", melee.worn(EquipmentSlot.NECK).getName());
		assertEquals("Avernic treads (max)", melee.worn(EquipmentSlot.FEET).getName());
		assertEquals("Avernic defender", melee.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void keepsWikiFangWhenEmberlightIsNotOwned()
	{
		RankedGearLoadout ranked = fangWiki();
		SlayerMonster demons = new MonsterDatabase(new Gson()).findByTaskName("Black demons");
		GearLoadout melee = GearLoadouts.forMonster(
			demons,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Osmumten's fang"))))
			.get(0);
		assertEquals("Osmumten's fang", melee.worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void prefersOwnedEmberlightOverWikiFang()
	{
		RankedGearLoadout ranked = fangWiki();
		SlayerMonster demons = new MonsterDatabase(new Gson()).findByTaskName("Black demons");
		GearLoadout melee = GearLoadouts.forMonster(
			demons,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Emberlight"))))
			.get(0);
		assertEquals("Emberlight", melee.worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void replacesWikiBoltsWithScorchingBow()
	{
		String json = "{"
			+ "\"style\":\"Ranged\","
			+ "\"Recommended Equipment\":{"
			+ "\"head\":[\" [[Slayer helmet (i)]]\"],"
			+ "\"cape\":[\" [[Ava's assembler]]\"],"
			+ "\"neck\":[\" [[Necklace of anguish]]\"],"
			+ "\"ammo\":[\" [[Ruby dragon bolts (e)]]\"],"
			+ "\"weapon\":[\" [[Bow of faerdhinen]]\"],"
			+ "\"body\":[\" [[Masori body (f)]]\"],"
			+ "\"legs\":[\" [[Masori chaps (f)]]\"],"
			+ "\"hands\":[\" [[Zaryte vambraces]]\"],"
			+ "\"feet\":[\" [[Pegasian boots]]\"],"
			+ "\"ring\":[\" [[Archers ring (i)]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Black demon/Strategies", json).toLoadout();
		SlayerMonster demons = new MonsterDatabase(new Gson()).findByTaskName("Black demons");
		GearLoadout ranged = loadoutFor(demons, CombatStyle.RANGED, List.of(wiki));
		assertEquals("Scorching bow", ranged.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Dragon arrow", ranged.worn(EquipmentSlot.AMMO).getName());
	}

	@Test
	public void appliesToBloodveldsAndFiends()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertTrue(DemonbaneGear.applies(database.findByTaskName("Bloodvelds")));
		assertTrue(DemonbaneGear.applies(database.findByTaskName("Waterfiends")));
		assertTrue(DemonbaneGear.applies(database.findByTaskName("Abyssal demons")));
		assertEquals(
			"Emberlight",
			GearLoadouts.forMonster(database.findByTaskName("Greater demons"), List.of())
				.get(0)
				.worn(EquipmentSlot.WEAPON)
				.getName());
	}

	@Test
	public void doesNotSwapWeaponsOnNonDemons()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		assertFalse(DemonbaneGear.applies(birds));
		assertEquals(
			"Ghrazi rapier",
			GearLoadouts.forMonster(birds, List.of()).get(0).worn(EquipmentSlot.WEAPON).getName());
		SlayerMonster hellhounds = new MonsterDatabase(new Gson()).findByTaskName("Hellhounds");
		assertFalse(DemonbaneGear.applies(hellhounds));
		assertEquals(
			"Ghrazi rapier",
			GearLoadouts.forMonster(hellhounds, List.of()).get(0).worn(EquipmentSlot.WEAPON).getName());
	}

	private static GearLoadout loadoutFor(SlayerMonster monster, CombatStyle style)
	{
		return loadoutFor(monster, style, List.of());
	}

	private static GearLoadout loadoutFor(SlayerMonster monster, CombatStyle style, List<GearLoadout> wiki)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, wiki))
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
			+ "\"head\":[\" [[Slayer helmet (i)]]\"],"
			+ "\"cape\":[\" [[Infernal cape]]\"],"
			+ "\"neck\":[\" [[Amulet of torture]]\"],"
			+ "\"ammo\":[\" [[Rada's blessing 4]]\"],"
			+ "\"weapon\":[\" [[Osmumten's fang]]\"],"
			+ "\"body\":[\" [[Torva platebody]]\"],"
			+ "\"shield\":[\" [[Avernic defender]]\"],"
			+ "\"legs\":[\" [[Torva platelegs]]\"],"
			+ "\"hands\":[\" [[Ferocious gloves]]\"],"
			+ "\"feet\":[\" [[Primordial boots]]\"],"
			+ "\"ring\":[\" [[Ultor ring]]\"]"
			+ "}}";
		return WikiEquipmentTable.parse(new Gson(), "Black demon/Strategies", json).toRanked();
	}
}
