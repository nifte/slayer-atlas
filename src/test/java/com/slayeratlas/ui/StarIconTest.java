package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

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
		BufferedImage onHover = (BufferedImage) StarIcon.onHover().getImage();
		BufferedImage off = (BufferedImage) StarIcon.off().getImage();
		int centerX = on.getWidth() / 2;
		int centerY = on.getHeight() / 2;
		assertEquals(18, on.getWidth());
		assertEquals(ColorScheme.BRAND_ORANGE.getRGB(), on.getRGB(centerX, centerY));
		assertNotEquals(ColorScheme.BRAND_ORANGE.getRGB(), onHover.getRGB(centerX, centerY));
		assertNotEquals(ColorScheme.LIGHT_GRAY_COLOR.darker().getRGB(), off.getRGB(centerX, centerY));
	}

	@Test
	public void hoverStarsAreDarker()
	{
		BufferedImage on = (BufferedImage) StarIcon.on().getImage();
		BufferedImage off = (BufferedImage) StarIcon.off().getImage();
		BufferedImage onHover = (BufferedImage) StarIcon.onHover().getImage();
		BufferedImage offHover = (BufferedImage) StarIcon.offHover().getImage();
		int centerX = on.getWidth() / 2;
		int centerY = on.getHeight() / 2;
		assertTrue(brightness(offHover, centerX, centerY) < brightness(off, centerX, centerY));
		assertTrue(brightness(onHover, centerX, centerY) < brightness(on, centerX, centerY));
		assertEquals(ColorScheme.LIGHT_GRAY_COLOR.darker().getRGB(), offHover.getRGB(centerX, centerY));
	}

	private static int brightness(BufferedImage image, int x, int y)
	{
		Color color = new Color(image.getRGB(x, y), true);
		return color.getRed() + color.getGreen() + color.getBlue();
	}
}
