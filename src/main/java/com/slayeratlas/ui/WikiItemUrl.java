package com.slayeratlas.ui;

import java.net.URI;
import java.net.URISyntaxException;

public final class WikiItemUrl
{
	private static final String HOST = "oldschool.runescape.wiki";

	private WikiItemUrl()
	{
	}

	public static String fromName(String name)
	{
		if (name == null || name.isEmpty())
		{
			return "";
		}
		try
		{
			URI uri = new URI("https", HOST, "/w/" + name.trim().replace(' ', '_'), null);
			return uri.toASCIIString();
		}
		catch (URISyntaxException ex)
		{
			return "";
		}
	}
}
