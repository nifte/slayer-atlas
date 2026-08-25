package com.slayeratlas.ui;

import com.slayeratlas.data.SkillRequirement;
import com.slayeratlas.data.SlayerMonster;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MonsterNotesText
{
	private MonsterNotesText()
	{
	}

	public static String display(SlayerMonster monster)
	{
		if (monster == null)
		{
			return "";
		}
		List<String> parts = new ArrayList<>();
		for (String requirement : requirements(monster))
		{
			String line = requiresLine(requirement);
			if (!line.isEmpty())
			{
				parts.add(line);
			}
		}
		String notes = monster.getNotes();
		if (notes != null && !notes.isEmpty())
		{
			parts.add(notes.trim());
		}
		return join(parts);
	}

	private static List<String> requirements(SlayerMonster monster)
	{
		List<String> values = new ArrayList<>();
		addUnique(values, monster.getRequirements());
		addUnique(values, SkillRequirement.levels(monster.getRequiredItems()));
		return values;
	}

	private static void addUnique(List<String> values, List<String> extras)
	{
		if (extras == null)
		{
			return;
		}
		for (String extra : extras)
		{
			if (extra != null && !containsIgnoreCase(values, extra))
			{
				values.add(extra);
			}
		}
	}

	private static boolean containsIgnoreCase(List<String> values, String candidate)
	{
		String needle = candidate.trim();
		for (String value : values)
		{
			if (value != null && value.trim().equalsIgnoreCase(needle))
			{
				return true;
			}
		}
		return false;
	}

	private static String requiresLine(String requirement)
	{
		if (requirement == null)
		{
			return "";
		}
		String text = requirement.trim();
		if (text.isEmpty())
		{
			return "";
		}
		String lower = text.toLowerCase(Locale.ROOT);
		if (lower.startsWith("require") || lower.contains("required"))
		{
			return ensurePeriod(text);
		}
		return ensurePeriod("Requires " + text);
	}

	private static String ensurePeriod(String text)
	{
		char last = text.charAt(text.length() - 1);
		if (last == '.' || last == '!' || last == '?')
		{
			return text;
		}
		return text + '.';
	}

	private static String join(List<String> parts)
	{
		if (parts.isEmpty())
		{
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (String part : parts)
		{
			if (builder.length() > 0)
			{
				builder.append(' ');
			}
			builder.append(part);
		}
		return builder.toString();
	}
}
