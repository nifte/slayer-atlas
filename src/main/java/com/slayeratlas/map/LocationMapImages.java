package com.slayeratlas.map;

import java.awt.image.BufferedImage;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.util.ImageUtil;

final class LocationMapImages
{
	static final int ICON_SIZE = 22;

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
