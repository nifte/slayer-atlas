package com.slayeratlas.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.ColorScheme;

final class LocationMapMarker
{
	private static final int SIZE = 16;

	private LocationMapMarker()
	{
	}

	static BufferedImage image()
	{
		BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillOval(1, 1, SIZE - 2, SIZE - 2);
		graphics.setColor(ColorScheme.BRAND_ORANGE);
		graphics.fillOval(3, 3, SIZE - 6, SIZE - 6);
		graphics.dispose();
		return image;
	}
}
