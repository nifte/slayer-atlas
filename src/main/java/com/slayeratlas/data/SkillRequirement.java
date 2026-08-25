package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public final class SkillRequirement
{
	private static final Pattern LEVEL = Pattern.compile(
		"(?i)^\\d+\\s+(Attack|Strength|Defence|Defense|Hitpoints|Ranged|Prayer|Magic|"
			+ "Cooking|Woodcutting|Fletching|Fishing|Firemaking|Crafting|Smithing|Mining|"
			+ "Herblore|Agility|Thieving|Slayer|Farming|Runecraft(?:ing)?|Hunter|"
			+ "Construction|Sailing|Combat)\\b.*");

	private SkillRequirement()
	{
	}

	public static boolean isLevel(String text)
	{
		return text != null && LEVEL.matcher(text.trim()).matches();
	}

	public static List<String> items(List<String> values)
	{
		return filter(values, false);
	}

	public static List<String> levels(List<String> values)
	{
		return filter(values, true);
	}

	private static List<String> filter(List<String> values, boolean levels)
	{
		if (values == null || values.isEmpty())
		{
			return Collections.emptyList();
		}
		List<String> found = new ArrayList<>();
		for (String value : values)
		{
			if (isLevel(value) == levels)
			{
				found.add(value);
			}
		}
		return found;
	}
}
