package com.slayeratlas.data;

public enum EquipmentSlot
{
	HEAD("head", 1, 0),
	CAPE("cape", 0, 1),
	NECK("neck", 1, 1),
	AMMO("ammo", 2, 1),
	WEAPON("weapon", 0, 2),
	BODY("body", 1, 2),
	SHIELD("shield", 2, 2),
	LEGS("legs", 1, 3),
	HANDS("hands", 0, 4),
	FEET("feet", 1, 4),
	RING("ring", 2, 4),
	TWO_HAND("2h", -1, -1),
	SPECIAL("special", -1, -1);

	private final String wikiKey;
	private final int column;
	private final int row;

	EquipmentSlot(String wikiKey, int column, int row)
	{
		this.wikiKey = wikiKey;
		this.column = column;
		this.row = row;
	}

	public String wikiKey()
	{
		return wikiKey;
	}

	public int column()
	{
		return column;
	}

	public int row()
	{
		return row;
	}

	public boolean onWornGrid()
	{
		return column >= 0 && row >= 0;
	}

	public static EquipmentSlot at(int column, int row)
	{
		for (EquipmentSlot slot : values())
		{
			if (slot.onWornGrid() && slot.column == column && slot.row == row)
			{
				return slot;
			}
		}
		return null;
	}

	public static EquipmentSlot fromWikiKey(String key)
	{
		if (key == null)
		{
			return null;
		}
		for (EquipmentSlot slot : values())
		{
			if (slot.wikiKey.equalsIgnoreCase(key))
			{
				return slot;
			}
		}
		return null;
	}
}
