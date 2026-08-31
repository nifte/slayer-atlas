package com.slayeratlas.data;

import java.util.List;

public final class VampyreGear
{
	public static final GearItem FLAIL = GearItem.named("Blisterwood flail");
	public static final GearItem SICKLE = GearItem.named("Blisterwood sickle");
	public static final GearItem IVANDIS = GearItem.named("Ivandis flail");

	private VampyreGear()
	{
	}

	public static boolean applies(SlayerMonster monster)
	{
		return MonsterHints.vampyre(monster);
	}

	public static List<GearItem> ranks(CombatStyle style, SlayerMonster monster)
	{
		if (!applies(monster) || style != CombatStyle.MELEE)
		{
			return List.of();
		}
		return List.of(FLAIL, SICKLE, IVANDIS);
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null || loadout.getStyle() != CombatStyle.MELEE || !applies(monster))
		{
			return loadout;
		}
		return loadout.withWorn(EquipmentSlot.WEAPON, FLAIL);
	}
}
