package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class WikiInventoryText
{
	private static final Set<String> WORN_KEYS = Set.of(
		"head",
		"cape",
		"neck",
		"ammo",
		"weapon",
		"body",
		"shield",
		"legs",
		"hands",
		"feet",
		"ring",
		"2h",
		"special");

	private WikiInventoryText()
	{
	}

	public static List<GearItem> parse(String wikitext)
	{
		if (wikitext == null || wikitext.isEmpty())
		{
			return List.of();
		}
		List<List<GearItem>> inventories = new ArrayList<>();
		List<GearItem> loadoutItems = new ArrayList<>();
		int index = 0;
		String lower = wikitext.toLowerCase(Locale.ROOT);
		while (index < wikitext.length())
		{
			int inventoryAt = lower.indexOf("{{inventory", index);
			int loadoutAt = lower.indexOf("{{loadout", index);
			int start = nextTemplate(inventoryAt, loadoutAt);
			if (start < 0)
			{
				break;
			}
			int end = templateEnd(wikitext, start);
			if (end < 0)
			{
				break;
			}
			String template = wikitext.substring(start, end);
			if (inventoryAt >= 0 && start == inventoryAt)
			{
				List<GearItem> slots = collectInventory(template);
				if (!slots.isEmpty())
				{
					inventories.add(slots);
				}
			}
			else
			{
				collectLoadout(template, loadoutItems);
			}
			index = end + 2;
		}
		List<GearItem> inventory = longest(inventories);
		if (!inventory.isEmpty())
		{
			return inventory;
		}
		return loadoutItems;
	}

	private static int nextTemplate(int inventoryAt, int loadoutAt)
	{
		if (inventoryAt < 0)
		{
			return loadoutAt;
		}
		if (loadoutAt < 0)
		{
			return inventoryAt;
		}
		return Math.min(inventoryAt, loadoutAt);
	}

	private static int templateEnd(String text, int start)
	{
		int depth = 0;
		for (int index = start; index < text.length() - 1; index++)
		{
			if (text.charAt(index) == '{' && text.charAt(index + 1) == '{')
			{
				depth++;
				index++;
			}
			else if (text.charAt(index) == '}' && text.charAt(index + 1) == '}')
			{
				depth--;
				if (depth == 0)
				{
					return index;
				}
				index++;
			}
		}
		return -1;
	}

	private static List<GearItem> collectInventory(String template)
	{
		List<GearItem> items = new ArrayList<>();
		int pipe = template.indexOf('|');
		if (pipe < 0)
		{
			return items;
		}
		for (String part : splitParams(template.substring(pipe + 1)))
		{
			String trimmed = part.trim();
			if (trimmed.isEmpty())
			{
				items.add(null);
				continue;
			}
			int equals = trimmed.indexOf('=');
			if (equals >= 0)
			{
				String key = trimmed.substring(0, equals).trim().toLowerCase(Locale.ROOT);
				if (isMeta(key))
				{
					continue;
				}
				trimmed = trimmed.substring(equals + 1).trim();
			}
			trimmed = stripQuantity(trimmed);
			if (trimmed.isEmpty())
			{
				items.add(null);
				continue;
			}
			items.add(WikiGearText.firstItem(trimmed));
		}
		return items;
	}

	private static void collectLoadout(String template, List<GearItem> items)
	{
		int pipe = template.indexOf('|');
		if (pipe < 0)
		{
			return;
		}
		for (String part : splitParams(template.substring(pipe + 1)))
		{
			String trimmed = part.trim();
			if (trimmed.isEmpty())
			{
				continue;
			}
			int equals = trimmed.indexOf('=');
			if (equals >= 0)
			{
				String key = trimmed.substring(0, equals).trim().toLowerCase(Locale.ROOT);
				if (isMeta(key) || WORN_KEYS.contains(key))
				{
					continue;
				}
				trimmed = trimmed.substring(equals + 1).trim();
			}
			if (trimmed.isEmpty())
			{
				continue;
			}
			GearItem item = WikiGearText.firstItem(trimmed);
			if (item != null)
			{
				items.add(item);
			}
		}
	}

	private static List<GearItem> longest(List<List<GearItem>> inventories)
	{
		List<GearItem> best = List.of();
		for (List<GearItem> inventory : inventories)
		{
			if (inventory.size() > best.size())
			{
				best = inventory;
			}
		}
		return best;
	}

	private static String stripQuantity(String value)
	{
		int slash = value.indexOf('\\');
		if (slash < 0)
		{
			return value;
		}
		return value.substring(0, slash).trim();
	}

	private static List<String> splitParams(String body)
	{
		List<String> parts = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		int depth = 0;
		for (int index = 0; index < body.length(); index++)
		{
			if (index + 1 < body.length() && body.charAt(index) == '{' && body.charAt(index + 1) == '{')
			{
				depth++;
				current.append("{{");
				index++;
			}
			else if (index + 1 < body.length() && body.charAt(index) == '}' && body.charAt(index + 1) == '}')
			{
				depth--;
				current.append("}}");
				index++;
			}
			else if (body.charAt(index) == '|' && depth == 0)
			{
				parts.add(current.toString());
				current.setLength(0);
			}
			else
			{
				current.append(body.charAt(index));
			}
		}
		parts.add(current.toString());
		return parts;
	}

	private static boolean isMeta(String key)
	{
		return key.equals("align")
			|| key.equals("float")
			|| key.equals("caption")
			|| key.equals("text")
			|| key.equals("invfixed")
			|| key.startsWith("quantity");
	}
}
