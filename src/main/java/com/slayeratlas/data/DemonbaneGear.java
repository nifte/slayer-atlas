package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class DemonbaneGear
{
	public static final GearItem EMBERLIGHT = GearItem.named("Emberlight");
	public static final GearItem SCORCHING_BOW = GearItem.named("Scorching bow");
	public static final GearItem PURGING_STAFF = GearItem.named("Purging staff");
	public static final GearItem DRAGON_ARROW = GearItem.named("Dragon arrow");

	private DemonbaneGear()
	{
	}

	public static boolean applies(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		if ("demon".equalsIgnoreCase(monster.getAttribute()))
		{
			return true;
		}
		return isDemonName(monster.getName())
			|| mentionsDemonbane(monster.getWeakness())
			|| mentionsDemonbane(monster.getRecommendedStyle());
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
		GearLoadout updated = loadout.withWorn(EquipmentSlot.WEAPON, weapon);
		if (loadout.getStyle() == CombatStyle.RANGED)
		{
			return updated.withWorn(EquipmentSlot.AMMO, DRAGON_ARROW);
		}
		return updated;
	}

	public static List<GearItem> ranks(CombatStyle style, SlayerMonster monster)
	{
		if (!applies(monster))
		{
			return List.of();
		}
		if (style == CombatStyle.RANGED)
		{
			return List.of(SCORCHING_BOW);
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(PURGING_STAFF);
		}
		if (style == CombatStyle.MELEE)
		{
			return List.of(EMBERLIGHT, GearItem.named("Arclight"), GearItem.named("Darklight"));
		}
		return List.of();
	}

	public static GearItem weapon(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return SCORCHING_BOW;
		}
		if (style == CombatStyle.MAGIC)
		{
			return PURGING_STAFF;
		}
		if (style == CombatStyle.MELEE)
		{
			return EMBERLIGHT;
		}
		return null;
	}

	private static boolean isDemonName(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.contains("demon")
			|| lower.contains("fiend")
			|| lower.contains("nechryael")
			|| lower.contains("bloodveld");
	}

	private static boolean mentionsDemonbane(String text)
	{
		if (text == null || text.isEmpty())
		{
			return false;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		return lower.contains("demonbane")
			|| lower.contains("emberlight")
			|| lower.contains("arclight")
			|| lower.contains("scorching bow")
			|| lower.contains("purging staff");
	}
}
