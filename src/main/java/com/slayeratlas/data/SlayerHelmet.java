package com.slayeratlas.data;

public final class SlayerHelmet
{
	public static final GearItem IMBUED = GearItem.named("Slayer helmet (i)");

	private SlayerHelmet()
	{
	}

	public static GearLoadout apply(GearLoadout loadout)
	{
		if (loadout == null)
		{
			return null;
		}
		return loadout.withWorn(EquipmentSlot.HEAD, IMBUED);
	}
}
