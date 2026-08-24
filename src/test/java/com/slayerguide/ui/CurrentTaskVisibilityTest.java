package com.slayerguide.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CurrentTaskVisibilityTest
{
	@Test
	public void onlyOnEmptySearchList()
	{
		assertTrue(CurrentTaskVisibility.visible(true, true));
		assertFalse(CurrentTaskVisibility.visible(true, false));
		assertFalse(CurrentTaskVisibility.visible(false, true));
		assertFalse(CurrentTaskVisibility.visible(false, false));
	}
}
