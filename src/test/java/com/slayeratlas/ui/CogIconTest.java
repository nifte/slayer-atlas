package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.ColorScheme;
import org.junit.Test;

public class CogIconTest
{
	@Test
	public void idleAndHoverCogsAreDistinct()
	{
		assertEquals(CogIcon.IDLE, CogIcon.idle().getDescription());
		assertEquals(CogIcon.HOVER, CogIcon.hover().getDescription());
		assertNotSame(CogIcon.idle(), CogIcon.hover());
	}

	@Test
	public void idleCogUsesLightGrayWithAHollowCenter()
	{
		BufferedImage idle = (BufferedImage) CogIcon.idle().getImage();
		assertEquals(16, idle.getWidth());
		assertEquals(16, idle.getHeight());
		assertEquals(0, new Color(idle.getRGB(8, 8), true).getAlpha());
		assertTrue(contains(idle, ColorScheme.LIGHT_GRAY_COLOR));
	}

	@Test
	public void hoverCogIsDarker()
	{
		BufferedImage idle = (BufferedImage) CogIcon.idle().getImage();
		BufferedImage hover = (BufferedImage) CogIcon.hover().getImage();
		assertTrue(contains(hover, ColorScheme.LIGHT_GRAY_COLOR.darker()));
		assertTrue(brightness(hover) < brightness(idle));
	}

	private static boolean contains(BufferedImage image, Color color)
	{
		int expected = color.getRGB();
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if (image.getRGB(x, y) == expected)
				{
					return true;
				}
			}
		}
		return false;
	}

	private static int brightness(BufferedImage image)
	{
		int total = 0;
		int count = 0;
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				Color color = new Color(image.getRGB(x, y), true);
				if (color.getAlpha() == 0)
				{
					continue;
				}
				total += color.getRed() + color.getGreen() + color.getBlue();
				count++;
			}
		}
		return count == 0 ? 0 : total / count;
	}
}
