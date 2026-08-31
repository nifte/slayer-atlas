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
		addFromText(names, monster.getWeakness());
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
		if (lower.contains("fungicide"))
		{
			names.add("Fungicide spray");
			names.add("Fungicide");
		}
		addIfMentioned(names, lower, "ice cooler", "Ice cooler");
		addIfMentioned(names, lower, "bag of salt", "Bag of salt");
		addIfMentioned(names, lower, "rock hammer", "Rock hammer");
		addIfMentioned(names, lower, "rock thrownhammer", "Rock hammer");
		addIfMentioned(names, lower, "slayer bell", "Slayer bell");
		addIfMentioned(names, lower, "light source", "Bullseye lantern");
		addIfMentioned(names, lower, "bug lantern", "Lit bug lantern");
		addIfMentioned(names, lower, "lockpick", "Lockpick");
		addIfMentioned(names, lower, "spade", "Spade");
	}

	private static void addIfMentioned(Set<String> names, String lower, String needle, String item)
	{
		if (lower.contains(needle))
		{
			names.add(item);
		}
	}
}
