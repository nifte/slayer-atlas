package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class DragonbaneGear
{
	private DragonbaneGear()
	{
	}

	public static boolean applies(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		if ("draconic".equalsIgnoreCase(monster.getAttribute()))
		{
			return true;
		}
		return isDragonkinName(monster.getName())
			|| mentionsDragonbane(monster.getWeakness())
			|| mentionsDragonbane(monster.getRecommendedStyle());
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null || !applies(monster))
		{
			return loadout;
		}
		GearItem weapon = weapon(loadout.getStyle());
		if (weapon == null)
		{
			return loadout;
		}
		return loadout.withWorn(EquipmentSlot.WEAPON, weapon);
	}

	public static List<GearItem> ranks(CombatStyle style, SlayerMonster monster)
	{
		if (!applies(monster))
		{
			return List.of();
		}
		GearItem weapon = weapon(style);
		return weapon == null ? List.of() : List.of(weapon);
	}

	public static GearItem weapon(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return GearItem.named("Dragon hunter crossbow");
		}
		if (style == CombatStyle.MAGIC)
		{
			return GearItem.named("Dragon hunter wand");
		}
		if (style == CombatStyle.MELEE)
		{
			return GearItem.named("Dragon hunter lance");
		}
		return null;
	}

	private static boolean isDragonkinName(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.contains("dragon")
			|| lower.contains("drake")
			|| lower.contains("wyvern")
			|| lower.contains("wyrm")
			|| lower.contains("hydra");
	}

	private static boolean mentionsDragonbane(String text)
	{
		if (text == null || text.isEmpty())
		{
			return false;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		return lower.contains("dragon hunter") || lower.contains("dragonbane");
	}
}
