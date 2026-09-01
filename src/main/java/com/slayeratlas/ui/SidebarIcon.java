package com.slayeratlas.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class SidebarIcon
{
	static final double ENLARGE = 1.3;

	private SidebarIcon()
	{
	}

	public static BufferedImage enlarge(BufferedImage source)
	{
		if (source == null)
		{
			return null;
		}
		int width = source.getWidth();
		int height = source.getHeight();
		int crop = (int) Math.round(Math.min(width, height) / ENLARGE);
		crop = Math.max(1, Math.min(crop, Math.min(width, height)));
		int x = (width - crop) / 2;
		int y = (height - crop) / 2;
		BufferedImage enlarged = new BufferedImage(crop, crop, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = enlarged.createGraphics();
		graphics.drawImage(source, 0, 0, crop, crop, x, y, x + crop, y + crop, null);
		graphics.dispose();
		return enlarged;
	}
}
