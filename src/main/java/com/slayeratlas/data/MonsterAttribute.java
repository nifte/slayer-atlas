package com.slayeratlas.data;

import java.util.Locale;

public final class MonsterAttribute
{
	private MonsterAttribute()
	{
	}

	public static String display(String attribute)
	{
		String official = officialName(attribute);
		if (official.isEmpty())
		{
			return "";
		}
		return official.toLowerCase(Locale.ROOT);
	}

	static String officialName(String attribute)
	{
		if (attribute == null)
		{
			return "";
		}
		String name = attribute.trim();
		if (name.isEmpty())
		{
			return "";
		}
		if ("dragon".equalsIgnoreCase(name))
		{
			return "Draconic";
		}
		return name;
	}
}
