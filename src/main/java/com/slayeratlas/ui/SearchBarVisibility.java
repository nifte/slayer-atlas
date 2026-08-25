package com.slayeratlas.ui;

public final class SearchBarVisibility
{
	private SearchBarVisibility()
	{
	}

	public static boolean visible(boolean showingDetail)
	{
		return !showingDetail;
	}
}
