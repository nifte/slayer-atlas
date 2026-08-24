package com.slayerguide.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CurrentTaskVisibilityTest
{
	@Test
	public void onlyOnEmptySearchListWithATask()
	{
		assertTrue(CurrentTaskVisibility.visible(true, true, true));
		assertFalse(CurrentTaskVisibility.visible(true, true, false));
		assertFalse(CurrentTaskVisibility.visible(true, false, true));
		assertFalse(CurrentTaskVisibility.visible(false, true, true));
		assertFalse(CurrentTaskVisibility.visible(false, false, false));
	}
}
