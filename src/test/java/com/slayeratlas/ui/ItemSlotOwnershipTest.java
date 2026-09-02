package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearRecommendation;
import com.slayeratlas.data.OwnedItems;
import java.util.Set;
import org.junit.Test;

public class ItemSlotOwnershipTest
{
	@Test
	public void greensCarriedItemsBeforeMarkingThemMissing()
	{
		ItemSlotOwnership ownership = ItemSlotOwnership.of(
			OwnedItems.withoutBank(Set.of("Ghrazi rapier")),
			GearRecommendation.of(false, OwnedItems.withBank(Set.of("Shark"))));
		assertEquals(ItemSlot.HELD_BACKGROUND, ownership.background(GearItem.named("Ghrazi rapier")));
		assertEquals(ItemSlot.EMPTY_BACKGROUND, ownership.background(GearItem.named("Shark")));
		assertEquals(ItemSlot.MISSING_BACKGROUND, ownership.background(GearItem.named("Torva platebody")));
		assertEquals(ItemSlot.EMPTY_BACKGROUND, ownership.background(null));
	}

	@Test
	public void treatsCosmeticVariantsAsOwned()
	{
		ItemSlotOwnership ownership = ItemSlotOwnership.of(
			OwnedItems.none(),
			GearRecommendation.of(false, OwnedItems.withBank(Set.of("Amulet of torture (or)"))));
		assertEquals(ItemSlot.EMPTY_BACKGROUND, ownership.background(GearItem.named("Amulet of torture")));
	}

	@Test
	public void treatsInGameSalveAmuletAsOwned()
	{
		ItemSlotOwnership banked = ItemSlotOwnership.of(
			OwnedItems.none(),
			GearRecommendation.of(false, OwnedItems.withBank(Set.of("Salve amulet(ei)"))));
		ItemSlotOwnership carried = ItemSlotOwnership.of(
			OwnedItems.withoutBank(Set.of("Salve amulet(ei)")),
			GearRecommendation.of(false, OwnedItems.withBank(Set.of("Salve amulet(ei)"))));
		assertEquals(ItemSlot.EMPTY_BACKGROUND, banked.background(GearItem.named("Salve amulet (ei)")));
		assertEquals(ItemSlot.HELD_BACKGROUND, carried.background(GearItem.named("Salve amulet (ei)")));
	}

	@Test
	public void treatsInGameHouseTabletsAsOwnedWikiTablets()
	{
		ItemSlotOwnership banked = ItemSlotOwnership.of(
			OwnedItems.none(),
			GearRecommendation.of(false, OwnedItems.withBank(Set.of("Teleport to house"))));
		ItemSlotOwnership carried = ItemSlotOwnership.of(
			OwnedItems.withoutBank(Set.of("Teleport to house tablet")),
			GearRecommendation.of(false, OwnedItems.withBank(Set.of("Teleport to house tablet"))));
		assertEquals(ItemSlot.EMPTY_BACKGROUND, banked.background(GearItem.named("Teleport to house (tablet)")));
		assertEquals(ItemSlot.HELD_BACKGROUND, carried.background(GearItem.named("Teleport to house (tablet)")));
	}

	@Test
	public void doesNotMarkMissingWhenOnlyOwnedRecommendationsAreOn()
	{
		ItemSlotOwnership ownership = ItemSlotOwnership.of(
			OwnedItems.none(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Shark"))));
		assertEquals(ItemSlot.EMPTY_BACKGROUND, ownership.background(GearItem.named("Torva platebody")));
	}

	@Test
	public void doesNotMarkMissingUntilABankSnapshotExists()
	{
		ItemSlotOwnership ownership = ItemSlotOwnership.of(
			OwnedItems.none(),
			GearRecommendation.of(false, OwnedItems.withoutBank(Set.of("Shark"))));
		assertEquals(ItemSlot.EMPTY_BACKGROUND, ownership.background(GearItem.named("Torva platebody")));
	}
}
