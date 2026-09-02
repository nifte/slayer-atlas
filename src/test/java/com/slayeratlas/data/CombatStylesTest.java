package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class CombatStylesTest
{
	@Test
	public void parsesRecommendedStyleMentionsInOrder()
	{
		assertEquals(
			Arrays.asList(CombatStyle.MELEE, CombatStyle.RANGED),
			CombatStyles.parse("Melee or ranged safespot"));
		assertEquals(
			Collections.singletonList(CombatStyle.MAGIC),
			CombatStyles.parse("Magic"));
		assertEquals(Collections.emptyList(), CombatStyles.parse(""));
		assertEquals(Collections.emptyList(), CombatStyles.parse(null));
	}

	@Test
	public void eligibleStylesFollowWikiAdvice()
	{
		assertEquals(
			Collections.singletonList(CombatStyle.MAGIC),
			CombatStyles.eligible(monster("{\"recommendedStyle\":\"Magic\",\"weakness\":\"Magic only. Melee cannot reach them.\"}")));
		assertEquals(
			Arrays.asList(CombatStyle.MELEE, CombatStyle.RANGED, CombatStyle.MAGIC),
			CombatStyles.eligible(monster("{\"recommendedStyle\":\"Melee\",\"weakness\":\"Any combat style. Finish with fungicide.\"}")));
		assertEquals(
			Arrays.asList(CombatStyle.MELEE, CombatStyle.RANGED),
			CombatStyles.eligible(monster("{\"recommendedStyle\":\"Melee or ranged with dragon hunter gear\"}")));
		assertEquals(
			Arrays.asList(CombatStyle.MELEE, CombatStyle.RANGED, CombatStyle.MAGIC),
			CombatStyles.eligible(monster(
				"{\"recommendedStyle\":\"Melee with a leaf-bladed battleaxe\","
					+ "\"weakness\":\"Leaf-bladed weapons, broad ammo, or Magic Dart only.\"}")));
	}

	@Test
	public void doesNotRecommendRangedWhenTortuganShieldTakesTheCape()
	{
		SlayerMonster blocked = new com.google.gson.Gson().fromJson(
			"{\"recommendedStyle\":\"Melee or ranged\",\"requiredItems\":[\"Tortugan shield\"]}",
			SlayerMonster.class);
		assertEquals(Collections.singletonList(CombatStyle.MELEE), CombatStyles.eligible(blocked));
		assertEquals(
			Collections.singletonList(CombatStyle.MELEE),
			CombatStyles.eligible(new MonsterDatabase(new com.google.gson.Gson()).findByTaskName("Gryphons")));
	}

	@Test
	public void classifiesWikiCaptions()
	{
		assertEquals(CombatStyle.MELEE, CombatStyle.fromCaption("Melee"));
		assertEquals(CombatStyle.RANGED, CombatStyle.fromCaption("Ranged (high defence)"));
		assertEquals(CombatStyle.MAGIC, CombatStyle.fromCaption("High Magic defence"));
		assertNull(CombatStyle.fromCaption("Prayer bonus"));
	}

	private static SlayerMonster monster(String json)
	{
		return new com.google.gson.Gson().fromJson(json, SlayerMonster.class);
	}
}
