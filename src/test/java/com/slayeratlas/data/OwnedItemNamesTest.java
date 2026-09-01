package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;

public class OwnedItemNamesTest
{
	@Test
	public void matchesLockedAssemblerToAvasAssembler()
	{
		assertTrue(OwnedItemNames.matches("Ava's assembler", "Ava's assembler (l)"));
		assertTrue(OwnedItemNames.matches("Ava's assembler", "Ava's assembler (l) (broken)"));
		assertEquals("ava's assembler", OwnedItemNames.normalize("Ava's assembler (l)"));
		assertEquals(
			null,
			OwnedItemNames.preferredOwnedName("Ava's assembler", List.of("Ava's assembler (l)")));
		assertEquals("Ava's assembler.png", GearItem.named("Ava's assembler (l)").getImageFile());
		assertTrue(OwnedItemNames.matches("Ava's assembler", "Masori assembler (l)"));
		assertEquals("masori assembler", OwnedItemNames.normalize("Masori assembler (l)"));
		assertEquals(
			"Masori assembler",
			OwnedItemNames.preferredOwnedName("Ava's assembler", List.of("Masori assembler (l)")));
	}

	@Test
	public void matchesChargedGloryToTheUnchargedWikiName()
	{
		assertTrue(OwnedItemNames.matches("Amulet of glory", "Amulet of glory (6)"));
		assertEquals("amulet of glory", OwnedItemNames.normalize("Amulet of glory (6)"));
	}

	@Test
	public void matchesImbuedSlayerHelmets()
	{
		assertTrue(OwnedItemNames.matches("Slayer helmet (i)", "Slayer helmet (i)"));
		assertTrue(OwnedItemNames.matches("Slayer helmet (i)", "Slayer helmet (imbued)"));
		assertEquals("slayer helmet (i)", OwnedItemNames.normalize("Slayer helmet (imbued)"));
	}

	@Test
	public void doesNotTreatAnUnimbuedHelmetAsImbued()
	{
		assertFalse(OwnedItemNames.matches("Slayer helmet (i)", "Slayer helmet"));
	}

	@Test
	public void matchesBlazingBlowpipeToToxicBlowpipe()
	{
		assertTrue(OwnedItemNames.matches("Toxic blowpipe", "Blazing blowpipe"));
		assertTrue(OwnedItemNames.matches("Toxic blowpipe", "Blazing blowpipe (empty)"));
		assertTrue(OwnedItemNames.matches("Blazing blowpipe", "Toxic blowpipe"));
	}

	@Test
	public void matchesDeadmanImbuedGodCapes()
	{
		assertTrue(OwnedItemNames.matches("Imbued god cape", "Imbued Zamorak cape (Deadman)"));
		assertTrue(OwnedItemNames.matches("Imbued Saradomin cape", "Imbued guthix cape (deadman)"));
		assertTrue(OwnedItemNames.matches("Imbued guthix cape", "Imbued Guthix cape (Deadman)"));
	}

	@Test
	public void matchesOrnamentKitSuffixesToTheBaseItem()
	{
		assertTrue(OwnedItemNames.matches("Amulet of torture", "Amulet of torture (or)"));
		assertTrue(OwnedItemNames.matches("Dragon defender", "Dragon defender (t)"));
		assertTrue(OwnedItemNames.matches("Armadyl godsword", "Armadyl godsword (or)"));
		assertTrue(OwnedItemNames.matches("Armadyl godsword", "Armadyl godsword (Deadman)"));
	}

	@Test
	public void matchesRecolouredSlayerHelmets()
	{
		assertTrue(OwnedItemNames.matches("Slayer helmet (i)", "Twisted slayer helmet (i)"));
		assertTrue(OwnedItemNames.matches("Slayer helmet (i)", "Hydra slayer helmet (imbued)"));
		assertFalse(OwnedItemNames.matches("Slayer helmet (i)", "Twisted slayer helmet"));
	}

	@Test
	public void sameItemIgnoresChargesAndLockTagsButNotRecolors()
	{
		assertTrue(OwnedItemNames.sameItem("Slayer helmet (i)", "Slayer helmet (imbued)"));
		assertTrue(OwnedItemNames.sameItem("Amulet of glory", "Amulet of glory (6)"));
		assertTrue(OwnedItemNames.sameItem("Ava's assembler", "Ava's assembler (l)"));
		assertFalse(OwnedItemNames.sameItem("Slayer helmet (i)", "Black slayer helmet (i)"));
		assertFalse(OwnedItemNames.sameItem("Slayer helmet (i)", "Twisted slayer helmet (i)"));
		assertFalse(OwnedItemNames.sameItem("Toxic blowpipe", "Blazing blowpipe"));
	}

	@Test
	public void doesNotTreatUnrelatedItemsAsOrnaments()
	{
		assertFalse(OwnedItemNames.matches("Toxic blowpipe", "Magic shortbow"));
		assertFalse(OwnedItemNames.matches("Imbued god cape", "Imbued heart"));
		assertFalse(OwnedItemNames.matches("Amulet of torture", "Amulet of glory"));
	}

	@Test
	public void matchesDizanasMaxCapeToBlessedQuiver()
	{
		assertTrue(OwnedItemNames.matches("Blessed dizana's quiver", "Dizana's max cape"));
		assertTrue(OwnedItemNames.matches("Ava's assembler", "Assembler max cape"));
		assertTrue(OwnedItemNames.matches("Ava's assembler", "Masori assembler"));
		assertTrue(OwnedItemNames.matches("Ava's assembler", "Masori assembler max cape (l)"));
		assertEquals(
			"Dizana's max cape",
			OwnedItemNames.preferredOwnedName("Blessed dizana's quiver", List.of("Dizana's max cape")));
	}

	@Test
	public void matchesSaturatedHeartToImbuedHeart()
	{
		assertTrue(OwnedItemNames.matches("Imbued heart", "Saturated heart"));
		assertTrue(OwnedItemNames.matches("Saturated heart", "Imbued heart"));
		assertEquals(
			"Saturated heart",
			OwnedItemNames.preferredOwnedName("Imbued heart", List.of("Saturated heart")));
		assertEquals(
			"Saturated heart",
			OwnedItemNames.preferredOwnedName("Imbued heart", List.of("Imbued heart", "Saturated heart")));
		assertEquals(
			null,
			OwnedItemNames.preferredOwnedName("Saturated heart", List.of("Imbued heart", "Saturated heart")));
		assertEquals(
			"Imbued heart",
			OwnedItemNames.preferredOwnedName("Saturated heart", List.of("Imbued heart")));
	}

	@Test
	public void prefersTheOwnedOrnamentalDisplayName()
	{
		assertEquals(
			"Blazing blowpipe",
			OwnedItemNames.preferredOwnedName("Toxic blowpipe", List.of("Blazing blowpipe")));
		assertEquals(
			"Amulet of torture (or)",
			OwnedItemNames.preferredOwnedName("Amulet of torture", List.of("Amulet of torture (or)")));
		assertEquals(
			null,
			OwnedItemNames.preferredOwnedName("Amulet of glory", List.of("Amulet of glory (6)")));
	}
}
