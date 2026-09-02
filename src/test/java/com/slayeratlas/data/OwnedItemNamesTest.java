package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
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
	public void matchesTrimmedConstructionAndCraftingCapes()
	{
		assertTrue(OwnedItemNames.matches("Construction cape", "Construction cape (t)"));
		assertTrue(OwnedItemNames.matches("Construction cape (t)", "Construction cape"));
		assertTrue(OwnedItemNames.matches("Crafting cape", "Crafting cape (t)"));
		assertEquals(
			"Construction cape (t)",
			OwnedItemNames.preferredOwnedName("Construction cape", List.of("Construction cape (t)")));
	}

	@Test
	public void matchesSailorAmuletNameVariants()
	{
		assertTrue(OwnedItemNames.matches("Sailor's amulet", "Sailors amulet"));
		assertTrue(OwnedItemNames.matches("Sailor's amulet", "Sailor’s amulet"));
		assertTrue(OwnedItemNames.matches("Sailor's amulet", "Amulet of the sailor"));
		assertEquals("sailor's amulet", OwnedItemNames.normalize("Sailor’s amulet"));
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
	public void matchesEternalSlayerRingToTheWikiSlayerRing()
	{
		assertTrue(OwnedItemNames.matches("Slayer ring", "Slayer ring (eternal)"));
		assertTrue(OwnedItemNames.matches("Slayer ring (eternal)", "Slayer ring"));
		assertTrue(OwnedItemNames.matches("Slayer ring", "Eternal slayer ring"));
		assertTrue(OwnedItemNames.matches("Slayer ring", "Slayer ring (8)"));
		assertEquals("slayer ring", OwnedItemNames.familyKey("Slayer ring (eternal)"));
		assertEquals(
			"Slayer ring (eternal)",
			OwnedItemNames.preferredOwnedName("Slayer ring", List.of("Slayer ring (eternal)")));
		assertEquals(
			"Slayer ring (eternal)",
			OwnedItemNames.preferredOwnedName(
				"Slayer ring",
				List.of("Slayer ring", "Slayer ring (eternal)")));
		assertEquals(
			null,
			OwnedItemNames.preferredOwnedName("Slayer ring", List.of("Slayer ring (8)")));
	}

	@Test
	public void matchesGraniteCannonballsToSteelCannonballs()
	{
		assertTrue(OwnedItemNames.matches("Cannonball", "Granite cannonball"));
		assertTrue(OwnedItemNames.matches("Granite cannonball", "Cannonball"));
		assertEquals(
			"Granite cannonball",
			OwnedItemNames.preferredOwnedName("Cannonball", List.of("Granite cannonball")));
	}

	@Test
	public void matchesADivineRunePouchToARunePouch()
	{
		assertTrue(OwnedItemNames.matches("Rune pouch", "Divine rune pouch"));
		assertTrue(OwnedItemNames.matches("Divine rune pouch", "Rune pouch"));
		assertEquals(
			"Divine rune pouch",
			OwnedItemNames.preferredOwnedName("Rune pouch", List.of("Divine rune pouch")));
		assertEquals(
			"Rune pouch",
			OwnedItemNames.preferredOwnedName("Divine rune pouch", List.of("Rune pouch")));
	}

	@Test
	public void matchesSalveAmuletParenSpacing()
	{
		assertTrue(OwnedItemNames.matches("Salve amulet (ei)", "Salve amulet(ei)"));
		assertTrue(OwnedItemNames.matches("Salve amulet(ei)", "Salve amulet (ei)"));
		assertTrue(OwnedItemNames.matches("Salve amulet (i)", "Salve amulet(i)"));
		assertTrue(OwnedItemNames.matches("Salve amulet (e)", "Salve amulet(e)"));
		assertFalse(OwnedItemNames.matches("Salve amulet (ei)", "Salve amulet (i)"));
		assertFalse(OwnedItemNames.matches("Salve amulet (ei)", "Salve amulet"));
		assertEquals("salve amulet (ei)", OwnedItemNames.normalize("Salve amulet(ei)"));
		assertEquals("salve amulet (ei)", OwnedItemNames.normalize("Salve amulet (ei)"));
	}

	@Test
	public void matchesAHouseTabletToThePlainHouseTeleportName()
	{
		assertTrue(OwnedItemNames.matches("Teleport to house (tablet)", "Teleport to house"));
		assertTrue(OwnedItemNames.matches("Teleport to house", "Teleport to house (tablet)"));
		assertTrue(OwnedItemNames.matches("Teleport to house (tablet)", "Teleport to house tablet"));
		assertTrue(OwnedItemNames.matches("Teleport to house tablet", "Teleport to house (tablet)"));
		assertTrue(OwnedItemNames.matches("Teleport to house (tablet)", "House teleport"));
		assertEquals("teleport to house", OwnedItemNames.familyKey("Teleport to house tablet"));
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
	public void matchesRecoiledRingsOfSuffering()
	{
		assertTrue(OwnedItemNames.matches("Ring of suffering (i)", "Ring of suffering (ri)"));
		assertTrue(OwnedItemNames.matches("Ring of suffering", "Ring of suffering (r)"));
		assertTrue(OwnedItemNames.sameItem("Ring of suffering (i)", "Ring of suffering (ri)"));
		assertEquals("ring of suffering (i)", OwnedItemNames.normalize("Ring of suffering (ri)"));
		assertEquals(
			"Ring of suffering (ri)",
			OwnedItemNames.preferredOwnedName("Ring of suffering (i)", List.of("Ring of suffering (ri)")));
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

	@Test
	public void familyKeyGroupsRecolorsAndOrnamentsTogether()
	{
		assertEquals(
			"slayer helmet (i)",
			OwnedItemNames.familyKey("Twisted slayer helmet (i)"));
		assertEquals(
			"slayer helmet (i)",
			OwnedItemNames.familyKey("Slayer helmet (i)"));
		assertEquals(
			"slayer helmet",
			OwnedItemNames.familyKey("Black slayer helmet"));
		assertEquals("imbued god cape", OwnedItemNames.familyKey("Imbued Guthix cape"));
		assertEquals("imbued god cape", OwnedItemNames.familyKey("Imbued Saradomin cape"));
		assertEquals("imbued god cape", OwnedItemNames.familyKey("Imbued god cape"));
		assertEquals("toxic blowpipe", OwnedItemNames.familyKey("Blazing blowpipe"));
		assertEquals("toxic blowpipe", OwnedItemNames.familyKey("Toxic blowpipe"));
		assertEquals("amulet of torture", OwnedItemNames.familyKey("Amulet of torture (or)"));
		assertEquals("infinity hat", OwnedItemNames.familyKey("Dark infinity hat"));
	}

	@Test
	public void prefersTheLastEquippedVariantOverALongerRecolor()
	{
		List<String> helms = List.of("Black slayer helmet (i)", "Twisted slayer helmet (i)");
		assertEquals(
			"Twisted slayer helmet (i)",
			OwnedItemNames.preferredOwnedName("Slayer helmet (i)", helms));
		assertEquals(
			"Black slayer helmet (i)",
			OwnedItemNames.preferredOwnedName(
				"Slayer helmet (i)",
				helms,
				Map.of(OwnedItemNames.familyKey("Black slayer helmet (i)"), "Black slayer helmet (i)")));
	}

	@Test
	public void keepsTheWikiNameWhenTheBaseVariantWasEquippedLast()
	{
		assertEquals(
			null,
			OwnedItemNames.preferredOwnedName(
				"Slayer helmet (i)",
				List.of("Slayer helmet (i)", "Twisted slayer helmet (i)"),
				Map.of(OwnedItemNames.familyKey("Slayer helmet (i)"), "Slayer helmet (i)")));
		assertEquals(
			null,
			OwnedItemNames.preferredOwnedName(
				"Toxic blowpipe",
				List.of("Toxic blowpipe", "Blazing blowpipe"),
				Map.of(OwnedItemNames.familyKey("Toxic blowpipe"), "Toxic blowpipe")));
	}

	@Test
	public void prefersTheLastEquippedGodCape()
	{
		List<String> capes = List.of("Imbued Saradomin cape", "Imbued Guthix cape");
		assertEquals(
			"Imbued Saradomin cape",
			OwnedItemNames.preferredOwnedName("Imbued god cape", capes));
		assertEquals(
			"Imbued Guthix cape",
			OwnedItemNames.preferredOwnedName(
				"Imbued god cape",
				capes,
				Map.of(OwnedItemNames.familyKey("Imbued Guthix cape"), "Imbued Guthix cape")));
		assertEquals(
			null,
			OwnedItemNames.preferredOwnedName(
				"Imbued Saradomin cape",
				capes,
				Map.of(OwnedItemNames.familyKey("Imbued Saradomin cape"), "Imbued Saradomin cape")));
	}

	@Test
	public void ignoresLastEquippedWhenThatVariantIsNoLongerOwned()
	{
		assertEquals(
			"Twisted slayer helmet (i)",
			OwnedItemNames.preferredOwnedName(
				"Slayer helmet (i)",
				List.of("Twisted slayer helmet (i)"),
				Map.of(OwnedItemNames.familyKey("Black slayer helmet (i)"), "Black slayer helmet (i)")));
	}
}
