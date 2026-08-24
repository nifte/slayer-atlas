package com.slayerguide.ui;

import java.awt.image.BufferedImage;
import net.runelite.client.util.ImageUtil;

public final class MonsterImageScaler
{
	private MonsterImageScaler()
	{
	}

	public static BufferedImage fitSquare(BufferedImage source, int size)
	{
		if (source == null || size <= 0)
		{
			return source;
		}
		int width = Math.max(1, source.getWidth());
		int height = Math.max(1, source.getHeight());
		double ratio = Math.min(size / (double) width, size / (double) height);
		int scaledWidth = Math.max(1, (int) Math.round(width * ratio));
		int scaledHeight = Math.max(1, (int) Math.round(height * ratio));
		BufferedImage scaled = ImageUtil.resizeImage(source, scaledWidth, scaledHeight, true);
		return ImageUtil.resizeCanvas(scaled, size, size);
	}
}
