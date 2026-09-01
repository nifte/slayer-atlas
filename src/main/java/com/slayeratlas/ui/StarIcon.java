package com.slayeratlas.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import net.runelite.client.ui.ColorScheme;

public final class StarIcon
{
	static final String ON = "on";
	static final String OFF = "off";

	private static final int SIZE = 14;
	private static final ImageIcon ON_ICON = icon(ON, true);
	private static final ImageIcon OFF_ICON = icon(OFF, false);

	private StarIcon()
	{
	}

	public static ImageIcon on()
	{
		return ON_ICON;
	}

	public static ImageIcon off()
	{
		return OFF_ICON;
	}

	private static ImageIcon icon(String description, boolean filled)
	{
		BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(filled ? ColorScheme.BRAND_ORANGE : ColorScheme.LIGHT_GRAY_COLOR);
		graphics.fill(star());
		graphics.dispose();
		ImageIcon icon = new ImageIcon(image);
		icon.setDescription(description);
		return icon;
	}

	private static Path2D.Float star()
	{
		Path2D.Float path = new Path2D.Float();
		double center = SIZE / 2.0;
		double outer = center - 0.75;
		double inner = outer * 0.42;
		for (int point = 0; point < 10; point++)
		{
			double radius = point % 2 == 0 ? outer : inner;
			double angle = -Math.PI / 2 + point * Math.PI / 5;
			float x = (float) (center + Math.cos(angle) * radius);
			float y = (float) (center + Math.sin(angle) * radius);
			if (point == 0)
			{
				path.moveTo(x, y);
			}
			else
			{
				path.lineTo(x, y);
			}
		}
		path.closePath();
		return path;
	}
}
