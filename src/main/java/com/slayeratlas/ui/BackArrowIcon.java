package com.slayeratlas.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public final class BackArrowIcon
{
	private static final int SIZE = 12;

	private BackArrowIcon()
	{
	}

	public static ImageIcon icon()
	{
		BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		int centerY = SIZE / 2;
		graphics.drawLine(SIZE - 2, centerY, 2, centerY);
		graphics.drawLine(2, centerY, 6, centerY - 4);
		graphics.drawLine(2, centerY, 6, centerY + 4);
		graphics.dispose();
		return new ImageIcon(image);
	}
}
