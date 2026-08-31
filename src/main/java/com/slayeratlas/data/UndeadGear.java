package com.slayeratlas.data;

import java.util.List;

public final class UndeadGear
{
	public static final GearItem SALVE_EI = GearItem.named("Salve amulet (ei)");
	public static final GearItem SALVE_I = GearItem.named("Salve amulet (i)");
	public static final GearItem SALVE_E = GearItem.named("Salve amulet (e)");
	public static final GearItem SALVE = GearItem.named("Salve amulet");

	private static final List<GearItem> NECKS = List.of(SALVE_EI, SALVE_I, SALVE_E, SALVE);

	private UndeadGear()
	{
	}

	public static boolean applies(SlayerMonster monster)
	{
		return MonsterHints.undead(monster);
	}

	public static List<GearItem> necks(SlayerMonster monster)
	{
		return applies(monster) ? NECKS : List.of();
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null || !applies(monster))
		{
			return loadout;
		}
		return loadout.withWorn(EquipmentSlot.NECK, SALVE_EI);
	}
}
