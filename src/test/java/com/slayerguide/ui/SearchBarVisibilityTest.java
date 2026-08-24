package com.slayerguide.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SearchBarVisibilityTest
{
	@Test
	public void hiddenOnMonsterDetail()
	{
		assertTrue(SearchBarVisibility.visible(false));
		assertFalse(SearchBarVisibility.visible(true));
	}
}
