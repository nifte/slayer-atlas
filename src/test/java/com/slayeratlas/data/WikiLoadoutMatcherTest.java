package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class WikiLoadoutMatcherTest
{
	@Test
	public void parsesWikiTableAndMatchesStrategyPage()
	{
		String json = "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"head\":[\" [[File:Slayer helmet (i).png|link=Slayer helmet (i)]] [[Slayer helmet (i)|Slayer helmet (i)]]\"],"
			+ "\"weapon\":[\" [[File:Osmumten's fang.png|link=Osmumten's fang]] [[Osmumten's fang|Osmumten's fang]]\"]"
			+ "}}";
		SlayerMonster wyverns = new Gson().fromJson(
			"{\"name\":\"Skeletal Wyverns\",\"wiki\":\"https://oldschool.runescape.wiki/w/Skeletal_Wyvern\"}",
			SlayerMonster.class);
		List<GearLoadout> matched = WikiLoadoutMatcher.match(
			new Gson(),
			wyverns,
			List.of(new WikiEquipmentRow("Skeletal Wyvern/Strategies", json)));
		assertEquals(1, matched.size());
		assertEquals(CombatStyle.MELEE, matched.get(0).getStyle());
		assertTrue(matched.get(0).isPrimary());
		assertNotNull(matched.get(0).worn(EquipmentSlot.HEAD));
		assertEquals("Slayer helmet (i)", matched.get(0).worn(EquipmentSlot.HEAD).getName());
	}

	@Test
	public void prefersBisTablesOverFourItemWildernessSets()
	{
		String fourItem = "{"
			+ "\"style\":\"4-item melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Dragon hunter lance]]\"],"
			+ "\"feet\":[\" [[Dragon boots]]\"]"
			+ "}}";
		String bis = "{"
			+ "\"style\":\"Melee BIS smuggle\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Dragon hunter lance]]\"],"
			+ "\"neck\":[\" [[Amulet of rancour]]\"],"
			+ "\"feet\":[\" [[Avernic treads (max)]]\"]"
			+ "}}";
		SlayerMonster kbd = SlayerMonster.forAlternative("King Black Dragon");
		List<GearLoadout> matched = WikiLoadoutMatcher.match(
			new Gson(),
			kbd,
			List.of(
				new WikiEquipmentRow("King Black Dragon/Strategies", fourItem),
				new WikiEquipmentRow("King Black Dragon/Strategies", bis)));
		assertEquals(1, matched.size());
		assertEquals("Avernic treads (max)", matched.get(0).worn(EquipmentSlot.FEET).getName());
		assertEquals("Amulet of rancour", matched.get(0).worn(EquipmentSlot.NECK).getName());
	}

	@Test
	public void skipsEmptyOrPlaceholderShieldRanks()
	{
		String json = "{"
			+ "\"style\":\"Melee\","
			+ "\"Recommended Equipment\":{"
			+ "\"weapon\":[\" [[Ghrazi rapier]]\"],"
			+ "\"shield\":[\"\",\"N/A\",\" [[Avernic defender]]\"]"
			+ "}}";
		List<GearLoadout> matched = WikiLoadoutMatcher.match(
			new Gson(),
			new Gson().fromJson(
				"{\"name\":\"Dust devils\",\"wiki\":\"https://oldschool.runescape.wiki/w/Dust_devil\"}",
				SlayerMonster.class),
			List.of(new WikiEquipmentRow("Dust devil/Strategies", json)));
		assertEquals("Avernic defender", matched.get(0).worn(EquipmentSlot.SHIELD).getName());
	}
}
