package com.slayeratlas;

public final class NewTaskFollow
{
	private NewTaskFollow()
	{
	}

	public static boolean isNewLiveAssignment(
		boolean liveAssignment,
		boolean assignmentChanged,
		boolean hasTask)
	{
		return liveAssignment && assignmentChanged && hasTask;
	}

	public static boolean shouldFollow(boolean newLiveAssignment, boolean openPanelEnabled)
	{
		return newLiveAssignment && openPanelEnabled;
	}
}
