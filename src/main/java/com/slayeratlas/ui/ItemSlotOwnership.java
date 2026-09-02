package com.slayeratlas.ui;

import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearRecommendation;
import com.slayeratlas.data.OwnedItems;
import java.awt.Color;

public final class ItemSlotOwnership
{
	private static final ItemSlotOwnership NONE = new ItemSlotOwnership(OwnedItems.none(), OwnedItems.none(), false);

	private final OwnedItems carried;
	private final OwnedItems owned;
	private final boolean markMissing;

	private ItemSlotOwnership(OwnedItems carried, OwnedItems owned, boolean markMissing)
	{
		this.carried = carried == null ? OwnedItems.none() : carried;
		this.owned = owned == null ? OwnedItems.none() : owned;
		this.markMissing = markMissing;
	}

	public static ItemSlotOwnership none()
	{
		return NONE;
	}

	public static ItemSlotOwnership carried(OwnedItems carried)
	{
		return new ItemSlotOwnership(carried, OwnedItems.none(), false);
	}

	public static ItemSlotOwnership of(OwnedItems carried, GearRecommendation recommendation)
	{
		GearRecommendation rec = recommendation == null ? GearRecommendation.specialized() : recommendation;
		return new ItemSlotOwnership(
			carried,
			rec.owned(),
			!rec.onlyOwned() && rec.owned().hasBankSnapshot());
	}

	public Color background(GearItem item)
	{
		if (item == null)
		{
			return ItemSlot.EMPTY_BACKGROUND;
		}
		if (carried.contains(item))
		{
			return ItemSlot.HELD_BACKGROUND;
		}
		if (markMissing && !owned.contains(item))
		{
			return ItemSlot.MISSING_BACKGROUND;
		}
		return ItemSlot.EMPTY_BACKGROUND;
	}
}
