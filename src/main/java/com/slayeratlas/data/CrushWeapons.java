package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class CrushWeapons
{
	public static final GearItem GRANITE = GearItem.named("Granite hammer");
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
		if (isGargoyle(monster))
		{
			return List.of(GRANITE, MACE, MAUL, BLUDGEON, GearItem.named("Dragon mace"));
		}
		return List.of(MACE, MAUL, BLUDGEON, GearItem.named("Dragon mace"), GRANITE);
	}

	public static GearLoadout apply(GearLoadout loadout, SlayerMonster monster)
	{
		if (loadout == null || loadout.getStyle() != CombatStyle.MELEE || !applies(monster))
		{
			return loadout;
		}
		return loadout.withWorn(EquipmentSlot.WEAPON, isGargoyle(monster) ? GRANITE : MACE);
	}

	public static boolean isGargoyleFinisher(GearItem item)
	{
		return item != null && isGargoyleFinisher(item.getName());
	}

	public static boolean isGargoyleFinisher(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		String lower = name.toLowerCase(Locale.ROOT);
		return OwnedItemNames.matches(name, GRANITE.getName()) || lower.contains("rock thrownhammer");
	}

	public static boolean isRockHammer(GearItem item)
	{
		return item != null && isRockHammer(item.getName());
	}

	public static boolean isRockHammer(String name)
	{
		return name != null && !name.isEmpty() && OwnedItemNames.matches(name, "Rock hammer");
	}

	public static boolean hasGargoyleFinisher(Iterable<GearItem> items)
	{
		if (items == null)
		{
			return false;
		}
		for (GearItem item : items)
		{
			if (isGargoyleFinisher(item))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isGargoyle(SlayerMonster monster)
	{
		if (monster == null || monster.getName() == null)
		{
			return false;
		}
		String name = monster.getName().toLowerCase(Locale.ROOT);
		return name.contains("gargoyle") || name.contains("grotesque");
	}
}
