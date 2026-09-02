package com.slayeratlas.bank;

import com.slayeratlas.data.EquipmentSlot;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.InventoryLoadouts;
import com.slayeratlas.data.OwnedItemNames;
import java.util.ArrayList;
import java.util.List;

public final class BankTabLayout
{
	public static final int INVENTORY_COLUMNS = 4;
	public static final int INVENTORY_SIZE = InventoryLoadouts.SIZE;
	public static final int INVENTORY_COLUMN0 = 4;
	public static final int EXTRAS_START = 7 * BankItemGrid.COLUMNS;

	private BankTabLayout()
	{
	}

	public static int equipmentIndex(EquipmentSlot slot)
	{
		if (slot == null || !slot.onWornGrid())
		{
			return -1;
		}
		return slot.row() * BankItemGrid.COLUMNS + slot.column();
	}

	public static int inventoryIndex(int slot)
	{
		if (slot < 0 || slot >= INVENTORY_SIZE)
		{
			return -1;
		}
		int row = slot / INVENTORY_COLUMNS;
		int column = INVENTORY_COLUMN0 + slot % INVENTORY_COLUMNS;
		return row * BankItemGrid.COLUMNS + column;
	}

	public static int x(int gridIndex)
	{
		return BankItemGrid.x(gridIndex);
	}

	public static int y(int gridIndex)
	{
		return BankItemGrid.y(gridIndex);
	}

	public static int scrollHeight(int maxGridIndex)
	{
		return BankItemGrid.scrollHeight(Math.max(0, maxGridIndex) + 1);
	}

	public static List<Placement> placements(GearLoadout loadout, List<String> names)
	{
		List<Placement> placements = new ArrayList<>();
		if (names == null || names.isEmpty())
		{
			return placements;
		}
		boolean[] used = new boolean[names.size()];
		if (loadout != null)
		{
			for (EquipmentSlot slot : EquipmentSlot.values())
			{
				add(placements, used, names, loadout.worn(slot), equipmentIndex(slot));
			}
			List<GearItem> inventory = loadout.getInventory();
			int limit = Math.min(inventory.size(), INVENTORY_SIZE);
			for (int slot = 0; slot < limit; slot++)
			{
				add(placements, used, names, inventory.get(slot), inventoryIndex(slot));
			}
		}
		int extra = EXTRAS_START;
		for (int i = 0; i < names.size(); i++)
		{
			if (used[i] || isBlank(names.get(i)))
			{
				continue;
			}
			placements.add(new Placement(i, extra++));
		}
		return placements;
	}

	private static void add(
		List<Placement> placements,
		boolean[] used,
		List<String> names,
		GearItem item,
		int gridIndex)
	{
		if (item == null || isBlank(item.getName()) || gridIndex < 0)
		{
			return;
		}
		int source = sourceFor(names, used, item.getName());
		if (source < 0)
		{
			return;
		}
		used[source] = true;
		placements.add(new Placement(source, gridIndex));
	}

	private static int sourceFor(List<String> names, boolean[] used, String wanted)
	{
		int reuse = -1;
		for (int i = 0; i < names.size(); i++)
		{
			if (!OwnedItemNames.sameItem(wanted, names.get(i)))
			{
				continue;
			}
			if (!used[i])
			{
				return i;
			}
			if (reuse < 0)
			{
				reuse = i;
			}
		}
		return reuse;
	}

	private static boolean isBlank(String name)
	{
		return name == null || name.isEmpty();
	}

	public static final class Placement
	{
		private final int sourceIndex;
		private final int gridIndex;

		public Placement(int sourceIndex, int gridIndex)
		{
			this.sourceIndex = sourceIndex;
			this.gridIndex = gridIndex;
		}

		public int sourceIndex()
		{
			return sourceIndex;
		}

		public int gridIndex()
		{
			return gridIndex;
		}

		@Override
		public boolean equals(Object other)
		{
			if (this == other)
			{
				return true;
			}
			if (!(other instanceof Placement))
			{
				return false;
			}
			Placement placement = (Placement) other;
			return sourceIndex == placement.sourceIndex && gridIndex == placement.gridIndex;
		}

		@Override
		public int hashCode()
		{
			return 31 * sourceIndex + gridIndex;
		}
	}
}
