package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayeratlas.data.SlayerMonster;
import org.junit.Test;

public class MonsterNotesTextTest
{
	@Test
	public void prefixesARequirementBeforeExistingNotes()
	{
		SlayerMonster monster = monster(
			"{\"requirements\":[\"70 Defence\"],\"notes\":\"Back of the dungeon.\"}");
		assertEquals("Requires 70 Defence. Back of the dungeon.", MonsterNotesText.display(monster));
	}

	@Test
	public void showsRequirementsWhenThereAreNoNotes()
	{
		SlayerMonster monster = monster("{\"requirements\":[\"Priest in Peril\"]}");
		assertEquals("Requires Priest in Peril.", MonsterNotesText.display(monster));
	}

	@Test
	public void keepsNotesWhenThereAreNoRequirements()
	{
		SlayerMonster monster = monster("{\"notes\":\"Chickens south of Falador.\"}");
		assertEquals("Chickens south of Falador.", MonsterNotesText.display(monster));
	}

	@Test
	public void writesEachRequirementAsItsOwnSentence()
	{
		SlayerMonster monster = monster(
			"{\"requirements\":[\"Bone Voyage\",\"70 Defence for the shield\"],\"notes\":\"Use the shield.\"}");
		assertEquals(
			"Requires Bone Voyage. Requires 70 Defence for the shield. Use the shield.",
			MonsterNotesText.display(monster));
	}

	@Test
	public void leavesTextThatAlreadyMentionsRequiredAlone()
	{
		SlayerMonster monster = monster(
			"{\"requirements\":[\"Priest in Peril is not required here; this is a Wilderness task\"]}");
		assertEquals(
			"Priest in Peril is not required here; this is a Wilderness task.",
			MonsterNotesText.display(monster));
	}

	@Test
	public void movesSkillLevelsFromRequiredItemsIntoNotes()
	{
		SlayerMonster monster = monster(
			"{\"requiredItems\":[\"50 Magic\"],\"notes\":\"Trident or stronger magic.\"}");
		assertEquals("Requires 50 Magic. Trident or stronger magic.", MonsterNotesText.display(monster));
	}

	@Test
	public void keepsRealRequiredItemsOutOfNotes()
	{
		SlayerMonster monster = monster(
			"{\"requiredItems\":[\"Mirror shield\"],\"notes\":\"Wear the shield.\"}");
		assertEquals("Wear the shield.", MonsterNotesText.display(monster));
	}

	@Test
	public void doesNotDuplicateASkillLevelAlreadyListedAsARequirement()
	{
		SlayerMonster monster = monster(
			"{\"requiredItems\":[\"70 Defence\"],\"requirements\":[\"70 Defence\"]}");
		assertEquals("Requires 70 Defence.", MonsterNotesText.display(monster));
	}

	@Test
	public void emptyWhenNothingToShow()
	{
		assertEquals("", MonsterNotesText.display(monster("{}")));
		assertEquals("", MonsterNotesText.display(null));
	}

	private static SlayerMonster monster(String json)
	{
		return new Gson().fromJson(json, SlayerMonster.class);
	}
}
