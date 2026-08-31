package com.slayeratlas.data;

import java.util.List;

public final class LeafBladedGear
{
	public static final GearItem BATTLEAXE = GearItem.named("Leaf-bladed battleaxe");
	public static final GearItem SPEAR = GearItem.named("Leaf-bladed spear");
	public static final GearItem SWORD = GearItem.named("Leaf-bladed sword");
	public static final GearItem BROAD_BOLTS = GearItem.named("Broad bolts");
	public static final GearItem SLAYER_STAFF = GearItem.named("Slayer's staff (e)");

	private LeafBladedGear()
	{
	}

	public static boolean applies(SlayerMonster monster)
	{
		return MonsterHints.leafBladed(monster);
	}

	public static List<GearItem> ranks(CombatStyle style, SlayerMonster monster)
	{
		if (!applies(monster))
		{
			return List.of();
		}
		if (style == CombatStyle.MELEE)
		{
			return List.of(BATTLEAXE, SPEAR, SWORD);
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(SLAYER_STAFF, GearItem.named("Slayer's staff"));
		}
		return List.of();
	}

	public static List<GearItem> ammo(CombatStyle style, SlayerMonster monster)
	{
		if (!applies(monster) || style != CombatStyle.RANGED)
		{
			return List.of();
		}
		return List.of(BROAD_BOLTS, GearItem.named("Broad arrows"));
	}

	public static GearItem weapon(CombatStyle style)
	{
		if (style == CombatStyle.MELEE)
		{
			return BATTLEAXE;
		}
		if (style == CombatStyle.MAGIC)
		{
			return SLAYER_STAFF;
		}
		return null;
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null || !applies(monster))
		{
			return loadout;
		}
		if (loadout.getStyle() == CombatStyle.RANGED)
		{
			return loadout.withWorn(EquipmentSlot.AMMO, BROAD_BOLTS);
		}
		GearItem weapon = weapon(loadout.getStyle());
		return weapon == null ? loadout : loadout.withWorn(EquipmentSlot.WEAPON, weapon);
	}
}
