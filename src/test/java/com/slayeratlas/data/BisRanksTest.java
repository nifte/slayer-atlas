package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class BisRanksTest
{
	@Test
	public void fallbackMeleeUsesStyleBis()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		RankedGearLoadout ranked = BisRanks.forStyle(CombatStyle.MELEE, birds);
		assertEquals("Ghrazi rapier", ranked.ranks(EquipmentSlot.WEAPON).get(0).getName());
		assertEquals("Torva platebody", ranked.ranks(EquipmentSlot.BODY).get(0).getName());
		assertEquals("Amulet of rancour", ranked.ranks(EquipmentSlot.NECK).get(0).getName());
		assertTrue(ranked.ranks(EquipmentSlot.WEAPON).size() > 1);
	}

	@Test
	public void mergesWikiMidTierAfterStyleBis()
	{
		RankedGearLoadout wiki = WikiEquipmentTable.parse(
			new Gson(),
			"Slayer task/Birds",
			"{\"style\":\"Melee\",\"Recommended Equipment\":{\"weapon\":[\" [[Rune scimitar]]\"]}}")
			.toRanked();
		List<GearItem> weapons = BisRanks.merge(wiki, new MonsterDatabase(new Gson()).findByTaskName("Birds"))
			.ranks(EquipmentSlot.WEAPON);
		assertEquals("Ghrazi rapier", weapons.get(0).getName());
		assertTrue(names(weapons).contains("Rune scimitar"));
	}

	@Test
	public void keepsWikiMaxOffhandAheadOfDragonfireFallback()
	{
		RankedGearLoadout wiki = WikiEquipmentTable.parse(
			new Gson(),
			"Frost dragon/Strategies",
			"{\"style\":\"Melee\",\"Recommended Equipment\":{"
				+ "\"shield\":[\" [[Avernic defender]]\",\" [[Dragon defender]]\",\" [[Dragonfire shield]]\"]}}")
			.toRanked();
		List<GearItem> shields = BisRanks.merge(
			wiki,
			new MonsterDatabase(new Gson()).findByTaskName("Frost dragons"))
			.ranks(EquipmentSlot.SHIELD);
		assertEquals("Avernic defender", shields.get(0).getName());
		assertTrue(names(shields).contains("Dragonfire shield"));
	}

	@Test
	public void putsDemonbaneAheadOfTheMeleeLadder()
	{
		SlayerMonster demons = new MonsterDatabase(new Gson()).findByTaskName("Black demons");
		List<GearItem> weapons = BisRanks.forStyle(CombatStyle.MELEE, demons).ranks(EquipmentSlot.WEAPON);
		assertEquals("Emberlight", weapons.get(0).getName());
		assertTrue(names(weapons).contains("Arclight"));
		assertTrue(names(weapons).contains("Ghrazi rapier"));
	}

	private static String names(List<GearItem> items)
	{
		StringBuilder text = new StringBuilder();
		for (GearItem item : items)
		{
			text.append(item.getName()).append(',');
		}
		return text.toString();
	}
}
