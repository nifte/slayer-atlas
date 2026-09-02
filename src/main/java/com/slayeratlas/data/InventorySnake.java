package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;

public final class InventorySnake
{
	public static final int COLUMNS = 4;

	private InventorySnake()
	{
	}

	public static int slot(int index)
	{
		if (index < 0)
		{
			return index;
		}
		int row = index / COLUMNS;
		int col = index % COLUMNS;
		if ((row & 1) == 1)
		{
			col = COLUMNS - 1 - col;
		}
		return row * COLUMNS + col;
	}

	public static List<GearItem> apply(List<GearItem> items)
	{
		if (items == null || items.isEmpty())
		{
			return items == null ? List.of() : items;
		}
		List<GearItem> snaked = new ArrayList<>(items.size());
		for (int index = 0; index < items.size(); index++)
		{
			snaked.add(null);
		}
		for (int logical = 0; logical < items.size(); logical++)
		{
			snaked.set(slot(logical), items.get(logical));
		}
		return snaked;
	}
}
