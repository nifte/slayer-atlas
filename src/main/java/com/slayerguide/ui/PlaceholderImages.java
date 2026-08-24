package com.slayerguide.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.ColorScheme;

public final class PlaceholderImages
{
	private PlaceholderImages()
	{
	}

	public static BufferedImage square(int size)
	{
		int length = Math.max(1, size);
		BufferedImage image = new BufferedImage(length, length, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(ColorScheme.DARKER_GRAY_HOVER_COLOR);
		graphics.fillRect(0, 0, length, length);
		graphics.setColor(new Color(255, 255, 255, 40));
		graphics.drawRect(0, 0, length - 1, length - 1);
		graphics.dispose();
		return image;
	}
}
