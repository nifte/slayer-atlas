package com.slayeratlas.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

public class LocationWorldMapPointTest
{
	@Test
	public void matchesClueHelperPinBehavior()
	{
		LocationWorldMapPoint point = LocationWorldMapPoint.of(new WorldPoint(2278, 3611, 0), "Kraken Cove");

		assertTrue(point.isSnapToEdge());
		assertTrue(point.isJumpOnClick());
		assertEquals("Kraken Cove", point.getName());
		assertEquals("Kraken Cove", point.getTooltip());
		assertNotNull(point.getImage());
		assertNotNull(point.getImagePoint());
		assertEquals(point.getImage().getWidth() / 2, point.getImagePoint().getX());
		assertEquals(point.getImage().getHeight(), point.getImagePoint().getY());
	}
}
