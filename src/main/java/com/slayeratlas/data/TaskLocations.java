package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TaskLocations
{
	private TaskLocations()
	{
	}

	public static List<MonsterLocation> resolve(SlayerMonster monster, MonsterDatabase database)
	{
		if (monster == null || database == null)
		{
			return Collections.emptyList();
		}
		List<SlayerMonster> alternatives = alternatives(monster, database);
		List<MonsterLocation> found = new ArrayList<>();
		for (String locationId : monster.getLocationIds())
		{
			if (!include(monster, locationId, alternatives))
			{
				continue;
			}
			MonsterLocation location = database.getLocation(locationId);
			if (location != null)
			{
				found.add(location);
			}
		}
		return found;
	}

	static boolean include(SlayerMonster task, String locationId, List<SlayerMonster> alternatives)
	{
		if (task == null || locationId == null || !task.isAssignment())
		{
			return true;
		}
		boolean sameSetupUsesIt = false;
		boolean differentSetupUsesIt = false;
		if (alternatives == null)
		{
			return true;
		}
		for (SlayerMonster alternative : alternatives)
		{
			if (!ownsLocation(task, alternative, locationId))
			{
				continue;
			}
			if (RecommendedLoadouts.same(task, alternative))
			{
				sameSetupUsesIt = true;
			}
			else
			{
				differentSetupUsesIt = true;
			}
		}
		return sameSetupUsesIt || !differentSetupUsesIt;
	}

	private static List<SlayerMonster> alternatives(SlayerMonster monster, MonsterDatabase database)
	{
		List<SlayerMonster> found = new ArrayList<>();
		if (monster.getAlternatives() == null)
		{
			return found;
		}
		for (String label : monster.getAlternatives())
		{
			SlayerMonster alternative = AlternativeMonsters.find(database, label, monster);
			if (alternative != null)
			{
				found.add(alternative);
			}
		}
		return found;
	}

	private static boolean ownsLocation(SlayerMonster task, SlayerMonster alternative, String locationId)
	{
		if (alternative == null || !contains(alternative.getLocationIds(), locationId))
		{
			return false;
		}
		return !sameIds(task.getLocationIds(), alternative.getLocationIds());
	}

	private static boolean contains(List<String> values, String needle)
	{
		return values != null && values.contains(needle);
	}

	private static boolean sameIds(List<String> left, List<String> right)
	{
		Set<String> first = left == null ? Set.of() : new HashSet<>(left);
		Set<String> second = right == null ? Set.of() : new HashSet<>(right);
		return first.equals(second);
	}
}
