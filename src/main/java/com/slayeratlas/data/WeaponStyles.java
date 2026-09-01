package com.slayeratlas.data;

import net.runelite.api.Item;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;

public final class WeaponStyles
{
	private WeaponStyles()
	{
	}

	public static CombatStyle of(ItemManager items, Item weapon)
	{
		if (weapon == null || weapon.getId() <= 0)
		{
			return CombatStyle.MELEE;
		}
		if (items == null)
		{
			return null;
		}
		ItemStats stats = items.getItemStats(items.canonicalize(weapon.getId()));
		if (stats == null || stats.getEquipment() == null)
		{
			return null;
		}
		return of(stats.getEquipment());
	}

	public static CombatStyle of(ItemEquipmentStats equipment)
	{
		if (equipment == null)
		{
			return CombatStyle.MELEE;
		}
		return of(
			equipment.getAstab(),
			equipment.getAslash(),
			equipment.getAcrush(),
			equipment.getAmagic(),
			equipment.getArange());
	}

	public static CombatStyle of(int stab, int slash, int crush, int magic, int ranged)
	{
		int melee = Math.max(stab, Math.max(slash, crush));
		if (ranged > melee && ranged >= magic)
		{
			return CombatStyle.RANGED;
		}
		if (magic > melee && magic > ranged)
		{
			return CombatStyle.MAGIC;
		}
		return CombatStyle.MELEE;
	}
}
