package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class WikiEquipmentTableTest
{
	@Test
	public void parsesEveryRankIncludingCellSplits()
	{
		String json = "{"
			+ "\"style\":\"Magic\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":["
			+ "\" [[Trident of the swamp]] / [[Trident of the seas]]\","
			+ "\" [[Sanguinesti staff]]\""
			+ "]"
			+ "}}";
		RankedGearLoadout ranked = WikiEquipmentTable.parse(new Gson(), "Kraken/Strategies", json).toRanked();
		List<GearItem> weapons = ranked.ranks(EquipmentSlot.WEAPON);
		assertEquals(3, weapons.size());
		assertEquals("Trident of the swamp", weapons.get(0).getName());
		assertEquals("Trident of the seas", weapons.get(1).getName());
		assertEquals("Sanguinesti staff", weapons.get(2).getName());
	}

	@Test
	public void mergesTwoHandedRanksIntoWeapon()
	{
		String json = "{"
			+ "\"style\":\"Magic\","
			+ "\"Recommended Equipment\":{"
			+ "\"2h\":[\" [[Tumeken's shadow]]\"],"
			+ "\"weapon\":[\" [[Trident of the swamp]]\"]"
			+ "}}";
		RankedGearLoadout ranked = WikiEquipmentTable.parse(new Gson(), "Kraken/Strategies", json).toRanked();
		List<GearItem> weapons = ranked.ranks(EquipmentSlot.WEAPON);
		assertEquals(2, weapons.size());
		assertEquals("Tumeken's shadow", weapons.get(0).getName());
		assertEquals("Trident of the swamp", weapons.get(1).getName());
		assertTrue(ranked.ranks(EquipmentSlot.TWO_HAND).isEmpty());
	}

	@Test
	public void keepsEverySpecialWeaponRank()
	{
		String json = "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Ghrazi rapier]]\"],"
			+ "\"special\":[\" [[Dragon claws]]\",\" [[Voidwaker]]\"]"
			+ "}}";
		RankedGearLoadout ranked = WikiEquipmentTable.parse(new Gson(), "Dust devil/Strategies", json).toRanked();
		assertEquals(2, ranked.getSpecials().size());
		assertEquals("Dragon claws", ranked.getSpecials().get(0).getName());
		assertEquals("Voidwaker", ranked.getSpecials().get(1).getName());
	}

	@Test
	public void keepsTheOriginalCaptionForScoring()
	{
		String json = "{"
			+ "\"style\":\"Melee BIS smuggle\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Dragon hunter lance]]\"]"
			+ "}}";
		WikiEquipmentTable table = WikiEquipmentTable.parse(new Gson(), "King Black Dragon/Strategies", json);
		assertEquals("Melee BIS smuggle", table.getCaption());
		assertEquals(CombatStyle.MELEE, table.getStyle());
		assertFalse(table.toRanked().isPrimary());
	}
}
