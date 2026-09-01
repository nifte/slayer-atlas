package com.slayeratlas.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.Test;

public class LocationMapImagesTest
{
	@Test
	public void loadsArrowAndIcon()
	{
		assertNotNull(LocationMapImages.arrow());
		assertNotNull(LocationMapImages.icon());
		assertEquals(LocationMapImages.ICON_SIZE, LocationMapImages.icon().getWidth());
		assertEquals(LocationMapImages.ICON_SIZE, LocationMapImages.icon().getHeight());
	}

	@Test
	public void edgePinsKeepTheTooltipAndPointTowardThatEdge()
	{
		BufferedImage south = LocationMapImages.pin(LocationMapEdge.SOUTH);
		BufferedImage north = LocationMapImages.pin(LocationMapEdge.NORTH);
		BufferedImage west = LocationMapImages.pin(LocationMapEdge.WEST);
		BufferedImage east = LocationMapImages.pin(LocationMapEdge.EAST);

		assertEquals(32, south.getWidth());
		assertEquals(39, south.getHeight());
		assertEquals(32, north.getWidth());
		assertEquals(39, north.getHeight());
		assertEquals(39, west.getWidth());
		assertEquals(32, west.getHeight());
		assertEquals(39, east.getWidth());
		assertEquals(32, east.getHeight());

		assertTrue(opaque(south, south.getWidth() / 2, south.getHeight() - 1));
		assertTrue(opaque(north, north.getWidth() / 2, 0));
		assertTrue(opaque(west, 0, west.getHeight() / 2));
		assertTrue(opaque(east, east.getWidth() - 1, east.getHeight() / 2));
	}

	private static boolean opaque(BufferedImage image, int x, int y)
	{
		return ((image.getRGB(x, y) >> 24) & 0xff) > 0;
	}
}
