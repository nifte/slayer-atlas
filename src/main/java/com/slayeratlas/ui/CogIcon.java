package com.slayeratlas.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import net.runelite.client.ui.ColorScheme;

public final class CogIcon
{
	static final String IDLE = "idle";
	static final String HOVER = "hover";

	private static final int SIZE = 16;
	private static final Color IDLE_COLOR = ColorScheme.LIGHT_GRAY_COLOR;
	private static final Color HOVER_COLOR = ColorScheme.LIGHT_GRAY_COLOR.darker();
	private static final ImageIcon IDLE_ICON = icon(IDLE, IDLE_COLOR);
	private static final ImageIcon HOVER_ICON = icon(HOVER, HOVER_COLOR);

	private CogIcon()
	{
	}

	public static ImageIcon idle()
	{
		return IDLE_ICON;
	}

	public static ImageIcon hover()
	{
		return HOVER_ICON;
	}

	private static ImageIcon icon(String description, Color color)
	{
		BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(color);
		graphics.fill(gear());
		graphics.dispose();
		ImageIcon icon = new ImageIcon(image);
		icon.setDescription(description);
		return icon;
	}

	private static Path2D.Float gear()
	{
		Path2D.Float path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
		double center = SIZE / 2.0;
		double outer = center - 0.7;
		double inner = outer * 0.68;
		double hole = outer * 0.32;
		int teeth = 8;
		for (int point = 0; point < teeth * 2; point++)
		{
			double radius = point % 2 == 0 ? outer : inner;
			double angle = -Math.PI / 2 + point * Math.PI / teeth;
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

		int holePoints = 16;
		for (int point = 0; point < holePoints; point++)
		{
			double angle = -point * 2 * Math.PI / holePoints;
			float x = (float) (center + Math.cos(angle) * hole);
			float y = (float) (center + Math.sin(angle) * hole);
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
