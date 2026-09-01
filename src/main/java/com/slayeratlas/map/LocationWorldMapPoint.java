package com.slayeratlas.map;

import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

/**
 * Tooltip arrow on the map. When snapped to an edge, the tooltip stays and points toward that edge.
 */
public class LocationWorldMapPoint extends WorldMapPoint
{
	private final Client client;
	private final BufferedImage mapImage;
	private final BufferedImage[] edgeImages;
	private final Point mapImagePoint;
	private LocationMapEdge edge = LocationMapEdge.SOUTH;

	public static LocationWorldMapPoint of(WorldPoint worldPoint, String name)
	{
		return of(worldPoint, name, null);
	}

	public static LocationWorldMapPoint of(WorldPoint worldPoint, String name, Client client)
	{
		try
		{
			return new LocationWorldMapPoint(worldPoint, LocationMapImages.arrow(), LocationMapImages.icon(), name, client);
		}
		catch (RuntimeException ex)
		{
			BufferedImage fallback = LocationMapMarker.image();
			return new LocationWorldMapPoint(worldPoint, fallback, fallback, name, client);
		}
	}

	LocationWorldMapPoint(WorldPoint worldPoint, BufferedImage arrow, BufferedImage icon, String name)
	{
		this(worldPoint, arrow, icon, name, null);
	}

	LocationWorldMapPoint(WorldPoint worldPoint, BufferedImage arrow, BufferedImage icon, String name, Client client)
	{
		super(worldPoint, null);
		this.client = client;
		mapImage = LocationMapImages.pin(arrow, icon, LocationMapEdge.SOUTH);
		edgeImages = new BufferedImage[LocationMapEdge.values().length];
		for (LocationMapEdge direction : LocationMapEdge.values())
		{
			edgeImages[direction.ordinal()] = direction == LocationMapEdge.SOUTH
				? mapImage
				: LocationMapImages.pin(arrow, icon, direction);
		}
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
		showEdge(LocationMapEdge.of(client, getWorldPoint()));
	}

	@Override
	public void onEdgeUnsnap()
	{
		setImage(mapImage);
		setImagePoint(mapImagePoint);
	}

	void updateEdge()
	{
		if (isCurrentlyEdgeSnapped())
		{
			showEdge(LocationMapEdge.of(client, getWorldPoint()));
		}
	}

	void face(LocationMapEdge direction)
	{
		if (direction == null)
		{
			return;
		}
		edge = direction;
		if (isCurrentlyEdgeSnapped())
		{
			showEdge(direction);
		}
	}

	private void showEdge(LocationMapEdge direction)
	{
		edge = direction == null ? LocationMapEdge.SOUTH : direction;
		setImage(edgeImages[edge.ordinal()]);
		setImagePoint(null);
	}
}
