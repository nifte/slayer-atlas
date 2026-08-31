package com.slayeratlas.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;
import com.slayeratlas.data.MonsterLocation;
import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

public class LocationPathTest
{
	@Test
	public void usesTheCatalogTileWhenNoPathOverrideIsSet()
	{
		MonsterLocation location = location("{\"x\":2278,\"y\":3611,\"plane\":0}");
		WorldPoint point = LocationPath.target(location);
		assertEquals(2278, point.getX());
		assertEquals(3611, point.getY());
		assertEquals(0, point.getPlane());
	}

	@Test
	public void prefersTheWalkableOverride()
	{
		MonsterLocation location = location(
			"{\"x\":2280,\"y\":10022,\"plane\":0,\"pathX\":2278,\"pathY\":3611,\"pathPlane\":0}");
		WorldPoint point = LocationPath.target(location);
		assertEquals(2278, point.getX());
		assertEquals(3611, point.getY());
		assertEquals(0, point.getPlane());
	}

	@Test
	public void returnsNullForAMissingLocation()
	{
		assertNull(LocationPath.target(null));
	}

	private static MonsterLocation location(String json)
	{
		return new Gson().fromJson(json, MonsterLocation.class);
	}
}
