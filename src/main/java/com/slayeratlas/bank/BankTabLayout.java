package com.slayeratlas.bank;

import com.slayeratlas.data.EquipmentSlot;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.InventoryLoadouts;
import com.slayeratlas.data.OwnedItemNames;
import java.util.Arrays;
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

	public static int[] gridIndexes(GearLoadout loadout, List<String> names)
	{
		if (names == null || names.isEmpty())
		{
			return new int[0];
		}
		int[] grids = new int[names.size()];
		Arrays.fill(grids, -1);
		boolean[] used = new boolean[names.size()];
		if (loadout != null)
		{
			for (EquipmentSlot slot : EquipmentSlot.values())
			{
				assign(grids, used, names, loadout.worn(slot), equipmentIndex(slot));
			}
			List<GearItem> inventory = loadout.getInventory();
			int limit = Math.min(inventory.size(), INVENTORY_SIZE);
			for (int slot = 0; slot < limit; slot++)
			{
				assign(grids, used, names, inventory.get(slot), inventoryIndex(slot));
			}
		}
		int extra = EXTRAS_START;
		for (int i = 0; i < names.size(); i++)
		{
			if (used[i] || isBlank(names.get(i)))
			{
				continue;
			}
			grids[i] = extra++;
		}
		return grids;
	}

	private static void assign(int[] grids, boolean[] used, List<String> names, GearItem item, int gridIndex)
	{
		if (item == null || isBlank(item.getName()) || gridIndex < 0)
		{
			return;
		}
		int match = firstUnused(names, used, item.getName());
		if (match < 0)
		{
			return;
		}
		used[match] = true;
		grids[match] = gridIndex;
	}

	private static int firstUnused(List<String> names, boolean[] used, String wanted)
	{
		for (int i = 0; i < names.size(); i++)
		{
			if (!used[i] && OwnedItemNames.sameItem(wanted, names.get(i)))
			{
				return i;
			}
		}
		return -1;
	}

	private static boolean isBlank(String name)
	{
		return name == null || name.isEmpty();
	}
}
