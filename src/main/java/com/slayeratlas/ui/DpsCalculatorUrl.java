package com.slayeratlas.ui;

import com.slayeratlas.data.SlayerMonster;

public final class DpsCalculatorUrl
{
	private static final String PREFIX = "https://tools.runescape.wiki/osrs-dps/?monster=";

	private DpsCalculatorUrl()
	{
	}

	public static String fromMonster(SlayerMonster monster)
	{
		if (monster == null)
		{
			return "";
		}
		return sanitize(monster.getDps());
	}

	public static String sanitize(String url)
	{
		if (url == null || url.isEmpty())
		{
			return "";
		}
		String trimmed = url.trim();
		if (!trimmed.startsWith(PREFIX))
		{
			return "";
		}
		String id = trimmed.substring(PREFIX.length());
		if (id.isEmpty())
		{
			return "";
		}
		for (int index = 0; index < id.length(); index++)
		{
			if (!Character.isDigit(id.charAt(index)))
			{
				return "";
			}
		}
		return PREFIX + id;
	}
}
