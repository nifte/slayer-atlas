package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class OffhandGear
{
	public static final GearItem MELEE = GearItem.named("Avernic defender");
	public static final GearItem RANGED = GearItem.named("Twisted buckler");
	public static final GearItem MAGIC = GearItem.named("Elidinis' ward (f)");
	public static final GearItem DRAGONFIRE_SHIELD = GearItem.named("Dragonfire shield");
	public static final GearItem DRAGONFIRE_WARD = GearItem.named("Dragonfire ward");
	public static final GearItem WYVERN_SHIELD = GearItem.named("Ancient wyvern shield");

	private OffhandGear()
	{
	}

	public static GearItem forStyle(CombatStyle style)
	{
		return forMonster(style, null);
	}

	public static GearItem forMonster(CombatStyle style, SlayerMonster monster)
	{
		if (DragonbaneGear.applies(monster))
		{
			if (style == CombatStyle.RANGED)
			{
				return DRAGONFIRE_WARD;
			}
			if (style == CombatStyle.MAGIC || requiresBreathShield(monster))
			{
				return WYVERN_SHIELD;
			}
			return DRAGONFIRE_SHIELD;
		}
		if (style == CombatStyle.RANGED)
		{
			return RANGED;
		}
		if (style == CombatStyle.MAGIC)
		{
			return MAGIC;
		}
		return MELEE;
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		loadout = withoutOffhandIfTwoHanded(loadout);
		if (loadout == null)
		{
			return null;
		}
		if (isTwoHanded(loadout.worn(EquipmentSlot.WEAPON)) || loadout.worn(EquipmentSlot.SHIELD) != null)
		{
			return loadout;
		}
		return loadout.withWorn(EquipmentSlot.SHIELD, forMonster(loadout.getStyle(), monster));
	}

	public static GearLoadout withoutOffhandIfTwoHanded(GearLoadout loadout)
	{
		if (loadout == null || !isTwoHanded(loadout.worn(EquipmentSlot.WEAPON)))
		{
			return loadout;
		}
		return loadout.withWorn(EquipmentSlot.SHIELD, null);
	}

	public static boolean prefersWikiRanks(List<GearItem> wiki)
	{
		if (wiki == null)
		{
			return false;
		}
		for (GearItem item : wiki)
		{
			if (item != null)
			{
				return !isDragonfireOffhand(item);
			}
		}
		return false;
	}

	public static boolean requiresBreathShield(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		if (monster.getName() != null && monster.getName().toLowerCase(Locale.ROOT).contains("wyvern"))
		{
			return true;
		}
		List<String> required = monster.getRequiredItems();
		if (required == null || required.isEmpty())
		{
			return false;
		}
		String joined = String.join(" ", required).toLowerCase(Locale.ROOT);
		return joined.contains("wyvern shield")
			|| joined.contains("elemental")
			|| joined.contains("mind shield");
	}

	public static boolean isDragonfireOffhand(GearItem shield)
	{
		if (shield == null || shield.getName() == null)
		{
			return false;
		}
		String lower = shield.getName().toLowerCase(Locale.ROOT);
		return lower.contains("dragonfire")
			|| lower.contains("anti-dragon")
			|| lower.contains("antidragon")
			|| lower.contains("wyvern shield")
			|| lower.contains("elemental shield")
			|| lower.contains("mind shield");
	}

	public static boolean isTwoHanded(GearItem weapon)
	{
		return weapon != null && isTwoHanded(weapon.getName());
	}

	public static boolean isTwoHanded(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		String lower = name.toLowerCase(Locale.ROOT);
		if (isOneHandedName(lower))
		{
			return false;
		}
		if (lower.contains("bow") || isTwoHandedStaff(lower))
		{
			return true;
		}
		return containsAny(
			lower,
			"scythe",
			"godsword",
			"halberd",
			"ballista",
			"blowpipe",
			"2h",
			"two-handed",
			"two handed",
			"claws",
			"bludgeon",
			"bulwark",
			"macuahuitl",
			"soulreaper",
			"hallowfell",
			"tumeken's shadow",
			"greatsword",
			"greataxe",
			"spear");
	}

	private static boolean isOneHandedName(String lower)
	{
		return containsAny(
			lower,
			"crossbow",
			"wand",
			"lance",
			"hasta",
			"purging staff",
			"nightmare staff",
			"eye of ayak");
	}

	private static boolean isTwoHandedStaff(String lower)
	{
		return containsAny(
			lower,
			"tumeken's shadow",
			"sanguinesti",
			"trident",
			"iban's staff",
			"ahrim's staff");
	}

	private static boolean containsAny(String lower, String... needles)
	{
		for (String needle : needles)
		{
			if (lower.contains(needle))
			{
				return true;
			}
		}
		return false;
	}
}
