package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class LoadoutBankMatcherTest
{
	@Test
	public void matchesChargedAndImbuedFormsOfTheSameLoadoutItem()
	{
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(List.of(
			"Amulet of glory",
			"Prayer potion",
			"Slayer helmet (i)",
			"Toxic blowpipe"));
		assertTrue(matcher.matches("Amulet of glory (6)"));
		assertTrue(matcher.matches("Prayer potion(3)"));
		assertTrue(matcher.matches("Slayer helmet (imbued)"));
		assertTrue(matcher.matches("Toxic blowpipe"));
		assertFalse(matcher.matches("Shark"));
		assertFalse(matcher.matches(null));
		assertFalse(matcher.matches("null"));
	}

	@Test
	public void doesNotTreatSlayerHelmetRecolorsAsTheLoadoutHelmet()
	{
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(List.of("Slayer helmet (i)"));
		assertTrue(matcher.matches("Slayer helmet (i)"));
		assertFalse(matcher.matches("Black slayer helmet (i)"));
		assertFalse(matcher.matches("Twisted slayer helmet (i)"));
		assertFalse(matcher.matches("Hydra slayer helmet (imbued)"));
		assertFalse(matcher.matches("Slayer helmet"));
	}

	@Test
	public void doesNotTreatOrnamentKitsAsASecondCopyOfTheLoadoutItem()
	{
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(List.of("Toxic blowpipe", "Ava's assembler"));
		assertTrue(matcher.matches("Toxic blowpipe"));
		assertFalse(matcher.matches("Blazing blowpipe"));
		assertTrue(matcher.matches("Ava's assembler (l)"));
		assertFalse(matcher.matches("Masori assembler"));
	}

	@Test
	public void matchesOnlyTheLastEquippedHelmetRecolorFromTheLoadout()
	{
		OwnedItems owned = OwnedItems.withBank(
			Set.of("Black slayer helmet (i)", "Twisted slayer helmet (i)"),
			Map.of(OwnedItemNames.familyKey("Black slayer helmet (i)"), "Black slayer helmet (i)"));
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(List.of(
			owned.shownAs(GearItem.named("Slayer helmet (i)")).getName()));
		assertTrue(matcher.matches("Black slayer helmet (i)"));
		assertFalse(matcher.matches("Twisted slayer helmet (i)"));
		assertFalse(matcher.matches("Slayer helmet (i)"));
	}

	@Test
	public void sameItemsIgnoresMatcherIdentity()
	{
		List<String> names = List.of("Abyssal whip", "Shark");
		LoadoutBankMatcher first = LoadoutBankMatcher.of(names);
		LoadoutBankMatcher second = LoadoutBankMatcher.of(names);
		assertTrue(first.sameItems(second));
		assertFalse(first.sameItems(LoadoutBankMatcher.of(List.of("Abyssal whip"))));
		assertFalse(first.sameItems(null));
	}

	@Test
	public void buildsFromALoadout()
	{
		GearLoadout loadout = PlayerLoadouts.named(
			CombatStyle.MELEE,
			java.util.Map.of(EquipmentSlot.WEAPON, "Abyssal whip"),
			List.of("Shark"));
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(loadout);
		assertTrue(matcher.matches("Abyssal whip"));
		assertTrue(matcher.matches("Shark"));
		assertFalse(matcher.matches("Trout"));
	}
}
