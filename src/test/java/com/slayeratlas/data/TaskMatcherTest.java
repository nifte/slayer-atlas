package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaskMatcherTest
{
	@Test
	public void normalizesCaseAndPunctuation()
	{
		assertEquals("cave kraken", TaskMatcher.normalize("Cave-kraken"));
		assertEquals("fossil island wyverns", TaskMatcher.normalize("Fossil Island Wyverns"));
	}

	@Test
	public void matchesPlurals()
	{
		assertTrue(TaskMatcher.namesMatch("Banshees", "Banshee"));
		assertTrue(TaskMatcher.namesMatch("Dust devil", "Dust devils"));
		assertTrue(TaskMatcher.namesMatch("Jellies", "Jelly"));
		assertFalse(TaskMatcher.namesMatch("Black dragons", "Red dragons"));
	}
}
