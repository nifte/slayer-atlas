package com.slayerguide.data;

import java.util.Locale;
import java.util.Objects;

public final class TaskMatcher
{
	private TaskMatcher()
	{
	}

	public static String normalize(String value)
	{
		if (value == null)
		{
			return "";
		}

		return value.toLowerCase(Locale.ROOT)
			.replace('-', ' ')
			.replaceAll("[^a-z0-9 ]", "")
			.replaceAll("\\s+", " ")
			.trim();
	}

	public static String singularize(String normalized)
	{
		if (normalized.endsWith("ies") && normalized.length() > 3)
		{
			return normalized.substring(0, normalized.length() - 3) + "y";
		}
		if (normalized.endsWith("sses") || normalized.endsWith("shes") || normalized.endsWith("ches")
			|| normalized.endsWith("xes") || normalized.endsWith("zes"))
		{
			return normalized.substring(0, normalized.length() - 2);
		}
		if (normalized.endsWith("ves") && normalized.length() > 3)
		{
			return normalized.substring(0, normalized.length() - 3) + "f";
		}
		if (normalized.endsWith("s") && !normalized.endsWith("ss") && normalized.length() > 3)
		{
			return normalized.substring(0, normalized.length() - 1);
		}
		return normalized;
	}

	public static boolean namesMatch(String taskName, String candidate)
	{
		String task = normalize(taskName);
		String name = normalize(candidate);
		if (task.isEmpty() || name.isEmpty())
		{
			return false;
		}
		if (task.equals(name))
		{
			return true;
		}
		return singularize(task).equals(singularize(name));
	}

	public static boolean matchesMonster(String taskName, SlayerMonster monster)
	{
		if (monster == null || taskName == null || taskName.trim().isEmpty())
		{
			return false;
		}
		if (namesMatch(taskName, monster.getName()))
		{
			return true;
		}
		for (String alias : monster.getAliases())
		{
			if (namesMatch(taskName, alias))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean matchesQuery(String query, SlayerMonster monster)
	{
		String needle = normalize(query);
		if (needle.isEmpty())
		{
			return true;
		}
		if (containsNormalized(monster.getName(), needle) || containsNormalized(monster.getAttribute(), needle)
			|| containsNormalized(monster.getAttackStyle(), needle))
		{
			return true;
		}
		for (String alias : monster.getAliases())
		{
			if (containsNormalized(alias, needle))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean matchesLocation(String assignedLocation, MonsterLocation location)
	{
		if (assignedLocation == null || assignedLocation.trim().isEmpty() || location == null)
		{
			return false;
		}
		String assigned = normalize(assignedLocation);
		String name = normalize(location.getName());
		if (assigned.equals(name) || assigned.contains(name) || name.contains(assigned))
		{
			return true;
		}
		return singularize(assigned).equals(singularize(name));
	}

	public static int searchRank(String query, SlayerMonster monster)
	{
		String needle = normalize(query);
		String name = normalize(monster.getName());
		if (name.equals(needle))
		{
			return 0;
		}
		if (name.startsWith(needle))
		{
			return 1;
		}
		if (containsNormalized(name, needle))
		{
			return 2;
		}
		return 3;
	}

	private static boolean containsNormalized(String value, String needle)
	{
		return normalize(Objects.toString(value, "")).contains(needle);
	}
}
