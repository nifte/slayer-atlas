package com.slayeratlas.map;

import com.slayeratlas.data.MonsterLocation;
import net.runelite.api.coords.WorldPoint;

/** World-map tile for a location. Instance and dungeon catalog tiles are mapped to the overworld. */
public final class LocationMap
{
	private static final int OVERWORLD_MAX_Y = 6400;

	private LocationMap()
	{
	}

	public static WorldPoint point(MonsterLocation location)
	{
		if (location == null)
		{
			return null;
		}
		WorldPoint catalog = new WorldPoint(location.getX(), location.getY(), location.getPlane());
		if (isOnOverworld(catalog))
		{
			return catalog;
		}
		if (location.getPathX() > 0)
		{
			return new WorldPoint(location.getPathX(), location.getPathY(), location.getPathPlane());
		}
		WorldPoint overworld = WorldPoint.getMirrorPoint(catalog, true);
		if (overworld != null && isOnOverworld(overworld))
		{
			return overworld;
		}
		return catalog;
	}

	static boolean isOnOverworld(WorldPoint point)
	{
		return point != null && point.getY() < OVERWORLD_MAX_Y;
	}
}
