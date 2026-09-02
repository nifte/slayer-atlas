package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import org.junit.Test;

public class GearRecommendationTest
{
	@Test
	public void showsTheBankHintUntilThisAccountHasASnapshot()
	{
		assertTrue(GearRecommendation.of(true, OwnedItems.none()).showBankHint());
		assertTrue(GearRecommendation.of(true, OwnedItems.withoutBank(Set.of("Shark"))).showBankHint());
		assertFalse(GearRecommendation.of(true, OwnedItems.withBank(Set.of("Shark"))).showBankHint());
	}

	@Test
	public void hidesTheBankHintWhenOwnedFilteringIsOff()
	{
		assertFalse(GearRecommendation.of(false, OwnedItems.none()).showBankHint());
		assertFalse(GearRecommendation.specialized().showBankHint());
	}

	@Test
	public void filtersToOwnedOnlyAfterABankSnapshotExists()
	{
		assertFalse(GearRecommendation.of(true, OwnedItems.none()).filterToOwned());
		assertTrue(GearRecommendation.of(true, OwnedItems.withBank(Set.of("Shark"))).filterToOwned());
		assertFalse(GearRecommendation.of(false, OwnedItems.withBank(Set.of("Shark"))).filterToOwned());
	}

	@Test
	public void doesNotUseGoadingPotionsByDefault()
	{
		assertFalse(GearRecommendation.specialized().useGoadingPotions());
		assertFalse(GearRecommendation.of(true, OwnedItems.none()).useGoadingPotions());
		assertFalse(GearRecommendation.of(false, OwnedItems.withBank(Set.of("Shark"))).useGoadingPotions());
	}

	@Test
	public void canEnableGoadingPotions()
	{
		assertTrue(GearRecommendation.of(false, true, OwnedItems.none()).useGoadingPotions());
		assertTrue(GearRecommendation.of(true, true, OwnedItems.withBank(Set.of("Goading potion"))).useGoadingPotions());
	}
}
