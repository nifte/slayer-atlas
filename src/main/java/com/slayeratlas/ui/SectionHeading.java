package com.slayeratlas.ui;

import java.util.Locale;

public final class SectionHeading
{
	private SectionHeading()
	{
	}

	public static String display(String heading)
	{
		if (heading == null || heading.isEmpty())
		{
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (String word : heading.trim().split("\\s+"))
		{
			if (builder.length() > 0)
			{
				builder.append(' ');
			}
			builder.append(titleWord(word));
		}
		return builder.toString();
	}

	private static String titleWord(String word)
	{
		if (word.isEmpty() || "&".equals(word))
		{
			return word;
		}
		if (word.length() == 1)
		{
			return word.toUpperCase(Locale.ROOT);
		}
		return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase(Locale.ROOT);
	}
}
