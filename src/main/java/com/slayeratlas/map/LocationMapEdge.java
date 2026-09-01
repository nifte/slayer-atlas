package com.slayeratlas.map;

import java.awt.Rectangle;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;

enum LocationMapEdge
{
	SOUTH(0, 0, -1),
	WEST(1, 1, 0),
	NORTH(2, 0, 1),
	EAST(3, -1, 0);

	private final int quarterTurns;
	private final int nudgeX;
	private final int nudgeY;

	LocationMapEdge(int quarterTurns, int nudgeX, int nudgeY)
	{
		this.quarterTurns = quarterTurns;
		this.nudgeX = nudgeX;
		this.nudgeY = nudgeY;
	}

	int quarterTurns()
	{
		return quarterTurns;
	}

	int nudgeX()
	{
		return nudgeX;
	}

	int nudgeY()
	{
		return nudgeY;
	}

	static LocationMapEdge of(Client client, WorldPoint point)
	{
		if (client == null || point == null)
		{
			return SOUTH;
		}
		try
		{
			WorldMap worldMap = client.getWorldMap();
			Widget map = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
			if (worldMap == null || map == null)
			{
				return SOUTH;
			}
			float zoom = worldMap.getWorldMapZoom();
			Rectangle bounds = map.getBounds();
			Point center = worldMap.getWorldMapPosition();
			if (zoom <= 0 || bounds == null || bounds.width <= 0 || bounds.height <= 0 || center == null)
			{
				return SOUTH;
			}
			int widthTiles = (int) Math.ceil(bounds.getWidth() / zoom);
			int heightTiles = (int) Math.ceil(bounds.getHeight() / zoom);
			return of(
				point.getX(),
				point.getY(),
				center.getX() - widthTiles / 2,
				center.getY() - heightTiles / 2,
				center.getX() + widthTiles / 2,
				center.getY() + heightTiles / 2);
		}
		catch (RuntimeException ex)
		{
			return SOUTH;
		}
	}

	static LocationMapEdge of(int x, int y, int minX, int minY, int maxX, int maxY)
	{
		int west = minX - x;
		int east = x - maxX;
		int south = minY - y;
		int north = y - maxY;
		LocationMapEdge edge = WEST;
		int best = west;
		if (east > best)
		{
			best = east;
			edge = EAST;
		}
		if (south > best)
		{
			best = south;
			edge = SOUTH;
		}
		if (north > best)
		{
			return NORTH;
		}
		return edge;
	}
}
