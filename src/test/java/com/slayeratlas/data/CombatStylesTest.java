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
	public void classifiesWikiCaptions()
	{
		assertEquals(CombatStyle.MELEE, CombatStyle.fromCaption("Melee"));
		assertEquals(CombatStyle.RANGED, CombatStyle.fromCaption("Ranged (high defence)"));
		assertEquals(CombatStyle.MAGIC, CombatStyle.fromCaption("High Magic defence"));
		assertNull(CombatStyle.fromCaption("Prayer bonus"));
	}
}
