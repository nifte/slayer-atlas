package com.slayeratlas.map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.util.ImageUtil;

final class LocationMapImages
{
	static final int ICON_SIZE = 22;
	private static final int ICON_NUDGE = 3;

	private LocationMapImages()
	{
	}

	static BufferedImage arrow()
	{
		BufferedImage loaded = load(LocationWorldMapPoint.class, "map_arrow.png");
		return loaded != null ? loaded : LocationMapMarker.image();
	}

	static BufferedImage icon()
	{
		try
		{
			BufferedImage slayer = new SkillIconManager().getSkillImage(Skill.SLAYER, false);
			if (slayer != null)
			{
				return ImageUtil.resizeImage(slayer, ICON_SIZE, ICON_SIZE);
			}
		}
		catch (RuntimeException ex)
		{
			// Fall through to the drawn marker.
		}
		return LocationMapMarker.image();
	}

	static BufferedImage pin(LocationMapEdge edge)
	{
		return pin(arrow(), icon(), edge);
	}

	static BufferedImage pin(BufferedImage arrow, BufferedImage icon, LocationMapEdge edge)
	{
		LocationMapEdge direction = edge == null ? LocationMapEdge.SOUTH : edge;
		return overlay(rotate(arrow, direction.quarterTurns()), icon, direction);
	}

	static BufferedImage rotate(BufferedImage source, int quarterTurns)
	{
		int turns = quarterTurns & 3;
		if (source == null || turns == 0)
		{
			return source;
		}
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage rotated = turns == 2
			? new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			: new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int pixel = source.getRGB(x, y);
				if (turns == 1)
				{
					rotated.setRGB(height - 1 - y, x, pixel);
				}
				else if (turns == 2)
				{
					rotated.setRGB(width - 1 - x, height - 1 - y, pixel);
				}
				else
				{
					rotated.setRGB(y, width - 1 - x, pixel);
				}
			}
		}
		return rotated;
	}

	private static BufferedImage overlay(BufferedImage arrow, BufferedImage icon, LocationMapEdge edge)
	{
		BufferedImage combined = new BufferedImage(arrow.getWidth(), arrow.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = combined.getGraphics();
		graphics.drawImage(arrow, 0, 0, null);
		if (icon != null)
		{
			int x = (arrow.getWidth() - icon.getWidth()) / 2 + edge.nudgeX() * ICON_NUDGE;
			int y = (arrow.getHeight() - icon.getHeight()) / 2 + edge.nudgeY() * ICON_NUDGE;
			int maxX = Math.max(0, arrow.getWidth() - icon.getWidth());
			int maxY = Math.max(0, arrow.getHeight() - icon.getHeight());
			graphics.drawImage(icon, clamp(x, 0, maxX), clamp(y, 0, maxY), null);
		}
		graphics.dispose();
		return combined;
	}

	private static int clamp(int value, int min, int max)
	{
		return Math.max(min, Math.min(max, value));
	}

	private static BufferedImage load(Class<?> type, String path)
	{
		try
		{
			return ImageUtil.loadImageResource(type, path);
		}
		catch (RuntimeException ex)
		{
			return null;
		}
	}
}
