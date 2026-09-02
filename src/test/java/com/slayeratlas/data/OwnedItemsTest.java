package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
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

	@Test
	public void showsTheLastEquippedHelmetRecolor()
	{
		OwnedItems owned = OwnedItems.withBank(
			Set.of("Black slayer helmet (i)", "Twisted slayer helmet (i)"),
			Map.of(OwnedItemNames.familyKey("Black slayer helmet (i)"), "Black slayer helmet (i)"));
		assertEquals(
			"Black slayer helmet (i)",
			owned.shownAs(GearItem.named("Slayer helmet (i)")).getName());
		assertEquals(
			"Black slayer helmet (i).png",
			owned.shownAs(GearItem.named("Slayer helmet (i)")).getImageFile());
	}

	@Test
	public void treatsInGameSalveAmuletAsTheSpacedWikiName()
	{
		GearItem wiki = GearItem.named("Salve amulet (ei)");
		assertTrue(OwnedItems.withBank(Set.of("Salve amulet(ei)")).contains(wiki));
		assertTrue(OwnedItems.withoutBank(Set.of("Salve amulet(ei)")).contains(wiki));
		assertFalse(OwnedItems.withBank(Set.of("Salve amulet (i)")).contains(wiki));
	}

	@Test
	public void treatsInGameHouseTabletsAsTheWikiHouseTablet()
	{
		GearItem wiki = GearItem.named("Teleport to house (tablet)");
		assertTrue(OwnedItems.withBank(Set.of("Teleport to house")).contains(wiki));
		assertTrue(OwnedItems.withoutBank(Set.of("Teleport to house")).contains(wiki));
		assertTrue(OwnedItems.withBank(Set.of("Teleport to house tablet")).contains(wiki));
		assertTrue(OwnedItems.withoutBank(Set.of("House teleport")).contains(wiki));
	}

	@Test
	public void treatsDifferentLastEquippedVariantsAsUnequal()
	{
		Set<String> names = Set.of("Black slayer helmet (i)", "Twisted slayer helmet (i)");
		OwnedItems black = OwnedItems.withBank(
			names,
			Map.of(OwnedItemNames.familyKey("Black slayer helmet (i)"), "Black slayer helmet (i)"));
		OwnedItems twisted = OwnedItems.withBank(
			names,
			Map.of(OwnedItemNames.familyKey("Twisted slayer helmet (i)"), "Twisted slayer helmet (i)"));
		assertFalse(black.equals(twisted));
	}
}
