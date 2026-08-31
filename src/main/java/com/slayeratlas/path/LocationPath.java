package com.slayeratlas.path;

import com.slayeratlas.data.MonsterLocation;
import net.runelite.api.coords.WorldPoint;

/** Walkable tile sent to Shortest Path, using a stored entrance when the catalog tile is unreachable. */
public final class LocationPath
{
	private LocationPath()
	{
	}

	public static WorldPoint target(MonsterLocation location)
	{
		if (location == null)
		{
			return null;
		}
		if (location.getPathX() > 0)
		{
			return new WorldPoint(location.getPathX(), location.getPathY(), location.getPathPlane());
		}
		return new WorldPoint(location.getX(), location.getY(), location.getPlane());
	}
}
