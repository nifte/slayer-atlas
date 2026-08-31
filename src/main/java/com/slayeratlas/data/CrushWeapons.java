package com.slayeratlas.data;

import java.util.List;

public final class CrushWeapons
{
	public static final GearItem MACE = GearItem.named("Inquisitor's mace");
	public static final GearItem MAUL = GearItem.named("Elder maul");
	public static final GearItem BLUDGEON = GearItem.named("Abyssal bludgeon");

	private CrushWeapons()
	{
	}

	public static boolean applies(SlayerMonster monster)
	{
		return MonsterHints.crush(monster);
	}

	public static List<GearItem> ranks(CombatStyle style, SlayerMonster monster)
	{
		if (!applies(monster) || style != CombatStyle.MELEE)
		{
			return List.of();
		}
		return List.of(
			MACE,
			MAUL,
			BLUDGEON,
			GearItem.named("Dragon mace"),
			GearItem.named("Granite hammer"));
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null || loadout.getStyle() != CombatStyle.MELEE || !applies(monster))
		{
			return loadout;
		}
		return loadout.withWorn(EquipmentSlot.WEAPON, MACE);
	}
}
