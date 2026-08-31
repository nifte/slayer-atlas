package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class InventoryRations
{
	private static final int MAX_FOOD = 16;
	private static final int MAX_REDUCED_FOOD = 10;

	private InventoryRations()
	{
	}

	public static int foodSlots(SlayerMonster monster)
	{
		if (monster == null)
		{
			return 1;
		}
		if (fullyNegated(monster))
		{
			return 0;
		}
		int combat = combat(monster);
		if (!hasOverhead(monster))
		{
			return clamp(combat / 20, 1, 8);
		}
		if (dragonfireLeftover(monster))
		{
			return clamp(combat / 18, 4, MAX_REDUCED_FOOD);
		}
		return clamp(combat / 12, 4, MAX_FOOD);
	}

	static boolean fullyNegated(SlayerMonster monster)
	{
		return hasOverhead(monster)
			&& attackStyleCovered(monster)
			&& !dragonfireLeftover(monster)
			&& !wyvernLeftover(monster)
			&& !unprayableDamage(monster);
	}

	private static boolean hasOverhead(SlayerMonster monster)
	{
		String prayer = lower(monster == null ? null : monster.getProtectionPrayer());
		if (prayer.isEmpty() || prayer.contains("none"))
		{
			return false;
		}
		return prayer.contains("melee") || prayer.contains("magic") || prayer.contains("missile");
	}

	private static boolean attackStyleCovered(SlayerMonster monster)
	{
		String style = lower(monster.getAttackStyle());
		if (style.isEmpty())
		{
			return true;
		}
		boolean melee = style.contains("melee");
		boolean magic = style.contains("magic") && !style.contains("magical melee")
			&& !style.contains("magical rang");
		boolean ranged = style.contains("rang") || style.contains("missile");
		String prayer = lower(monster.getProtectionPrayer());
		if (melee && !prayer.contains("melee"))
		{
			return false;
		}
		if (magic && !prayer.contains("magic"))
		{
			return false;
		}
		if (ranged && !prayer.contains("missile"))
		{
			return false;
		}
		return melee || magic || ranged;
	}

	private static boolean dragonfireLeftover(SlayerMonster monster)
	{
		if (OffhandGear.requiresBreathShield(monster))
		{
			return false;
		}
		String blob = blob(monster);
		if (blob.contains("optional") && blob.contains("antifire"))
		{
			return false;
		}
		return blob.contains("antifire") || blob.contains("anti-dragon");
	}

	private static boolean wyvernLeftover(SlayerMonster monster)
	{
		return OffhandGear.requiresBreathShield(monster);
	}

	private static boolean unprayableDamage(SlayerMonster monster)
	{
		String blob = blob(monster);
		if (blob.contains("all three combat"))
		{
			return true;
		}
		if (blob.contains("typeless") || blob.contains("bypass"))
		{
			return true;
		}
		return hitsThroughPrayer(blob);
	}

	private static boolean hitsThroughPrayer(String blob)
	{
		if (blob.contains("through protect") || blob.contains("through protection"))
		{
			return true;
		}
		if (blob.contains("hits through prayer") || blob.contains("hit through prayer"))
		{
			return true;
		}
		if (blob.contains("will not block") || blob.contains("does not block")
			|| blob.contains("do not block"))
		{
			return true;
		}
		if (blob.contains("ignores prayer") || blob.contains("ignore prayer")
			|| blob.contains("ignores protect") || blob.contains("ignore protect"))
		{
			return !blob.contains("without") || !blob.contains("shield");
		}
		return false;
	}

	private static String blob(SlayerMonster monster)
	{
		return lower(monster.getName())
			+ " "
			+ lower(monster.getWeakness())
			+ " "
			+ lower(monster.getNotes())
			+ " "
			+ lower(monster.getAttackStyle())
			+ " "
			+ lower(monster.getProtectionPrayer())
			+ " "
			+ join(monster.getRequiredItems())
			+ " "
			+ join(monster.getRecommendedPotions());
	}

	private static String join(List<String> values)
	{
		if (values == null || values.isEmpty())
		{
			return "";
		}
		return String.join(" ", values).toLowerCase(Locale.ROOT);
	}

	private static int combat(SlayerMonster monster)
	{
		if (monster.getCombatLevelMax() != null)
		{
			return monster.getCombatLevelMax();
		}
		if (monster.getCombatLevelMin() != null)
		{
			return monster.getCombatLevelMin();
		}
		return 50;
	}

	private static int clamp(int value, int min, int max)
	{
		return Math.max(min, Math.min(max, value));
	}

	private static String lower(String value)
	{
		return value == null ? "" : value.toLowerCase(Locale.ROOT);
	}
}
