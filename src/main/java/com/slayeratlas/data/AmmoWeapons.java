package com.slayeratlas.data;

import java.util.Locale;

public final class AmmoWeapons
{
	public static final GearItem ZARYTE_CROSSBOW = GearItem.named("Zaryte crossbow");

	private AmmoWeapons()
	{
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null || loadout.getStyle() != CombatStyle.RANGED)
		{
			return loadout;
		}
		if (!usesBolts(loadout.worn(EquipmentSlot.AMMO)))
		{
			return loadout;
		}
		GearItem weapon = loadout.worn(EquipmentSlot.WEAPON);
		if (weapon != null)
		{
			return loadout;
		}
		if (DemonbaneGear.applies(monster))
		{
			return loadout;
		}
		GearItem crossbow = DragonbaneGear.applies(monster)
			? DragonbaneGear.weapon(CombatStyle.RANGED)
			: ZARYTE_CROSSBOW;
		return loadout.withWorn(EquipmentSlot.WEAPON, crossbow);
	}

	private static boolean usesBolts(GearItem ammo)
	{
		if (ammo == null || ammo.getName() == null)
		{
			return false;
		}
		return ammo.getName().toLowerCase(Locale.ROOT).contains("bolt");
	}
}
