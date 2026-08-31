package com.slayeratlas.data;

import java.util.List;

public final class KalphiteGear
{
	public static final GearItem SUN = GearItem.named("Keris partisan of the sun");
	public static final GearItem CORRUPTION = GearItem.named("Keris partisan of corruption");
	public static final GearItem PARTISAN = GearItem.named("Keris partisan");
	public static final GearItem KERIS = GearItem.named("Keris");

	private KalphiteGear()
	{
	}

	public static boolean applies(SlayerMonster monster)
	{
		return MonsterHints.kalphite(monster);
	}

	public static List<GearItem> ranks(CombatStyle style, SlayerMonster monster)
	{
		if (!applies(monster) || style != CombatStyle.MELEE)
		{
			return List.of();
		}
		return List.of(SUN, CORRUPTION, PARTISAN, KERIS);
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null || loadout.getStyle() != CombatStyle.MELEE || !applies(monster))
		{
			return loadout;
		}
		return loadout.withWorn(EquipmentSlot.WEAPON, SUN);
	}
}
