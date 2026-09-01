package com.slayeratlas.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import org.junit.Test;

public class LocationMapMarkerTest
{
	@Test
	public void paintsAVisiblePin()
	{
		BufferedImage image = LocationMapMarker.image();
		assertNotNull(image);
		assertEquals(16, image.getWidth());
		assertEquals(16, image.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
	}
}
