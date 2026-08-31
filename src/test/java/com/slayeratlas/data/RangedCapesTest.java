package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class RangedCapesTest
{
	@Test
	public void ranksBlessedQuiverThenQuiverThenAvasDevices()
	{
		List<GearItem> ranked = RangedCapes.rank(List.of());
		assertEquals("Blessed dizana's quiver", ranked.get(0).getName());
		assertEquals("Dizana's quiver", ranked.get(1).getName());
		assertEquals("Ava's assembler", ranked.get(2).getName());
		assertEquals("Ava's accumulator", ranked.get(3).getName());
		assertEquals("Ava's attractor", ranked.get(4).getName());
	}

	@Test
	public void keepsWikiAssemblerOnTheLadder()
	{
		List<GearItem> ranked = RangedCapes.rank(List.of(RangedCapes.ASSEMBLER));
		assertEquals("Blessed dizana's quiver", ranked.get(0).getName());
		assertEquals("Ava's assembler", ranked.get(2).getName());
	}

	@Test
	public void fillsTheRangedCapeOnFireGiants()
	{
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout ranged = loadoutFor(giants, CombatStyle.RANGED, List.of());
		assertEquals("Blessed dizana's quiver", ranged.worn(EquipmentSlot.CAPE).getName());
	}

	@Test
	public void fillsAMissingWikiCapeWithBlessedQuiver()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Fire giant/Strategies",
			"{\"style\":\"Ranged\",\"Recommended Equipment\":{"
				+ "\"weapon\":[\" [[Twisted bow]]\"],"
				+ "\"ammo\":[\" [[Dragon arrow]]\"]}}")
			.toRanked();
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout ranged = loadoutFor(giants, CombatStyle.RANGED, List.of(ranked));
		assertNotNull(ranged.worn(EquipmentSlot.CAPE));
		assertEquals("Blessed dizana's quiver", ranged.worn(EquipmentSlot.CAPE).getName());
	}

	@Test
	public void usesOwnedAssemblerWhenNoQuiverIsOwned()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Fire giant/Strategies",
			"{\"style\":\"Ranged\",\"Recommended Equipment\":{\"cape\":[\" [[Ava's assembler]]\"]}}")
			.toRanked();
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout ranged = GearLoadouts.forMonster(
			giants,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Ava's assembler"))))
			.stream()
			.filter(loadout -> loadout.getStyle() == CombatStyle.RANGED)
			.findFirst()
			.orElseThrow();
		assertEquals("Ava's assembler", ranged.worn(EquipmentSlot.CAPE).getName());
	}

	@Test
	public void usesALockedMasoriAssemblerAsOwnedAssembler()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Fire giant/Strategies",
			"{\"style\":\"Ranged\",\"Recommended Equipment\":{"
				+ "\"weapon\":[\" [[Twisted bow]]\"],"
				+ "\"ammo\":[\" [[Dragon arrow]]\"]}}")
			.toRanked();
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout ranged = GearLoadouts.forMonster(
			giants,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Masori assembler (l)"))))
			.stream()
			.filter(loadout -> loadout.getStyle() == CombatStyle.RANGED)
			.findFirst()
			.orElseThrow();
		assertEquals("Masori assembler", ranged.worn(EquipmentSlot.CAPE).getName());
		assertEquals("Masori assembler.png", ranged.worn(EquipmentSlot.CAPE).getImageFile());
	}

	@Test
	public void usesALockedAssemblerAsOwnedAssembler()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Fire giant/Strategies",
			"{\"style\":\"Ranged\",\"Recommended Equipment\":{"
				+ "\"weapon\":[\" [[Twisted bow]]\"],"
				+ "\"ammo\":[\" [[Dragon arrow]]\"]}}")
			.toRanked();
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout ranged = GearLoadouts.forMonster(
			giants,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Ava's assembler (l)"))))
			.stream()
			.filter(loadout -> loadout.getStyle() == CombatStyle.RANGED)
			.findFirst()
			.orElseThrow();
		assertEquals("Ava's assembler", ranged.worn(EquipmentSlot.CAPE).getName());
		assertEquals("Ava's assembler.png", ranged.worn(EquipmentSlot.CAPE).getImageFile());
	}

	@Test
	public void prefersOwnedBlessedQuiverOverAssembler()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Fire giant/Strategies",
			"{\"style\":\"Ranged\",\"Recommended Equipment\":{\"cape\":[\" [[Ava's assembler]]\"]}}")
			.toRanked();
		SlayerMonster giants = new MonsterDatabase(new Gson()).findByTaskName("Fire giants");
		GearLoadout ranged = GearLoadouts.forMonster(
			giants,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Ava's assembler",
				"Blessed dizana's quiver"))))
			.stream()
			.filter(loadout -> loadout.getStyle() == CombatStyle.RANGED)
			.findFirst()
			.orElseThrow();
		assertEquals("Blessed dizana's quiver", ranged.worn(EquipmentSlot.CAPE).getName());
	}

	private static GearLoadout loadoutFor(SlayerMonster monster, CombatStyle style, List<RankedGearLoadout> wiki)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, wiki, GearRecommendation.specialized()))
		{
			if (loadout.getStyle() == style)
			{
				return loadout;
			}
		}
		throw new AssertionError("No " + style + " loadout");
	}
}
