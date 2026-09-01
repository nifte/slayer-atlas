package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public final class LoadoutPotionFilter
{
	private LoadoutPotionFilter()
	{
	}

	public static List<PotionStorageSlot> extra(
		List<PotionStorageSlot> stored,
		LoadoutBankMatcher matcher,
		List<String> shownNames,
		IntFunction<String> itemName)
	{
		List<PotionStorageSlot> extra = new ArrayList<>();
		if (stored == null || matcher == null || itemName == null)
		{
			return extra;
		}
		for (PotionStorageSlot slot : stored)
		{
			if (slot == null || slot.itemId() <= 0 || slot.quantity() <= 0)
			{
				continue;
			}
			String name = itemName.apply(slot.itemId());
			if (!matcher.matches(name) || alreadyShown(shownNames, name))
			{
				continue;
			}
			extra.add(slot);
		}
		return extra;
	}

	private static boolean alreadyShown(List<String> shownNames, String name)
	{
		if (shownNames == null)
		{
			return false;
		}
		for (String shown : shownNames)
		{
			if (OwnedItemNames.sameItem(shown, name))
			{
				return true;
			}
		}
		return false;
	}
}
