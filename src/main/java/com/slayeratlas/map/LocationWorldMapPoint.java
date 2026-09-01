package com.slayeratlas.map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

/**
 * Same shape as RuneLite's clue-scroll map pin: arrow on the map, icon when snapped to the edge.
 */
public class LocationWorldMapPoint extends WorldMapPoint
{
	private final BufferedImage mapImage;
	private final BufferedImage edgeImage;
	private final Point mapImagePoint;

	public static LocationWorldMapPoint of(WorldPoint worldPoint, String name)
	{
		try
		{
			return new LocationWorldMapPoint(worldPoint, LocationMapImages.arrow(), LocationMapImages.icon(), name);
		}
		catch (RuntimeException ex)
		{
			BufferedImage fallback = LocationMapMarker.image();
			return new LocationWorldMapPoint(worldPoint, fallback, fallback, name);
		}
	}

	LocationWorldMapPoint(WorldPoint worldPoint, BufferedImage arrow, BufferedImage icon, String name)
	{
		super(worldPoint, null);
		mapImage = overlay(arrow, icon);
		edgeImage = icon != null ? icon : mapImage;
		mapImagePoint = new Point(mapImage.getWidth() / 2, mapImage.getHeight());
		setSnapToEdge(true);
		setJumpOnClick(true);
		setName(name);
		setTooltip(name);
		setImage(mapImage);
		setImagePoint(mapImagePoint);
	}

	@Override
	public void onEdgeSnap()
	{
		setImage(edgeImage);
		setImagePoint(null);
	}

	@Override
	public void onEdgeUnsnap()
	{
		setImage(mapImage);
		setImagePoint(mapImagePoint);
	}

	private static BufferedImage overlay(BufferedImage arrow, BufferedImage icon)
	{
		BufferedImage combined = new BufferedImage(arrow.getWidth(), arrow.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = combined.getGraphics();
		graphics.drawImage(arrow, 0, 0, null);
		if (icon != null)
		{
			int x = (arrow.getWidth() - icon.getWidth()) / 2;
			int y = Math.max(0, (arrow.getHeight() - icon.getHeight()) / 2 - 3);
			graphics.drawImage(icon, x, y, null);
		}
		graphics.dispose();
		return combined;
	}
}
