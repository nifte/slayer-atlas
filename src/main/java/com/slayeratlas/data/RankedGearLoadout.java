package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RankedGearLoadout
{
	private final String pageName;
	private final String caption;
	private final CombatStyle style;
	private final boolean primary;
	private final Map<EquipmentSlot, List<GearItem>> ranks;
	private final List<GearItem> specials;
	private final List<GearItem> wikiInventory;

	public RankedGearLoadout(
		String pageName,
		CombatStyle style,
		boolean primary,
		Map<EquipmentSlot, List<GearItem>> ranks,
		List<GearItem> specials)
	{
		this(pageName, "", style, primary, ranks, specials, List.of());
	}

	public RankedGearLoadout(
		String pageName,
		String caption,
		CombatStyle style,
		boolean primary,
		Map<EquipmentSlot, List<GearItem>> ranks,
		List<GearItem> specials)
	{
		this(pageName, caption, style, primary, ranks, specials, List.of());
	}

	public RankedGearLoadout(
		String pageName,
		CombatStyle style,
		boolean primary,
		Map<EquipmentSlot, List<GearItem>> ranks,
		List<GearItem> specials,
		List<GearItem> wikiInventory)
	{
		this(pageName, "", style, primary, ranks, specials, wikiInventory);
	}

	public RankedGearLoadout(
		String pageName,
		String caption,
		CombatStyle style,
		boolean primary,
		Map<EquipmentSlot, List<GearItem>> ranks,
		List<GearItem> specials,
		List<GearItem> wikiInventory)
	{
		this.pageName = pageName == null ? "" : pageName;
		this.caption = caption == null ? "" : caption;
		this.style = style;
		this.primary = primary;
		Map<EquipmentSlot, List<GearItem>> copy = new EnumMap<>(EquipmentSlot.class);
		if (ranks != null)
		{
			for (Map.Entry<EquipmentSlot, List<GearItem>> entry : ranks.entrySet())
			{
				copy.put(entry.getKey(), List.copyOf(entry.getValue()));
			}
		}
		this.ranks = Collections.unmodifiableMap(copy);
		this.specials = copyItems(specials);
		this.wikiInventory = copyItems(wikiInventory);
	}

	private static List<GearItem> copyItems(List<GearItem> items)
	{
		if (items == null || items.isEmpty())
		{
			return List.of();
		}
		return Collections.unmodifiableList(new ArrayList<>(items));
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

	public boolean isPrimary()
	{
		return primary;
	}

	public List<GearItem> ranks(EquipmentSlot slot)
	{
		List<GearItem> items = ranks.get(slot);
		return items == null ? List.of() : items;
	}

	public Map<EquipmentSlot, List<GearItem>> getRanks()
	{
		return ranks;
	}

	public List<GearItem> getSpecials()
	{
		return specials;
	}

	public List<GearItem> getWikiInventory()
	{
		return wikiInventory;
	}

	public RankedGearLoadout withWikiInventory(List<GearItem> items)
	{
		return new RankedGearLoadout(pageName, caption, style, primary, ranks, specials, items);
	}

	public RankedGearLoadout withRanks(Map<EquipmentSlot, List<GearItem>> updated)
	{
		return new RankedGearLoadout(pageName, caption, style, primary, updated, specials, wikiInventory);
	}

	public GearLoadout toLoadout()
	{
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		for (Map.Entry<EquipmentSlot, List<GearItem>> entry : ranks.entrySet())
		{
			if (!entry.getKey().onWornGrid() || entry.getValue().isEmpty())
			{
				continue;
			}
			worn.put(entry.getKey(), entry.getValue().get(0));
		}
		List<GearItem> extras = new ArrayList<>(specials);
		extras.addAll(wikiInventory);
		return new GearLoadout(style, primary, worn, extras);
	}
}
