package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import java.awt.image.BufferedImage;
import net.runelite.client.ui.ColorScheme;
import org.junit.Test;

public class StarIconTest
{
	@Test
	public void onAndOffStarsAreDistinct()
	{
		assertEquals(StarIcon.ON, StarIcon.on().getDescription());
		assertEquals(StarIcon.OFF, StarIcon.off().getDescription());
		assertNotSame(StarIcon.on(), StarIcon.off());
	}

	@Test
	public void filledStarUsesTheBrandColor()
	{
		BufferedImage on = (BufferedImage) StarIcon.on().getImage();
		BufferedImage off = (BufferedImage) StarIcon.off().getImage();
		int centerX = on.getWidth() / 2;
		int centerY = on.getHeight() / 2;
		assertEquals(ColorScheme.BRAND_ORANGE.getRGB(), on.getRGB(centerX, centerY));
		assertNotEquals(ColorScheme.BRAND_ORANGE.getRGB(), off.getRGB(centerX, centerY));
	}
}
