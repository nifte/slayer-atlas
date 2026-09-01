package com.slayeratlas.data;

public final class PotionStorageSlot
{
	private final int itemId;
	private final int quantity;
	private final int storeIndex;

	public PotionStorageSlot(int itemId, int quantity, int storeIndex)
	{
		this.itemId = itemId;
		this.quantity = quantity;
		this.storeIndex = storeIndex;
	}

	public int itemId()
	{
		return itemId;
	}

	public int quantity()
	{
		return quantity;
	}

	public int storeIndex()
	{
		return storeIndex;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		if (!(other instanceof PotionStorageSlot))
		{
			return false;
		}
		PotionStorageSlot slot = (PotionStorageSlot) other;
		return itemId == slot.itemId && quantity == slot.quantity && storeIndex == slot.storeIndex;
	}

	@Override
	public int hashCode()
	{
		return 31 * (31 * itemId + quantity) + storeIndex;
	}
}
