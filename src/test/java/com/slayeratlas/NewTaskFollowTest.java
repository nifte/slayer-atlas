package com.slayeratlas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NewTaskFollowTest
{
	@Test
	public void recognizesOnlyALiveNewAssignment()
	{
		assertTrue(NewTaskFollow.isNewLiveAssignment(true, true, true));
		assertFalse(NewTaskFollow.isNewLiveAssignment(false, true, true));
		assertFalse(NewTaskFollow.isNewLiveAssignment(true, false, true));
		assertFalse(NewTaskFollow.isNewLiveAssignment(true, true, false));
	}

	@Test
	public void followsOnlyWhenThePanelSettingIsOn()
	{
		assertTrue(NewTaskFollow.shouldFollow(true, true));
		assertFalse(NewTaskFollow.shouldFollow(true, false));
		assertFalse(NewTaskFollow.shouldFollow(false, true));
	}
}
