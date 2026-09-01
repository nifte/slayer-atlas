package com.slayeratlas.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
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

	@Test
	public void keepsTheTooltipWhenSnappedToTheEdge()
	{
		LocationWorldMapPoint point = LocationWorldMapPoint.of(new WorldPoint(2278, 3611, 0), "Kraken Cove");

		point.onEdgeSnap();

		assertNull(point.getImagePoint());
		assertTrue(point.getImage().getWidth() > LocationMapImages.ICON_SIZE);
		assertTrue(point.getImage().getHeight() > LocationMapImages.ICON_SIZE);
		assertTrue(opaque(point.getImage(), point.getImage().getWidth() / 2, point.getImage().getHeight() - 1));
	}

	@Test
	public void pointsTheTooltipArrowTowardTheFacedEdge()
	{
		LocationWorldMapPoint point = LocationWorldMapPoint.of(new WorldPoint(2278, 3611, 0), "Kraken Cove");
		point.setCurrentlyEdgeSnapped(true);

		point.face(LocationMapEdge.NORTH);
		assertEquals(32, point.getImage().getWidth());
		assertEquals(39, point.getImage().getHeight());
		assertTrue(opaque(point.getImage(), point.getImage().getWidth() / 2, 0));

		point.face(LocationMapEdge.EAST);
		assertEquals(39, point.getImage().getWidth());
		assertEquals(32, point.getImage().getHeight());
		assertTrue(opaque(point.getImage(), point.getImage().getWidth() - 1, point.getImage().getHeight() / 2));

		point.onEdgeUnsnap();
		assertNotNull(point.getImagePoint());
		assertEquals(32, point.getImage().getWidth());
		assertEquals(39, point.getImage().getHeight());
	}

	private static boolean opaque(BufferedImage image, int x, int y)
	{
		return ((image.getRGB(x, y) >> 24) & 0xff) > 0;
	}
}
