package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import org.junit.Test;

public class OwnedItemsTest
{
	@Test
	public void treatsLockedAssemblerAsAvasAssembler()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Ava's assembler (l)"));
		assertTrue(owned.contains(GearItem.named("Ava's assembler")));
		assertEquals("Ava's assembler", owned.shownAs(GearItem.named("Ava's assembler")).getName());
		assertEquals("Ava's assembler.png", owned.shownAs(GearItem.named("Ava's assembler")).getImageFile());
		assertTrue(OwnedItems.withBank(Set.of("Masori assembler (l)"))
			.contains(GearItem.named("Ava's assembler")));
	}

	@Test
	public void treatsChargedGloryAsTheWikiGlory()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Amulet of glory (6)"));
		assertTrue(owned.contains(GearItem.named("Amulet of glory")));
	}

	@Test
	public void treatsPotionStorageDosesAsTheWikiPotion()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Prayer potion(4)"));
		assertTrue(owned.contains(GearItem.named("Prayer potion")));
		assertEquals("Prayer potion", owned.shownAs(GearItem.named("Prayer potion")).getName());
	}

	@Test
	public void treatsImbuedHelmetVariantsAsTheWikiHelmet()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Slayer helmet (imbued)"));
		assertTrue(owned.contains(GearItem.named("Slayer helmet (i)")));
	}

	@Test
	public void treatsBlazingBlowpipeAsToxicBlowpipe()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Blazing blowpipe"));
		assertTrue(owned.contains(GearItem.named("Toxic blowpipe")));
	}

	@Test
	public void treatsDeadmanImbuedCapesAsTheWikiGodCape()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Imbued Zamorak cape (Deadman)"));
		assertTrue(owned.contains(GearItem.named("Imbued god cape")));
		assertTrue(owned.contains(GearItem.named("Imbued Saradomin cape")));
	}

	@Test
	public void showsTheOwnedOrnamentalName()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Blazing blowpipe"));
		assertEquals("Blazing blowpipe", owned.shownAs(GearItem.named("Toxic blowpipe")).getName());
		assertEquals(
			"Imbued Zamorak cape (Deadman)",
			OwnedItems.withBank(Set.of("Imbued Zamorak cape (Deadman)"))
				.shownAs(GearItem.named("Imbued god cape"))
				.getName());
	}

	@Test
	public void keepsWikiPresentationForChargeOnlyDifferences()
	{
		GearItem wiki = GearItem.named("Amulet of glory");
		assertEquals(wiki.getName(), OwnedItems.withBank(Set.of("Amulet of glory (6)")).shownAs(wiki).getName());
	}

	@Test
	public void treatsTheSameOwnedNamesAsEqual()
	{
		OwnedItems first = OwnedItems.withBank(Set.of("Shark", "Prayer potion(4)"));
		OwnedItems second = OwnedItems.withBank(Set.of("Prayer potion(4)", "Shark"));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertFalse(first.equals(OwnedItems.withoutBank(Set.of("Shark", "Prayer potion(4)"))));
		assertFalse(first.equals(OwnedItems.withBank(Set.of("Shark"))));
	}
}
