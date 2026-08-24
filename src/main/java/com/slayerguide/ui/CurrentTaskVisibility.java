package com.slayerguide.ui;

public final class CurrentTaskVisibility
{
	private CurrentTaskVisibility()
	{
	}

	public static boolean visible(boolean listVisible, boolean searchEmpty)
	{
		return listVisible && searchEmpty;
	}
}
