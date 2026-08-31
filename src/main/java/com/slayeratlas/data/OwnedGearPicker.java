package com.slayeratlas.data;

import java.util.List;

public final class OwnedGearPicker
{
	private OwnedGearPicker()
	{
	}

	public static GearItem pick(List<GearItem> ranks, OwnedItems owned, boolean onlyOwned)
	{
		if (ranks == null || ranks.isEmpty())
		{
			return null;
		}
		GearItem first = ranks.get(0);
		if (!onlyOwned || owned == null || !owned.hasBankSnapshot())
		{
			return first;
		}
		for (GearItem item : ranks)
		{
			if (owned.contains(item))
			{
				return owned.shownAs(item);
			}
		}
		return null;
	}
}
