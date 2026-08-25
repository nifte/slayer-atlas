package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class GearLoadoutsTest
{
	@Test
	public void replacesWikiHeadWithImbuedSlayerHelmet()
	{
		String json = "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"head\":[\" [[Slayer helmet]]\"],"
			+ "\"weapon\":[\" [[Ghrazi rapier]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Dust devil/Strategies", json).toLoadout();
		assertEquals("Slayer helmet", wiki.worn(EquipmentSlot.HEAD).getName());

		SlayerMonster dust = new Gson().fromJson("{\"name\":\"Dust devils\"}", SlayerMonster.class);
		GearLoadout melee = GearLoadouts.forMonster(dust, List.of(wiki)).get(0);
		assertEquals("Slayer helmet (i)", melee.worn(EquipmentSlot.HEAD).getName());
		assertEquals("Ghrazi rapier", melee.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Avernic defender", melee.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void keepsWikiOffhandOnOneHandedWeapons()
	{
		String json = "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Ghrazi rapier]]\"],"
			+ "\"shield\":[\" [[Dragon defender]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Dust devil/Strategies", json).toLoadout();
		SlayerMonster dust = new Gson().fromJson("{\"name\":\"Dust devils\"}", SlayerMonster.class);
		assertEquals(
			"Dragon defender",
			GearLoadouts.forMonster(dust, List.of(wiki)).get(0).worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void doesNotAddAnOffhandToTwoHandedWeapons()
	{
		String json = "{"
			+ "\"style\":\"Ranged\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Venator bow]]\"],"
			+ "\"shield\":[\" [[Twisted buckler]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Dust devil/Strategies", json).toLoadout();
		SlayerMonster dust = new Gson().fromJson(
			"{\"name\":\"Dust devils\",\"recommendedStyle\":\"Ranged\"}",
			SlayerMonster.class);
		GearLoadout ranged = GearLoadouts.forMonster(dust, List.of(wiki)).get(0);
		assertEquals("Venator bow", ranged.worn(EquipmentSlot.WEAPON).getName());
		assertNull(ranged.worn(EquipmentSlot.SHIELD));
	}

	@Test
	public void keepsALanceOnWikiDragonMelee()
	{
		String json = "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Dragon hunter lance]]\"],"
			+ "\"neck\":[\" [[Amulet of torture]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Black dragon/Strategies", json).toLoadout();
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		GearLoadout melee = GearLoadouts.forMonster(dragons, List.of(wiki)).get(0);
		assertEquals("Dragon hunter lance", melee.worn(EquipmentSlot.WEAPON).getName());
		assertNull(melee.worn(EquipmentSlot.SHIELD));
		assertEquals("Amulet of torture", melee.worn(EquipmentSlot.NECK).getName());
	}

	@Test
	public void keepsWikiDragonfireOffhandsOnOneHandedDragonSetups()
	{
		String json = "{"
			+ "\"style\":\"Ranged\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Dragon hunter crossbow]]\"],"
			+ "\"shield\":[\" [[Anti-dragon shield]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Black dragon/Strategies", json).toLoadout();
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		GearLoadout ranged = null;
		for (GearLoadout loadout : GearLoadouts.forMonster(dragons, List.of(wiki)))
		{
			if (loadout.getStyle() == CombatStyle.RANGED)
			{
				ranged = loadout;
			}
		}
		assertEquals("Anti-dragon shield", ranged.worn(EquipmentSlot.SHIELD).getName());
	}
}
