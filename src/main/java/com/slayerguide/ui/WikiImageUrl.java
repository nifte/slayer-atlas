package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
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
		if (fileName == null || fileName.isEmpty())
		{
			return "";
		}
		try
		{
			URI uri = new URI("https", HOST, PATH_PREFIX + fileName.trim().replace(' ', '_'), null);
			return uri.toASCIIString();
		}
		catch (URISyntaxException ex)
		{
			return "";
		}
	}
}
