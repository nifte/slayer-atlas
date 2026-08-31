package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
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
	public void upgradesAWikiDefenderToTheMeleeBisOffhand()
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
			"Avernic defender",
			GearLoadouts.forMonster(dust, List.of(wiki)).get(0).worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void usesTheBestOwnedOffhandWhenBisIsMissing()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Dust devil/Strategies",
			"{\"style\":\"Melee\",\"Recommended Equipment\":{\"shield\":[\" [[Dragon defender]]\"]}}")
			.toRanked();
		SlayerMonster dust = new Gson().fromJson("{\"name\":\"Dust devils\"}", SlayerMonster.class);
		assertEquals(
			"Dragon defender",
			GearLoadouts.forMonster(
				dust,
				List.of(ranked),
				GearRecommendation.of(true, OwnedItems.withBank(Set.of("Dragon defender"))))
				.get(0)
				.worn(EquipmentSlot.SHIELD)
				.getName());
	}

	@Test
	public void fillsOffhandWhenWikiOnlyHasATwoHandedWeapon()
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
		assertEquals("Zaryte crossbow", ranged.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Twisted buckler", ranged.worn(EquipmentSlot.SHIELD).getName());
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
		assertEquals("Dragonfire shield", melee.worn(EquipmentSlot.SHIELD).getName());
		assertEquals("Amulet of rancour", melee.worn(EquipmentSlot.NECK).getName());
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
		assertEquals("Dragonfire ward", ranged.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void keepsACompleteWikiSetAndFillsEmptySlots()
	{
		String json = "{"
			+ "\"style\":\"Magic\","
			+ "\"Recommended Equipment\":{"
			+ "\"head\":[\" [[Slayer helmet (i)]]\"],"
			+ "\"cape\":[\" [[Imbued god cape]]\"],"
			+ "\"neck\":[\" [[Occult necklace]]\"],"
			+ "\"ammo\":[\" [[Rada's blessing 4]]\"],"
			+ "\"weapon\":[\" [[Tumeken's shadow]]\"],"
			+ "\"body\":[\" [[Ancestral robe top]]\"],"
			+ "\"legs\":[\" [[Ancestral robe bottom]]\"],"
			+ "\"hands\":[\" [[Confliction gauntlets]]\"],"
			+ "\"feet\":[\" [[Eternal boots]]\"],"
			+ "\"ring\":[\" [[Magus ring]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Kraken/Strategies", json).toLoadout();
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findByTaskName("Cave kraken");
		GearLoadout magic = GearLoadouts.forMonster(kraken, List.of(wiki)).get(0);
		assertEquals("Ancestral robe top", magic.worn(EquipmentSlot.BODY).getName());
		assertEquals("Confliction gauntlets", magic.worn(EquipmentSlot.HANDS).getName());
		assertEquals("Tumeken's shadow", magic.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Elidinis' ward (f)", magic.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void fillsMissingWeaponAndShieldWhenWikiHasBolts()
	{
		String json = "{"
			+ "\"style\":\"Ranged\","
			+ "\"Recommended Equipment\":{"
			+ "\"head\":[\" [[Slayer helmet (i)]]\"],"
			+ "\"cape\":[\" [[Ava's assembler]]\"],"
			+ "\"neck\":[\" [[Necklace of anguish]]\"],"
			+ "\"ammo\":[\" [[Ruby dragon bolts (e)]]\"],"
			+ "\"body\":[\" [[Masori body (f)]]\"],"
			+ "\"legs\":[\" [[Masori chaps (f)]]\"],"
			+ "\"hands\":[\" [[Zaryte vambraces]]\"],"
			+ "\"feet\":[\" [[Pegasian boots]]\"],"
			+ "\"ring\":[\" [[Archers ring (i)]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Zygomite/Strategies", json).toLoadout();
		SlayerMonster zygomites = new MonsterDatabase(new Gson()).findByTaskName("Zygomites");
		GearLoadout ranged = loadoutFor(GearLoadouts.forMonster(zygomites, List.of(wiki)), CombatStyle.RANGED);
		assertEquals("Zaryte crossbow", ranged.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Twisted buckler", ranged.worn(EquipmentSlot.SHIELD).getName());
		assertEquals("Necklace of rupture", ranged.worn(EquipmentSlot.NECK).getName());
		assertEquals("Ruby dragon bolts (e)", ranged.worn(EquipmentSlot.AMMO).getName());
		assertEquals("Avernic treads (max)", ranged.worn(EquipmentSlot.FEET).getName());
		assertAllWornFilled(ranged, zygomites.getName());
	}

	@Test
	public void upgradesSparseWikiTablesToStyleBis()
	{
		String json = "{"
			+ "\"style\":\"Magic\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Trident of the swamp]]\"],"
			+ "\"body\":[\" [[Mystic robe top]]\"],"
			+ "\"legs\":[\" [[Mystic robe bottom]]\"],"
			+ "\"cape\":[\" [[Fire cape]]\"],"
			+ "\"ring\":[\" [[Seers ring]]\"]"
			+ "}}";
		GearLoadout wiki = WikiEquipmentTable.parse(new Gson(), "Slayer task/Cave krakens", json).toLoadout();
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findByTaskName("Cave kraken");
		GearLoadout magic = GearLoadouts.forMonster(kraken, List.of(wiki)).get(0);
		assertEquals("Ancestral robe top", magic.worn(EquipmentSlot.BODY).getName());
		assertEquals("Tumeken's shadow", magic.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Imbued Saradomin cape", magic.worn(EquipmentSlot.CAPE).getName());
		assertEquals("Magus ring", magic.worn(EquipmentSlot.RING).getName());
		assertEquals("Confliction gauntlets", magic.worn(EquipmentSlot.HANDS).getName());
		assertEquals("Rada's blessing 4", magic.worn(EquipmentSlot.AMMO).getName());
		assertEquals("Elidinis' ward (f)", magic.worn(EquipmentSlot.SHIELD).getName());
		assertAllWornFilled(magic, kraken.getName());
	}

	@Test
	public void usesOwnedWikiMidTierWhenBisIsNotOwned()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Slayer task/Cave krakens",
			"{\"style\":\"Magic\",\"Recommended Equipment\":{"
				+ "\"weapon\":[\" [[Trident of the swamp]]\"],"
				+ "\"body\":[\" [[Mystic robe top]]\"]}}")
			.toRanked();
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findByTaskName("Cave kraken");
		GearLoadout magic = GearLoadouts.forMonster(
			kraken,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Trident of the swamp",
				"Mystic robe top"))))
			.get(0);
		assertEquals("Trident of the swamp", magic.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Mystic robe top", magic.worn(EquipmentSlot.BODY).getName());
		assertEquals(null, magic.worn(EquipmentSlot.CAPE));
	}

	@Test
	public void usesStyleBisUntilABankSnapshotExists()
	{
		RankedGearLoadout ranked = WikiEquipmentTable.parse(
			new Gson(),
			"Black demon/Strategies",
			"{\"style\":\"Melee\",\"Recommended Equipment\":{\"weapon\":[\" [[Abyssal whip]]\"]}}")
			.toRanked();
		SlayerMonster demons = new MonsterDatabase(new Gson()).findByTaskName("Black demons");
		GearLoadout melee = GearLoadouts.forMonster(
			demons,
			List.of(ranked),
			GearRecommendation.of(true, OwnedItems.none()))
			.get(0);
		assertEquals("Emberlight", melee.worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void fillsEveryWornAndInventorySlotForEveryMonster()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		for (SlayerMonster monster : database.getPages())
		{
			for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
			{
				assertAllWornFilled(loadout, monster.getName());
				assertEquals(monster.getName(), InventoryLoadouts.SIZE, loadout.getInventory().size());
				for (GearItem item : loadout.getInventory())
				{
					assertNotNull(monster.getName(), item);
					assertNotNull(monster.getName(), item.getName());
				}
			}
		}
	}

	private static GearLoadout loadoutFor(List<GearLoadout> loadouts, CombatStyle style)
	{
		for (GearLoadout loadout : loadouts)
		{
			if (loadout.getStyle() == style)
			{
				return loadout;
			}
		}
		throw new AssertionError("No " + style + " loadout");
	}

	private static void assertAllWornFilled(GearLoadout loadout, String monster)
	{
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (slot.onWornGrid())
			{
				assertNotNull(monster + " " + loadout.getStyle() + " " + slot, loadout.worn(slot));
			}
		}
	}
}
