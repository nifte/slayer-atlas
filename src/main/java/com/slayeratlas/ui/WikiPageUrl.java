package com.slayeratlas.ui;

import java.net.URI;
import java.net.URISyntaxException;

public final class WikiPageUrl
{
	private static final String HOST = "oldschool.runescape.wiki";

	private WikiPageUrl()
	{
	}

	public static String forTitle(String title)
	{
		if (title == null)
		{
			return "";
		}
		String page = title.trim().replace(' ', '_');
		if (page.isEmpty())
		{
			return "";
		}
		try
		{
			return new URI("https", HOST, "/w/" + page, null).toASCIIString();
		}
		catch (URISyntaxException ex)
		{
			return "";
		}
	}
}
