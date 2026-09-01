package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class FrostDragonLoadoutTest
{
	private final Gson gson = new Gson();
	private final SlayerMonster frost = new MonsterDatabase(gson).findByTaskName("Frost dragons");

	@Test
	public void matchesTheFrostDragonStrategiesPage()
	{
		assertTrue(WikiPageNames.matches("Frost dragon/Strategies", frost));
		List<RankedGearLoadout> matched = WikiLoadoutMatcher.matchRanked(
			gson,
			frost,
			List.of(
				new WikiEquipmentRow("Frost dragon/Strategies", meleeJson()),
				new WikiEquipmentRow("Frost dragon/Strategies", rangedJson()),
				new WikiEquipmentRow("Frost dragon/Strategies", magicJson())));
		assertEquals(3, matched.size());
		assertEquals("Avernic defender", matched.get(0).ranks(EquipmentSlot.SHIELD).get(0).getName());
		assertEquals("Twisted buckler", matched.get(1).ranks(EquipmentSlot.SHIELD).get(0).getName());
		assertEquals("Tome of Fire", matched.get(2).ranks(EquipmentSlot.SHIELD).get(0).getName());
	}

	@Test
	public void usesWikiMaxMeleeOffhandAndAntifires()
	{
		GearLoadout melee = loadoutFor(wikiLoadouts(), CombatStyle.MELEE);
		assertEquals("Avernic defender", melee.worn(EquipmentSlot.SHIELD).getName());
		assertEquals("Dragon hunter lance", melee.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Amulet of rancour", melee.worn(EquipmentSlot.NECK).getName());
		assertEquals("Avernic treads (max)", melee.worn(EquipmentSlot.FEET).getName());
		assertFalse(names(melee.getInventory()).contains("Dragonfire shield"));
		assertTrue(count(melee.getInventory(), "Extended super antifire(4)") >= 2);
	}

	@Test
	public void usesWikiMaxRangedAndMagicOffhands()
	{
		List<GearLoadout> loadouts = wikiLoadouts();
		assertEquals("Twisted buckler", loadoutFor(loadouts, CombatStyle.RANGED).worn(EquipmentSlot.SHIELD).getName());
		assertEquals("Tome of Fire", loadoutFor(loadouts, CombatStyle.MAGIC).worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void ownedOnlyWalksTheWikiShieldRanks()
	{
		List<RankedGearLoadout> ranked = WikiLoadoutMatcher.matchRanked(
			gson,
			frost,
			List.of(new WikiEquipmentRow("Frost dragon/Strategies", meleeJson())));
		assertEquals(
			"Avernic defender",
			ownedMelee(ranked, Set.of("Avernic defender", "Dragon defender", "Dragonfire shield"))
				.worn(EquipmentSlot.SHIELD)
				.getName());
		assertEquals(
			"Dragon defender",
			ownedMelee(ranked, Set.of("Dragon defender", "Dragonfire shield"))
				.worn(EquipmentSlot.SHIELD)
				.getName());
		assertEquals(
			"Dragonfire shield",
			ownedMelee(ranked, Set.of("Dragonfire shield", "Anti-dragon shield"))
				.worn(EquipmentSlot.SHIELD)
				.getName());
	}

	@Test
	public void ownedOnlyNeverRecommendsAnUnownedOffhand()
	{
		List<RankedGearLoadout> ranked = WikiLoadoutMatcher.matchRanked(
			gson,
			frost,
			List.of(new WikiEquipmentRow("Frost dragon/Strategies", meleeJson())));
		GearLoadout melee = ownedMelee(ranked, Set.of("Dragon defender"));
		assertEquals("Dragon defender", melee.worn(EquipmentSlot.SHIELD).getName());
		assertFalse(names(List.of(melee.worn(EquipmentSlot.SHIELD))).contains("Avernic defender"));
		assertFalse(names(melee.getInventory()).contains("Extended super antifire(4)"));
	}

	@Test
	public void strategyPageMaxDpsTableBeatsGenericDragonfireFallback()
	{
		SlayerMonster dragons = new MonsterDatabase(gson).findByTaskName("Black dragons");
		RankedGearLoadout wiki = WikiEquipmentTable.parse(gson, "Black dragon/Strategies", meleeJson()).toRanked();
		GearLoadout melee = loadoutFor(
			GearLoadouts.forMonster(dragons, List.of(wiki), GearRecommendation.specialized()),
			CombatStyle.MELEE);
		assertEquals("Avernic defender", melee.worn(EquipmentSlot.SHIELD).getName());
		assertTrue(count(melee.getInventory(), "Extended super antifire(4)") >= 2);
	}

	private List<GearLoadout> wikiLoadouts()
	{
		List<RankedGearLoadout> ranked = WikiLoadoutMatcher.matchRanked(
			gson,
			frost,
			List.of(
				new WikiEquipmentRow("Frost dragon/Strategies", meleeJson()),
				new WikiEquipmentRow("Frost dragon/Strategies", rangedJson()),
				new WikiEquipmentRow("Frost dragon/Strategies", magicJson())));
		return GearLoadouts.forMonster(frost, ranked, GearRecommendation.specialized());
	}

	private GearLoadout ownedMelee(List<RankedGearLoadout> ranked, Set<String> owned)
	{
		return loadoutFor(
			GearLoadouts.forMonster(frost, ranked, GearRecommendation.of(true, OwnedItems.withBank(owned))),
			CombatStyle.MELEE);
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

	private static int count(List<GearItem> items, String name)
	{
		int total = 0;
		for (GearItem item : items)
		{
			if (item != null && name.equals(item.getName()))
			{
				total++;
			}
		}
		return total;
	}

	private static String names(List<GearItem> items)
	{
		StringBuilder text = new StringBuilder();
		for (GearItem item : items)
		{
			if (item != null)
			{
				text.append(item.getName()).append(',');
			}
		}
		return text.toString();
	}

	private static String meleeJson()
	{
		return "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Dragon hunter lance]]\"],"
			+ "\"shield\":[\" [[Avernic defender]]\",\" [[Dragon defender]]\","
			+ "\" [[Dragonfire shield]]\",\" [[Anti-dragon shield]]\"],"
			+ "\"neck\":[\" [[Amulet of rancour]]\",\" [[Amulet of torture]]\"],"
			+ "\"feet\":[\" [[Avernic treads (max)]]\",\" [[Primordial boots]]\"],"
			+ "\"special\":[\" [[Burning claws]]\",\" [[Dragon claws]]\"]"
			+ "}}";
	}

	private static String rangedJson()
	{
		return "{"
			+ "\"style\":\"Ranged\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Dragon hunter crossbow]]\"],"
			+ "\"shield\":[\" [[Twisted buckler]]\",\" [[Dragonfire ward]]\",\" [[Anti-dragon shield]]\"]"
			+ "}}";
	}

	private static String magicJson()
	{
		return "{"
			+ "\"style\":\"Magic\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Dragon hunter wand]]\"],"
			+ "\"shield\":[\" [[Tome of Fire]]\",\" [[Elidinis' ward (f)]]\",\" [[Anti-dragon shield]]\"]"
			+ "}}";
	}
}
