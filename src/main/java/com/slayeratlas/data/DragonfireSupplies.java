package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;

public final class DragonfireSupplies
{
	private DragonfireSupplies()
	{
	}

	public static boolean needsPotion(SlayerMonster monster, GearItem shield)
	{
		if (!DragonbaneGear.applies(monster) || OffhandGear.requiresBreathShield(monster))
		{
			return false;
		}
		return !OffhandGear.isDragonfireOffhand(shield);
	}

	public static boolean needsDragonfireOffhand(SlayerMonster monster, GearRecommendation recommendation)
	{
		if (OffhandGear.requiresBreathShield(monster))
		{
			return true;
		}
		if (!DragonbaneGear.applies(monster))
		{
			return false;
		}
		return !potionFullyProtects(monster, recommendedPotion(monster, recommendation));
	}

	public static boolean potionFullyProtects(SlayerMonster monster, GearItem potion)
	{
		if (monster == null || !isSuperAntifire(potion) || strongerDragonfire(monster))
		{
			return false;
		}
		return true;
	}

	static boolean isSuperAntifire(GearItem potion)
	{
		return potion != null && isSuperAntifire(potion.getName());
	}

	static boolean isSuperAntifire(String name)
	{
		return name != null && name.toLowerCase(Locale.ROOT).contains("super antifire");
	}

	static GearItem recommendedPotion(SlayerMonster monster, GearRecommendation recommendation)
	{
		if (!recommendsPotion(monster))
		{
			return null;
		}
		return OwnedSupplies.pick(OwnedSupplies.ANTIFIRE, recommendation);
	}

	static boolean recommendsPotion(SlayerMonster monster)
	{
		if (monster == null || OffhandGear.requiresBreathShield(monster))
		{
			return false;
		}
		List<String> potions = monster.getRecommendedPotions();
		if (potions == null)
		{
			return false;
		}
		for (String blurb : potions)
		{
			if (isRecommendedAntifire(blurb))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isRecommendedAntifire(String blurb)
	{
		if (blurb == null || blurb.isEmpty())
		{
			return false;
		}
		String lower = blurb.toLowerCase(Locale.ROOT);
		if (!lower.contains("antifire"))
		{
			return false;
		}
		return !lower.startsWith("none")
			&& !lower.contains("optional")
			&& !lower.contains("if affordable");
	}

	private static boolean strongerDragonfire(SlayerMonster monster)
	{
		if (monster.getName() == null)
		{
			return false;
		}
		String lower = monster.getName().toLowerCase(Locale.ROOT);
		return lower.contains("vorkath")
			|| lower.contains("king black")
			|| lower.contains("galvek");
	}
}
