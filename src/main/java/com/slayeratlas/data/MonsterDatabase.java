package com.slayeratlas.data;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MonsterDatabase
{
	private final Map<String, MonsterLocation> locationsById;
	private final Map<String, SlayerMonster> monstersById;
	private final List<SlayerMonster> pages;
	private final List<SlayerMonster> monsters;

	@Inject
	public MonsterDatabase(Gson gson)
	{
		MonsterCatalog catalog = loadCatalog(gson);
		AlternativePages.addTo(catalog, gson);
		catalog.normalize();
		Map<String, MonsterLocation> locationMap = new LinkedHashMap<>();
		for (MonsterLocation location : catalog.getLocations())
		{
			locationMap.put(location.getId(), location);
		}
		this.locationsById = Collections.unmodifiableMap(locationMap);

		Map<String, SlayerMonster> monsterMap = new LinkedHashMap<>();
		List<SlayerMonster> sorted = new ArrayList<>(catalog.getMonsters());
		sorted.sort(Comparator.comparing(SlayerMonster::getName, String.CASE_INSENSITIVE_ORDER));
		for (SlayerMonster monster : sorted)
		{
			monsterMap.put(monster.getId(), monster);
		}
		this.monstersById = Collections.unmodifiableMap(monsterMap);
		this.pages = Collections.unmodifiableList(sorted);
		List<SlayerMonster> assignments = new ArrayList<>();
		for (SlayerMonster monster : sorted)
		{
			if (monster.isAssignment())
			{
				assignments.add(monster);
			}
		}
		this.monsters = Collections.unmodifiableList(assignments);
	}

	public List<SlayerMonster> getMonsters()
	{
		return monsters;
	}

	public List<SlayerMonster> getPages()
	{
		return pages;
	}

	public Map<String, MonsterLocation> getLocationsById()
	{
		return locationsById;
	}

	public SlayerMonster getMonster(String id)
	{
		return monstersById.get(id);
	}

	public SlayerMonster findNamedPage(String name)
	{
		if (name == null || name.trim().isEmpty())
		{
			return null;
		}
		SlayerMonster singular = null;
		for (SlayerMonster monster : pages)
		{
			if (TaskMatcher.normalize(name).equals(TaskMatcher.normalize(monster.getName())))
			{
				return monster;
			}
			if (singular == null && TaskMatcher.namesMatch(name, monster.getName()))
			{
				singular = monster;
			}
		}
		return singular;
	}

	public MonsterLocation getLocation(String id)
	{
		return locationsById.get(id);
	}

	public List<MonsterLocation> locationsFor(SlayerMonster monster)
	{
		if (monster == null)
		{
			return Collections.emptyList();
		}
		List<MonsterLocation> found = new ArrayList<>();
		for (String locationId : monster.getLocationIds())
		{
			MonsterLocation location = locationsById.get(locationId);
			if (location != null)
			{
				found.add(location);
			}
		}
		return found;
	}

	public SlayerMonster findByTaskName(String taskName)
	{
		if (taskName == null || taskName.trim().isEmpty())
		{
			return null;
		}
		for (SlayerMonster monster : monsters)
		{
			if (TaskMatcher.matchesMonster(taskName, monster))
			{
				return monster;
			}
		}

		String needle = TaskMatcher.normalize(taskName);
		SlayerMonster best = null;
		int bestRank = Integer.MAX_VALUE;
		for (SlayerMonster monster : monsters)
		{
			if (TaskMatcher.matchesQuery(taskName, monster))
			{
				int rank = TaskMatcher.searchRank(needle, monster);
				if (rank < bestRank)
				{
					best = monster;
					bestRank = rank;
				}
			}
		}
		return best;
	}

	public List<SlayerMonster> search(String query)
	{
		boolean empty = query == null || query.trim().isEmpty();
		List<SlayerMonster> matches = new ArrayList<>();
		for (SlayerMonster monster : empty ? monsters : pages)
		{
			if (TaskMatcher.matchesQuery(query, monster))
			{
				matches.add(monster);
			}
		}
		if (query != null && !query.trim().isEmpty())
		{
			matches.sort(Comparator
				.comparingInt((SlayerMonster monster) -> TaskMatcher.searchRank(query, monster))
				.thenComparing(SlayerMonster::getName, String.CASE_INSENSITIVE_ORDER));
		}
		return matches;
	}

	public MonsterLocation preferredLocation(SlayerMonster monster, String assignedLocation)
	{
		List<MonsterLocation> locations = locationsFor(monster);
		if (locations.isEmpty())
		{
			return null;
		}
		if (assignedLocation != null && !assignedLocation.trim().isEmpty())
		{
			for (MonsterLocation location : locations)
			{
				if (TaskMatcher.matchesLocation(assignedLocation, location))
				{
					return location;
				}
			}
		}
		if (monster.getRecommendedLocationId() != null)
		{
			MonsterLocation recommended = locationsById.get(monster.getRecommendedLocationId());
			if (recommended != null && locations.contains(recommended))
			{
				return recommended;
			}
		}
		for (MonsterLocation location : locations)
		{
			if (!location.isWilderness())
			{
				return location;
			}
		}
		return locations.get(0);
	}

	private static MonsterCatalog loadCatalog(Gson gson)
	{
		InputStream stream = MonsterDatabase.class.getResourceAsStream("monsters.json");
		if (stream == null)
		{
			throw new IllegalStateException("Missing slayer monster database (monsters.json).");
		}
		try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8))
		{
			MonsterCatalog catalog = gson.fromJson(reader, MonsterCatalog.class);
			if (catalog == null || catalog.getMonsters().isEmpty())
			{
				throw new IllegalStateException("Slayer monster database was empty.");
			}
			return catalog;
		}
		catch (IOException ex)
		{
			throw new IllegalStateException("Could not read slayer monster database.", ex);
		}
	}
}
