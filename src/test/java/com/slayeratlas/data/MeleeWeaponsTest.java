package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class MeleeWeaponsTest
{
	@Test
	public void ranksRapierMaceAndSaeldorAboveFang()
	{
		List<GearItem> ordered = MeleeWeapons.demoteFang(List.of(MeleeWeapons.FANG));
		assertEquals("Ghrazi rapier", ordered.get(0).getName());
		assertEquals("Inquisitor's mace", ordered.get(1).getName());
		assertEquals("Blade of saeldor", ordered.get(2).getName());
		assertEquals("Osmumten's fang", ordered.get(3).getName());
	}

	@Test
	public void keepsAScytheAheadOfTheFangGroup()
	{
		List<GearItem> ordered = MeleeWeapons.demoteFang(List.of(
			GearItem.named("Scythe of vitur"),
			MeleeWeapons.FANG,
			GearItem.named("Abyssal whip")));
		assertEquals("Scythe of vitur", ordered.get(0).getName());
		assertEquals("Ghrazi rapier", ordered.get(1).getName());
		assertEquals("Osmumten's fang", ordered.get(4).getName());
		assertEquals("Abyssal whip", ordered.get(5).getName());
	}

	@Test
	public void leavesWeaponListsWithoutFangUnchanged()
	{
		List<GearItem> weapons = List.of(GearItem.named("Abyssal whip"));
		assertSame(weapons, MeleeWeapons.demoteFang(weapons));
	}

	@Test
	public void recommendsRapierOnFireGiantsWhenWikiListsFangFirst()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Fire giant/Strategies",
			"{\"style\":\"Melee\",\"Recommended Equipment\":{"
				+ "\"weapon\":[\" [[Osmumten's fang]]\",\" [[Ghrazi rapier]]\",\" [[Abyssal whip]]\"]}}")
			.toRanked();
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout melee = GearLoadouts.forMonster(
			giants,
			List.of(ranked),
			GearRecommendation.specialized())
			.get(0);
		assertEquals("Ghrazi rapier", melee.worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void usesOwnedRapierInsteadOfOwnedFang()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Fire giant/Strategies",
			"{\"style\":\"Melee\",\"Recommended Equipment\":{\"weapon\":[\" [[Osmumten's fang]]\"]}}")
			.toRanked();
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout melee = GearLoadouts.forMonster(
			giants,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Osmumten's fang",
				"Ghrazi rapier"))))
			.get(0);
		assertEquals("Ghrazi rapier", melee.worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void keepsFangWhenNoneOfTheBetterWeaponsAreOwned()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Fire giant/Strategies",
			"{\"style\":\"Melee\",\"Recommended Equipment\":{\"weapon\":[\" [[Osmumten's fang]]\"]}}")
			.toRanked();
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout melee = GearLoadouts.forMonster(
			giants,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Osmumten's fang"))))
			.get(0);
		assertEquals("Osmumten's fang", melee.worn(EquipmentSlot.WEAPON).getName());
	}
}
