package com.slayeratlas.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.data.MonsterLocation;
import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

public class LocationMapTest
{
	@Test
	public void usesTheSurfaceEntranceWhenTheCatalogTileIsUnderground()
	{
		MonsterLocation location = location(
			"{\"x\":2280,\"y\":10022,\"plane\":0,\"pathX\":2278,\"pathY\":3611,\"pathPlane\":0}");
		WorldPoint point = LocationMap.point(location);
		assertEquals(2278, point.getX());
		assertEquals(3611, point.getY());
		assertEquals(0, point.getPlane());
		assertTrue(LocationMap.isOnOverworld(point));
	}

	@Test
	public void usesTheCatalogTileWhenItIsAlreadyOnTheOverworld()
	{
		MonsterLocation location = location("{\"x\":3007,\"y\":3150,\"plane\":0}");
		WorldPoint point = LocationMap.point(location);
		assertEquals(3007, point.getX());
		assertEquals(3150, point.getY());
		assertEquals(0, point.getPlane());
	}

	@Test
	public void returnsNullForAMissingLocation()
	{
		assertNull(LocationMap.point(null));
	}

	private static MonsterLocation location(String json)
	{
		return new Gson().fromJson(json, MonsterLocation.class);
	}
}
