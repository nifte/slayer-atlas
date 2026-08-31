package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;
import org.junit.Test;

public class OwnedGearPickerTest
{
	private static final List<GearItem> NECKS = List.of(
		GearItem.named("Amulet of rancour"),
		GearItem.named("Amulet of torture"),
		GearItem.named("Amulet of glory"));

	@Test
	public void usesWikiRankOneWhenThereIsNoBankSnapshot()
	{
		assertEquals(
			"Amulet of rancour",
			OwnedGearPicker.pick(NECKS, OwnedItems.none(), true).getName());
		assertEquals(
			"Amulet of rancour",
			OwnedGearPicker.pick(NECKS, OwnedItems.withoutBank(Set.of("Amulet of glory")), true).getName());
	}

	@Test
	public void usesTheBestOwnedRankOnceABankSnapshotExists()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Amulet of glory"));
		assertEquals("Amulet of glory", OwnedGearPicker.pick(NECKS, owned, true).getName());
	}

	@Test
	public void recommendsNothingWhenNoneOfTheRowIsOwned()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Shark"));
		assertNull(OwnedGearPicker.pick(NECKS, owned, true));
	}

	@Test
	public void ignoresOwnedItemsWhenTheFilterIsOff()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Amulet of glory"));
		assertEquals("Amulet of rancour", OwnedGearPicker.pick(NECKS, owned, false).getName());
	}

	@Test
	public void recommendsTheOwnedOrnamentalVersion()
	{
		List<GearItem> weapons = List.of(
			GearItem.named("Toxic blowpipe"),
			GearItem.named("Magic shortbow"));
		OwnedItems owned = OwnedItems.withBank(Set.of("Blazing blowpipe"));
		GearItem picked = OwnedGearPicker.pick(weapons, owned, true);
		assertEquals("Blazing blowpipe", picked.getName());
		assertEquals("Blazing blowpipe.png", picked.getImageFile());
	}

	@Test
	public void recommendsTheOwnedDeadmanCape()
	{
		List<GearItem> capes = List.of(GearItem.named("Imbued god cape"));
		OwnedItems owned = OwnedItems.withBank(Set.of("Imbued Zamorak cape (Deadman)"));
		assertEquals(
			"Imbued Zamorak cape (Deadman)",
			OwnedGearPicker.pick(capes, owned, true).getName());
	}

	@Test
	public void recommendsTheOrnamentedTorture()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Amulet of torture (or)"));
		assertEquals("Amulet of torture (or)", OwnedGearPicker.pick(NECKS, owned, true).getName());
	}

	@Test
	public void keepsTheWikiNameWhenOnlyTheChargeCountDiffers()
	{
		OwnedItems owned = OwnedItems.withBank(Set.of("Amulet of glory (6)"));
		assertEquals("Amulet of glory", OwnedGearPicker.pick(NECKS, owned, true).getName());
	}

	@Test
	public void keepsWikiRankOneWhenOwnedFilteringIsOffEvenIfAnOrnamentIsOwned()
	{
		List<GearItem> weapons = List.of(GearItem.named("Toxic blowpipe"));
		OwnedItems owned = OwnedItems.withBank(Set.of("Blazing blowpipe"));
		assertEquals("Toxic blowpipe", OwnedGearPicker.pick(weapons, owned, false).getName());
	}
}
