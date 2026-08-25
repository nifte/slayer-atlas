package com.slayeratlas.ui;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import net.runelite.client.ui.ColorScheme;

public final class ChevronIcon
{
	static final String COLLAPSED = "collapsed";
	static final String EXPANDED = "expanded";

	private static final int SIZE = 10;

	private ChevronIcon()
	{
	}

	public static ImageIcon collapsed()
	{
		return icon(COLLAPSED, false);
	}

	public static ImageIcon expanded()
	{
		return icon(EXPANDED, true);
	}

	private static ImageIcon icon(String description, boolean expanded)
	{
		BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(ColorScheme.LIGHT_GRAY_COLOR);
		graphics.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		Path2D chevron = new Path2D.Float();
		if (expanded)
		{
			chevron.moveTo(1.5f, 3f);
			chevron.lineTo(5f, 7.5f);
			chevron.lineTo(8.5f, 3f);
		}
		else
		{
			chevron.moveTo(3f, 1.5f);
			chevron.lineTo(7.5f, 5f);
			chevron.lineTo(3f, 8.5f);
		}
		graphics.draw(chevron);
		graphics.dispose();
		ImageIcon icon = new ImageIcon(image);
		icon.setDescription(description);
		return icon;
	}
}
