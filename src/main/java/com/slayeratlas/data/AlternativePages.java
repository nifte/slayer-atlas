package com.slayeratlas.data;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AlternativePages
{
	private AlternativePages()
	{
	}

	public static void addTo(MonsterCatalog catalog, Gson gson)
	{
		if (catalog == null)
		{
			return;
		}
		AlternativePageCatalog extras = load(gson);
		extras.normalize();
		addLocations(catalog, extras.getLocations());

		List<SlayerMonster> assignments = new ArrayList<>(catalog.getMonsters());
		for (List<String> group : groupedAlternatives(assignments).values())
		{
			SlayerMonster parent = bestParent(group.get(0), assignments);
			if (parent == null)
			{
				continue;
			}
			String name = AlternativeMonsters.lookupName(group.get(0));
			SlayerMonster page = SlayerMonster.catalogAlternative(name);
			page.copyAssignmentDetails(parent);
			page.applyOverride(extras.getOverrides().get(name));
			page.setAlternatives(navigation(parent, name));
			catalog.getMonsters().add(page);
		}
	}

	private static Map<String, List<String>> groupedAlternatives(List<SlayerMonster> assignments)
	{
		Map<String, List<String>> groups = new java.util.LinkedHashMap<>();
		for (SlayerMonster parent : assignments)
		{
			if (parent.getAlternatives() == null)
			{
				continue;
			}
			for (String alternative : parent.getAlternatives())
			{
				String name = AlternativeMonsters.lookupName(alternative);
				if (name.isEmpty() || pointsToOtherAssignment(name, parent, assignments))
				{
					continue;
				}
				groups.computeIfAbsent(AlternativeMonsters.slug(name), key -> new ArrayList<>()).add(alternative);
			}
		}
		return groups;
	}

	private static SlayerMonster bestParent(String alternative, List<SlayerMonster> assignments)
	{
		String name = AlternativeMonsters.lookupName(alternative);
		SlayerMonster named = null;
		SlayerMonster listed = null;
		for (SlayerMonster assignment : assignments)
		{
			if (!listsAlternative(assignment, name))
			{
				continue;
			}
			if (TaskMatcher.matchesMonster(name, assignment))
			{
				return assignment;
			}
			if (listed == null)
			{
				listed = assignment;
			}
			if (named == null && assignment.getAlternatives().contains(name))
			{
				named = assignment;
			}
		}
		return named != null ? named : listed;
	}

	private static boolean listsAlternative(SlayerMonster assignment, String name)
	{
		if (assignment.getAlternatives() == null)
		{
			return false;
		}
		for (String alternative : assignment.getAlternatives())
		{
			if (TaskMatcher.namesMatch(name, AlternativeMonsters.lookupName(alternative)))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean pointsToOtherAssignment(
		String name,
		SlayerMonster parent,
		List<SlayerMonster> assignments)
	{
		for (SlayerMonster assignment : assignments)
		{
			if (assignment.getId().equals(parent.getId()))
			{
				continue;
			}
			if (TaskMatcher.namesMatch(name, assignment.getName()))
			{
				return true;
			}
		}
		return false;
	}

	private static List<String> navigation(SlayerMonster parent, String name)
	{
		LinkedHashSet<String> labels = new LinkedHashSet<>();
		for (String alternative : parent.getAlternatives())
		{
			if (!TaskMatcher.namesMatch(name, AlternativeMonsters.lookupName(alternative)))
			{
				labels.add(alternative);
			}
		}
		labels.add(parent.getName());
		return new ArrayList<>(labels);
	}

	private static void addLocations(MonsterCatalog catalog, List<MonsterLocation> extras)
	{
		Set<String> seen = new HashSet<>();
		for (MonsterLocation location : catalog.getLocations())
		{
			seen.add(location.getId());
		}
		for (MonsterLocation location : extras)
		{
			if (seen.add(location.getId()))
			{
				catalog.getLocations().add(location);
			}
		}
	}

	private static AlternativePageCatalog load(Gson gson)
	{
		InputStream stream = AlternativePages.class.getResourceAsStream("alternative_pages.json");
		if (stream == null)
		{
			return new AlternativePageCatalog();
		}
		try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8))
		{
			AlternativePageCatalog extras = gson.fromJson(reader, AlternativePageCatalog.class);
			return extras == null ? new AlternativePageCatalog() : extras;
		}
		catch (IOException ex)
		{
			throw new IllegalStateException("Could not read alternative monster pages.", ex);
		}
	}
}
