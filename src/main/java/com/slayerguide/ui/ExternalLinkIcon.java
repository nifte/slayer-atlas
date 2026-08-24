package com.slayerguide.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public final class ExternalLinkIcon
{
	private static final int SIZE = 12;

	private ExternalLinkIcon()
	{
	}

	public static ImageIcon icon()
	{
		BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawLine(1, 4, 1, 11);
		graphics.drawLine(1, 11, 8, 11);
		graphics.drawLine(8, 11, 8, 7);
		graphics.drawLine(1, 4, 5, 4);
		graphics.drawLine(5, 7, 10, 2);
		graphics.drawLine(10, 2, 7, 2);
		graphics.drawLine(10, 2, 10, 5);
		graphics.dispose();
		return new ImageIcon(image);
	}
}
