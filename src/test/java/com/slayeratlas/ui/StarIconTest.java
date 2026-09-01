package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import java.awt.Color;
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
		assertEquals(StarIcon.ON_HOVER, StarIcon.onHover().getDescription());
		assertEquals(StarIcon.OFF_HOVER, StarIcon.offHover().getDescription());
		assertNotSame(StarIcon.on(), StarIcon.off());
		assertNotSame(StarIcon.on(), StarIcon.onHover());
		assertNotSame(StarIcon.off(), StarIcon.offHover());
	}

	@Test
	public void filledStarUsesTheBrandColor()
	{
		BufferedImage on = (BufferedImage) StarIcon.on().getImage();
		BufferedImage off = (BufferedImage) StarIcon.off().getImage();
		int centerX = on.getWidth() / 2;
		int centerY = on.getHeight() / 2;
		assertEquals(18, on.getWidth());
		assertEquals(ColorScheme.BRAND_ORANGE.getRGB(), on.getRGB(centerX, centerY));
		assertNotEquals(ColorScheme.BRAND_ORANGE.getRGB(), off.getRGB(centerX, centerY));
	}

	@Test
	public void hoverStarsAreBrighter()
	{
		BufferedImage on = (BufferedImage) StarIcon.on().getImage();
		BufferedImage onHover = (BufferedImage) StarIcon.onHover().getImage();
		BufferedImage offHover = (BufferedImage) StarIcon.offHover().getImage();
		int centerX = on.getWidth() / 2;
		int centerY = on.getHeight() / 2;
		assertEquals(Color.WHITE.getRGB(), offHover.getRGB(centerX, centerY));
		assertNotEquals(on.getRGB(centerX, centerY), onHover.getRGB(centerX, centerY));
	}
}
