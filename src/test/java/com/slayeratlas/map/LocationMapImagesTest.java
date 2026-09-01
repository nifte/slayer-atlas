package com.slayeratlas.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}
