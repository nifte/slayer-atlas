package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class RequiredGear
{
	public static final GearItem TORTUGAN_SHIELD = GearItem.named("Tortugan shield");
	public static final GearItem MIRROR_SHIELD = GearItem.named("Mirror shield");
	public static final GearItem BUG_LANTERN = GearItem.named("Lit bug lantern");
	public static final GearItem INSULATED_BOOTS = GearItem.named("Insulated boots");
	public static final GearItem SLAYER_GLOVES = GearItem.named("Slayer gloves");
	public static final GearItem WITCHWOOD_ICON = GearItem.named("Witchwood icon");
	public static final GearItem BRIMSTONE_BOOTS = GearItem.named("Boots of brimstone");

	private RequiredGear()
	{
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null)
		{
			return null;
		}
		GearItem shield = shield(monster);
		if (shield != null)
		{
			if (OffhandGear.isTwoHanded(loadout.worn(EquipmentSlot.WEAPON)))
			{
				loadout = loadout.withWorn(EquipmentSlot.WEAPON, oneHandedWeapon(loadout.getStyle()));
			}
			loadout = loadout.withWorn(EquipmentSlot.SHIELD, shield);
		}
		GearItem cape = cape(monster);
		if (cape != null)
		{
			loadout = loadout.withWorn(EquipmentSlot.CAPE, cape);
		}
		GearItem neck = neck(monster);
		if (neck != null)
		{
			loadout = loadout.withWorn(EquipmentSlot.NECK, neck);
		}
		GearItem hands = hands(monster);
		if (hands != null)
		{
			loadout = loadout.withWorn(EquipmentSlot.HANDS, hands);
		}
		GearItem feet = feet(monster);
		if (feet != null)
		{
			loadout = loadout.withWorn(EquipmentSlot.FEET, feet);
		}
		return loadout;
	}

	public static GearItem shield(SlayerMonster monster)
	{
		String required = requiredText(monster);
		if (required.contains("mirror shield") || required.contains("v's shield"))
		{
			return MIRROR_SHIELD;
		}
		if (required.contains("bug lantern"))
		{
			return BUG_LANTERN;
		}
		return null;
	}

	public static GearItem cape(SlayerMonster monster)
	{
		return requiredText(monster).contains("tortugan") ? TORTUGAN_SHIELD : null;
	}

	public static GearItem neck(SlayerMonster monster)
	{
		if (!requiredText(monster).contains("witchwood") || recommendsProtectFromMelee(monster))
		{
			return null;
		}
		return WITCHWOOD_ICON;
	}

	public static GearItem hands(SlayerMonster monster)
	{
		return requiredText(monster).contains("slayer gloves") ? SLAYER_GLOVES : null;
	}

	public static GearItem feet(SlayerMonster monster)
	{
		String required = requiredText(monster);
		if (required.contains("insulated boots"))
		{
			return INSULATED_BOOTS;
		}
		if (required.contains("boots of stone") || required.contains("boots of brimstone"))
		{
			return BRIMSTONE_BOOTS;
		}
		return null;
	}

	private static GearItem oneHandedWeapon(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return GearItem.named("Zaryte crossbow");
		}
		if (style == CombatStyle.MAGIC)
		{
			return GearItem.named("Eye of Ayak");
		}
		return GearItem.named("Ghrazi rapier");
	}

	private static boolean recommendsProtectFromMelee(SlayerMonster monster)
	{
		if (monster == null || monster.getProtectionPrayer() == null)
		{
			return false;
		}
		return monster.getProtectionPrayer().toLowerCase(Locale.ROOT).contains("protect from melee");
	}

	private static String requiredText(SlayerMonster monster)
	{
		if (monster == null)
		{
			return "";
		}
		List<String> required = monster.getRequiredItems();
		if (required == null || required.isEmpty())
		{
			return "";
		}
		return String.join(" ", required).toLowerCase(Locale.ROOT);
	}
}
