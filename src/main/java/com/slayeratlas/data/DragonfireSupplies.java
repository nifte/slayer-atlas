package com.slayeratlas.data;

public final class DragonfireSupplies
{
	private DragonfireSupplies()
	{
	}

	public static boolean needsPotion(SlayerMonster monster, GearItem shield)
	{
		if (!DragonbaneGear.applies(monster) || OffhandGear.requiresBreathShield(monster))
		{
			return false;
		}
		return !OffhandGear.isDragonfireOffhand(shield);
	}
}
