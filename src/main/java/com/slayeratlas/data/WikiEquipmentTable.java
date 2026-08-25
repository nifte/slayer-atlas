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
	private final String pageName;
	private final String caption;
	private final CombatStyle style;
	private final boolean primary;
	private final Map<EquipmentSlot, GearItem> worn;
	private final List<GearItem> extras;

	public WikiEquipmentTable(
		String pageName,
		String caption,
		CombatStyle style,
		boolean primary,
		Map<EquipmentSlot, GearItem> worn,
		List<GearItem> extras)
	{
		this.pageName = pageName;
		this.caption = caption;
		this.style = style;
		this.primary = primary;
		this.worn = worn;
		this.extras = extras;
	}

	public String getPageName()
	{
		return pageName;
	}

	public String getCaption()
	{
		return caption;
	}

	public CombatStyle getStyle()
	{
		return style;
	}

	public GearLoadout toLoadout()
	{
		return new GearLoadout(style, primary, worn, extras);
	}

	int score()
	{
		int score = worn.size();
		if (primary)
		{
			score += 100;
		}
		String lower = caption == null ? "" : caption.toLowerCase(Locale.ROOT);
		if (lower.contains("bis"))
		{
			score += 80;
		}
		if (lower.contains("4-item") || lower.contains("budget"))
		{
			score -= 50;
		}
		String page = pageName == null ? "" : pageName.toLowerCase(Locale.ROOT);
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
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		List<GearItem> extras = new ArrayList<>();
		for (Map.Entry<String, JsonElement> entry : slots.entrySet())
		{
			EquipmentSlot slot = EquipmentSlot.fromWikiKey(entry.getKey());
			if (slot == null || !entry.getValue().isJsonArray() || entry.getValue().getAsJsonArray().size() == 0)
			{
				continue;
			}
			GearItem item = firstItem(entry.getValue());
			if (item == null)
			{
				continue;
			}
			if (slot == EquipmentSlot.TWO_HAND)
			{
				worn.putIfAbsent(EquipmentSlot.WEAPON, item);
			}
			else if (slot == EquipmentSlot.SPECIAL)
			{
				extras.add(item);
			}
			else
			{
				worn.put(slot, item);
			}
		}
		if (worn.isEmpty())
		{
			return null;
		}
		boolean primary = caption.trim().equalsIgnoreCase(style.displayName());
		return new WikiEquipmentTable(pageName, caption, style, primary, worn, extras);
	}

	private static GearItem firstItem(JsonElement value)
	{
		if (value == null || !value.isJsonArray())
		{
			return null;
		}
		for (JsonElement element : value.getAsJsonArray())
		{
			if (element == null || element.isJsonNull() || !element.isJsonPrimitive())
			{
				continue;
			}
			GearItem item = WikiGearText.firstItem(element.getAsString());
			if (item != null)
			{
				return item;
			}
		}
		return null;
	}
}
