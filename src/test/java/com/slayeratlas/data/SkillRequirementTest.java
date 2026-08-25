package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class SkillRequirementTest
{
	@Test
	public void recognizesBareSkillLevels()
	{
		assertTrue(SkillRequirement.isLevel("50 Magic"));
		assertTrue(SkillRequirement.isLevel("91 Slayer"));
		assertTrue(SkillRequirement.isLevel("73 Sailing"));
		assertTrue(SkillRequirement.isLevel("70 Defence for the shield"));
		assertTrue(SkillRequirement.isLevel(" 20 Defence "));
	}

	@Test
	public void leavesItemsAlone()
	{
		assertFalse(SkillRequirement.isLevel("Mirror shield or slayer helmet"));
		assertFalse(SkillRequirement.isLevel("Lit bug lantern (33 Firemaking)"));
		assertFalse(SkillRequirement.isLevel("Brittle key"));
		assertFalse(SkillRequirement.isLevel(""));
		assertFalse(SkillRequirement.isLevel(null));
	}

	@Test
	public void splitsAMixedRequiredList()
	{
		assertEquals(
			Collections.singletonList("Three Cerberus crystals or an eternal key"),
			SkillRequirement.items(Arrays.asList("91 Slayer", "Three Cerberus crystals or an eternal key")));
		assertEquals(
			Collections.singletonList("91 Slayer"),
			SkillRequirement.levels(Arrays.asList("91 Slayer", "Three Cerberus crystals or an eternal key")));
	}
}
