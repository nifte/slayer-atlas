package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.Test;

public class LoadoutPotionFilterTest
{
	@Test
	public void keepsStoredPotionsThatAreOnTheLoadoutAndNotAlreadyShown()
	{
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(List.of(
			"Prayer potion",
			"Divine super combat potion",
			"Shark"));
		List<PotionStorageSlot> extra = LoadoutPotionFilter.extra(
			List.of(
				new PotionStorageSlot(1, 12, 0),
				new PotionStorageSlot(2, 4, 5),
				new PotionStorageSlot(3, 8, 10)),
			matcher,
			List.of("Prayer potion(4)"),
			id -> Map.of(1, "Prayer potion(4)", 2, "Divine super combat potion(4)", 3, "Stamina potion(4)").get(id));
		assertEquals(1, extra.size());
		assertEquals(2, extra.get(0).itemId());
	}

	@Test
	public void skipsEmptyOrUnrelatedStorageSlots()
	{
		LoadoutBankMatcher matcher = LoadoutBankMatcher.of(List.of("Prayer potion"));
		List<PotionStorageSlot> extra = LoadoutPotionFilter.extra(
			List.of(
				new PotionStorageSlot(1, 0, 0),
				new PotionStorageSlot(2, 3, 5)),
			matcher,
			List.of(),
			id -> id == 2 ? "Shark" : "Prayer potion");
		assertTrue(extra.isEmpty());
	}
}
