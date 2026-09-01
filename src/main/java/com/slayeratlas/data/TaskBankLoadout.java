package com.slayeratlas.data;

import java.util.List;

public final class TaskBankLoadout
{
	private TaskBankLoadout()
	{
	}

	public static GearLoadout resolve(
		SlayerMonster monster,
		LoadoutSelection selection,
		TaskLoadouts taskLoadouts,
		GearRecommendation recommendation)
	{
		if (monster == null)
		{
			return null;
		}
		if (selection != null)
		{
			GearLoadout remembered = selection.loadout(monster.getId());
			if (remembered != null)
			{
				return remembered;
			}
		}
		GearLoadout saved = taskLoadouts == null ? null : taskLoadouts.load(monster.getId());
		if (saved != null)
		{
			return saved;
		}
		List<GearLoadout> loadouts = GearLoadouts.forMonster(
			monster,
			List.of(),
			recommendation == null ? GearRecommendation.specialized() : recommendation);
		return loadouts.isEmpty() ? null : loadouts.get(0);
	}
}
