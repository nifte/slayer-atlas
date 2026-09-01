package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SpecialInventoryItems
{
	private SpecialInventoryItems()
	{
	}

	public static List<GearItem> forMonster(SlayerMonster monster)
	{
		Set<String> names = new LinkedHashSet<>();
		addFromTexts(names, monster);
		List<GearItem> items = new ArrayList<>();
		for (String name : names)
		{
			GearItem item = GearItem.named(name);
			if (item != null)
			{
				items.add(item);
			}
		}
		return items;
	}

	private static void addFromTexts(Set<String> names, SlayerMonster monster)
	{
		if (monster == null)
		{
			return;
		}
		addFromText(names, monster.getNotes());
		if (monster.getRequiredItems() == null)
		{
			return;
		}
		for (String required : monster.getRequiredItems())
		{
			addFromText(names, required);
		}
	}

	private static void addFromText(Set<String> names, String text)
	{
		if (text == null || text.isEmpty())
		{
			return;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		addIfMentioned(names, lower, "fishing explosive", "Fishing explosive");
		if (mentioned(lower, "fungicide"))
		{
			names.add("Fungicide spray");
			names.add("Fungicide");
		}
		addIfMentioned(names, lower, "ice cooler", "Ice cooler");
		addIfMentioned(names, lower, "bag of salt", "Bag of salt");
		addIfMentioned(names, lower, "rock hammer", "Rock hammer");
		addIfMentioned(names, lower, "rock thrownhammer", "Rock hammer");
		addIfMentioned(names, lower, "slayer bell", "Slayer bell");
		addIfMentioned(names, lower, "crystal chime", "Crystal chime");
		addIfMentioned(names, lower, "light source", "Bullseye lantern");
		addIfMentioned(names, lower, "bullseye lantern", "Bullseye lantern");
		addIfMentioned(names, lower, "lockpick", "Lockpick");
		addIfMentioned(names, lower, "spade", "Spade");
	}

	private static void addIfMentioned(Set<String> names, String lower, String needle, String item)
	{
		if (mentioned(lower, needle))
		{
			names.add(item);
		}
	}

	private static boolean mentioned(String lower, String needle)
	{
		int index = 0;
		while (index < lower.length())
		{
			int found = lower.indexOf(needle, index);
			if (found < 0)
			{
				return false;
			}
			if (!negated(lower, found))
			{
				return true;
			}
			index = found + needle.length();
		}
		return false;
	}

	private static boolean negated(String lower, int found)
	{
		String before = lower.substring(Math.max(0, found - 16), found);
		return before.contains("no ") || before.contains("not ") || before.contains("without ");
	}
}
