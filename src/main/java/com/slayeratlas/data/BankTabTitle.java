package com.slayeratlas.data;

import java.util.List;

public final class BankTabTitle
{
	private BankTabTitle()
	{
	}

	public static String of(
		SlayerMonster monster,
		LoadoutSelection selection,
		TaskLoadouts taskLoadouts,
		GearRecommendation recommendation)
	{
		if (monster == null)
		{
			return "Slayer Atlas";
		}
		GearRecommendation rec = recommendation == null ? GearRecommendation.specialized() : recommendation;
		List<GearLoadout> styles = GearLoadouts.forMonster(monster, List.of(), rec);
		boolean hasSaved = taskLoadouts != null && taskLoadouts.load(monster.getId()) != null;
		if (styles.size() + (hasSaved ? 1 : 0) <= 1)
		{
			return monster.getName();
		}
		String loadout = loadoutName(monster, selection, hasSaved, styles);
		if (loadout == null || loadout.isEmpty())
		{
			return monster.getName();
		}
		return monster.getName() + " (" + loadout + ")";
	}

	private static String loadoutName(
		SlayerMonster monster,
		LoadoutSelection selection,
		boolean hasSaved,
		List<GearLoadout> styles)
	{
		if (selection != null && selection.saved(monster.getId()))
		{
			return "Saved";
		}
		CombatStyle style = selection == null ? null : selection.style(monster.getId());
		if (style != null)
		{
			return style.displayName();
		}
		if (hasSaved)
		{
			return "Saved";
		}
		if (styles.isEmpty() || styles.get(0) == null || styles.get(0).getStyle() == null)
		{
			return null;
		}
		return styles.get(0).getStyle().displayName();
	}
}
