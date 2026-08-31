package com.slayeratlas.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class WikiEquipmentTable
{
	private final RankedGearLoadout ranked;

	public WikiEquipmentTable(RankedGearLoadout ranked)
	{
		this.ranked = ranked;
	}

	public String getPageName()
	{
		return ranked.getPageName();
	}

	public String getCaption()
	{
		return ranked.getCaption();
	}

	public CombatStyle getStyle()
	{
		return ranked.getStyle();
	}

	public GearLoadout toLoadout()
	{
		return ranked.toLoadout();
	}

	public RankedGearLoadout toRanked()
	{
		return ranked;
	}

	int score()
	{
		int score = filledSlots();
		if (ranked.isPrimary())
		{
			score += 100;
		}
		String lower = ranked.getCaption().toLowerCase(Locale.ROOT);
		if (lower.contains("bis"))
		{
			score += 80;
		}
		if (lower.contains("4-item") || lower.contains("budget") || lower.contains("mid")
			|| lower.contains("cheap") || lower.contains("entry"))
		{
			score -= 50;
		}
		String page = ranked.getPageName().toLowerCase(Locale.ROOT);
		if (page.endsWith("/strategies"))
		{
			score += 20;
		}
		if (page.startsWith("slayer task/"))
		{
			score -= 10;
		}
		return score;
	}

	private int filledSlots()
	{
		int filled = 0;
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (slot.onWornGrid() && !ranked.ranks(slot).isEmpty())
			{
				filled++;
			}
		}
		return filled;
	}

	public static WikiEquipmentTable parse(Gson gson, String pageName, String json)
	{
		if (json == null || json.isEmpty())
		{
			return null;
		}
		JsonObject root = gson.fromJson(json, JsonObject.class);
		if (root == null)
		{
			return null;
		}
		String caption = "";
		if (root.has("style") && !root.get("style").isJsonNull())
		{
			caption = root.get("style").getAsString();
		}
		CombatStyle style = CombatStyle.fromCaption(caption);
		if (style == null)
		{
			return null;
		}
		JsonObject slots = root.getAsJsonObject("Recommended Equipment");
		if (slots == null)
		{
			return null;
		}
		Map<EquipmentSlot, List<GearItem>> ranks = new EnumMap<>(EquipmentSlot.class);
		List<GearItem> specials = new ArrayList<>();
		for (Map.Entry<String, JsonElement> entry : slots.entrySet())
		{
			EquipmentSlot slot = EquipmentSlot.fromWikiKey(entry.getKey());
			if (slot == null)
			{
				continue;
			}
			List<GearItem> items = items(entry.getValue());
			if (items.isEmpty())
			{
				continue;
			}
			if (slot == EquipmentSlot.TWO_HAND)
			{
				merge(ranks, EquipmentSlot.WEAPON, items);
			}
			else if (slot == EquipmentSlot.SPECIAL)
			{
				addUnique(specials, items);
			}
			else
			{
				merge(ranks, slot, items);
			}
		}
		if (ranks.isEmpty())
		{
			return null;
		}
		boolean primary = caption.trim().equalsIgnoreCase(style.displayName());
		return new WikiEquipmentTable(new RankedGearLoadout(pageName, caption, style, primary, ranks, specials));
	}

	private static void merge(Map<EquipmentSlot, List<GearItem>> ranks, EquipmentSlot slot, List<GearItem> items)
	{
		List<GearItem> existing = ranks.computeIfAbsent(slot, key -> new ArrayList<>());
		addUnique(existing, items);
	}

	private static void addUnique(List<GearItem> target, List<GearItem> items)
	{
		for (GearItem item : items)
		{
			if (item == null || containsName(target, item.getName()))
			{
				continue;
			}
			target.add(item);
		}
	}

	private static boolean containsName(List<GearItem> items, String name)
	{
		if (name == null)
		{
			return false;
		}
		for (GearItem item : items)
		{
			if (item != null && name.equalsIgnoreCase(item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	private static List<GearItem> items(JsonElement value)
	{
		List<GearItem> parsed = new ArrayList<>();
		collect(value, parsed);
		return parsed;
	}

	private static void collect(JsonElement value, List<GearItem> parsed)
	{
		if (value == null || value.isJsonNull())
		{
			return;
		}
		if (value.isJsonPrimitive())
		{
			addUnique(parsed, WikiGearText.items(value.getAsString()));
			return;
		}
		if (value.isJsonArray())
		{
			for (JsonElement element : value.getAsJsonArray())
			{
				collect(element, parsed);
			}
			return;
		}
		if (value.isJsonObject())
		{
			for (Map.Entry<String, JsonElement> nested : value.getAsJsonObject().entrySet())
			{
				collect(nested.getValue(), parsed);
			}
		}
	}
}
