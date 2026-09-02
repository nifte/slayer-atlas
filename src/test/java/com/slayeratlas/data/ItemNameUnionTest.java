package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import org.junit.Test;

public class ItemNameUnionTest
{
	@Test
	public void treatsMovedItemsAsTheSameCarriedSet()
	{
		assertTrue(ItemNameUnion.same(
			Set.of("Abyssal whip"),
			Set.of("Shark"),
			Set.of(),
			Set.of("Abyssal whip", "Shark")));
		assertTrue(ItemNameUnion.same(
			Set.of("Abyssal whip", "Dragon defender"),
			Set.of("Shark"),
			Set.of("Abyssal whip"),
			Set.of("Dragon defender", "Shark")));
	}

	@Test
	public void treatsADroppedItemAsADifferentCarriedSet()
	{
		assertFalse(ItemNameUnion.same(
			Set.of("Abyssal whip"),
			Set.of("Shark"),
			Set.of("Abyssal whip"),
			Set.of()));
	}

	@Test
	public void treatsIdenticalContainersAsTheSameSet()
	{
		assertTrue(ItemNameUnion.same(
			Set.of("Abyssal whip"),
			Set.of("Shark"),
			Set.of("Abyssal whip"),
			Set.of("Shark")));
	}
}
