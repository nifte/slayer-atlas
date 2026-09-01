package com.slayeratlas.map;

import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.api.worldmap.WorldMapData;
import net.runelite.api.worldmap.WorldMapRenderer;

@Slf4j
final class LocationMapLayer
{
	private LocationMapLayer()
	{
	}

	static boolean contains(Client client, WorldPoint point)
	{
		if (client == null || point == null)
		{
			return false;
		}
		WorldMap worldMap = client.getWorldMap();
		if (worldMap == null)
		{
			return false;
		}
		WorldMapData data = worldMap.getWorldMapData();
		return data != null && data.surfaceContainsPosition(point.getX(), point.getY());
	}

	static boolean isReady(Client client)
	{
		if (client == null)
		{
			return false;
		}
		WorldMap worldMap = client.getWorldMap();
		if (worldMap == null)
		{
			return false;
		}
		WorldMapRenderer renderer = worldMap.getWorldMapRenderer();
		return renderer == null || renderer.isLoaded();
	}

	static boolean switchTo(Client client, WorldPoint point)
	{
		if (client == null || point == null)
		{
			return false;
		}
		Widget choice = findLayerChoice(client, point);
		if (choice == null)
		{
			return false;
		}
		int op = selectOp(choice.getActions());
		log.info(
			"Switching world map layer via menuAction widget={} index={} op={} name={}",
			choice.getId(),
			choice.getIndex(),
			op,
			choice.getText());
		client.menuAction(choice.getIndex(), choice.getId(), MenuAction.CC_OP, op, -1, "Select", "");
		return true;
	}

	static boolean isSurfaceName(String text)
	{
		if (text == null || text.isEmpty())
		{
			return false;
		}
		String name = text.toLowerCase(Locale.ROOT).trim();
		return name.equals("gielinor") || name.startsWith("gielinor ") || name.equals("surface");
	}

	static int selectOp(String[] actions)
	{
		if (actions == null)
		{
			return 1;
		}
		for (int i = 0; i < actions.length; i++)
		{
			if (actions[i] != null && actions[i].equalsIgnoreCase("Select"))
			{
				return i + 1;
			}
		}
		return 1;
	}

	private static Widget findLayerChoice(Client client, WorldPoint point)
	{
		Widget[] roots = {
			client.getWidget(InterfaceID.Worldmap.MAPLIST_DISPLAY),
			client.getWidget(InterfaceID.Worldmap.MAPLIST_BOX_GRAPHIC0),
			client.getWidget(InterfaceID.WORLDMAP, 0)
		};
		for (Widget root : roots)
		{
			Widget match = LocationMapOpen.find(root, widget -> matchesLayer(widget, point));
			if (match != null)
			{
				return match;
			}
		}
		return null;
	}

	private static boolean matchesLayer(Widget widget, WorldPoint point)
	{
		if (widget == null || !hasSelect(widget.getActions()))
		{
			return false;
		}
		if (LocationMap.isOnOverworld(point))
		{
			return isSurfaceName(widget.getText());
		}
		return false;
	}

	private static boolean hasSelect(String[] actions)
	{
		if (actions == null)
		{
			return false;
		}
		for (String action : actions)
		{
			if (action != null && action.equalsIgnoreCase("Select"))
			{
				return true;
			}
		}
		return false;
	}
}
