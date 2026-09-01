package com.slayeratlas.path;

import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.MonsterLocation;
import com.slayeratlas.data.SlayerMonster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.runelite.api.coords.WorldPoint;

/** Destinations for Auto path on new task: Konar's assigned area, or every listed spot so Shortest Path can pick the nearest. */
public final class AutoPathTargets
{
	private AutoPathTargets()
	{
	}

	public static Collection<WorldPoint> of(
		MonsterDatabase database,
		SlayerMonster monster,
		String assignedLocation)
	{
		if (database == null || monster == null)
		{
			return Collections.emptyList();
		}
		MonsterLocation assigned = database.preferredLocation(monster, assignedLocation);
		if (assigned != null)
		{
			WorldPoint point = LocationPath.target(assigned);
			return point == null ? Collections.emptyList() : List.of(point);
		}
		List<WorldPoint> points = new ArrayList<>();
		for (MonsterLocation location : database.locationsFor(monster))
		{
			WorldPoint point = LocationPath.target(location);
			if (point != null)
			{
				points.add(point);
			}
		}
		return points;
	}
}
