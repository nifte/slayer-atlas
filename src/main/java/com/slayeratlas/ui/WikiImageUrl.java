package com.slayeratlas.ui;

import com.slayeratlas.data.SlayerMonster;
import java.net.URI;
import java.net.URISyntaxException;

public final class WikiImageUrl
{
	private static final String HOST = "oldschool.runescape.wiki";
	private static final String PATH_PREFIX = "/w/Special:Redirect/file/";

	private WikiImageUrl()
	{
	}

	public static String fileName(SlayerMonster monster)
	{
		if (monster == null)
		{
			return "";
		}
		if (monster.getImage() != null && !monster.getImage().isEmpty())
		{
			return monster.getImage();
		}
		if (monster.getName() == null || monster.getName().isEmpty())
		{
			return "";
		}
		return monster.getName() + ".png";
	}

	public static String fromFileName(String fileName)
	{
		return fromFileName(fileName, 0);
	}

	public static String fromFileName(String fileName, int width)
	{
		if (fileName == null || fileName.isEmpty())
		{
			return "";
		}
		try
		{
			String query = width > 0 ? "width=" + width : null;
			URI uri = new URI(
				"https",
				HOST,
				PATH_PREFIX + fileName.trim().replace(' ', '_'),
				query,
				null);
			return uri.toASCIIString();
		}
		catch (URISyntaxException ex)
		{
			return "";
		}
	}
}
