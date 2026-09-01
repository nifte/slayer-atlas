package com.slayeratlas.map;

import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;

@Slf4j
final class LocationMapOpen
{
	private LocationMapOpen()
	{
	}

	static boolean isOpen(Client client)
	{
		Widget map = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
		if (map != null && !map.isHidden())
		{
			return true;
		}
		Widget root = client.getWidget(InterfaceID.WORLDMAP, 0);
		return root != null && !root.isHidden();
	}

	static void center(Client client, WorldPoint point)
	{
		WorldMap worldMap = client.getWorldMap();
		if (worldMap != null)
		{
			worldMap.setWorldMapPositionTarget(point);
		}
	}

	static void open(Client client)
	{
		Widget button = findButton(client);
		if (button == null)
		{
			log.warn("Could not find a world map orb widget");
			return;
		}
		int op = option(button.getActions());
		String name = optionName(button.getActions(), op);
		log.info("Opening world map via menuAction widget={} op={} option={}", button.getId(), op, name);
		client.menuAction(-1, button.getId(), MenuAction.CC_OP, op, -1, name, "");
	}

	static int option(String[] actions)
	{
		if (actions == null)
		{
			return 1;
		}
		for (int i = 0; i < actions.length; i++)
		{
			if (isWorldMapAction(actions[i]))
			{
				return i + 1;
			}
		}
		return 1;
	}

	private static String optionName(String[] actions, int op)
	{
		if (actions == null || op < 1 || op > actions.length || actions[op - 1] == null)
		{
			return "World Map";
		}
		return actions[op - 1];
	}

	private static Widget findButton(Client client)
	{
		Widget[] roots = {
			client.getWidget(InterfaceID.Orbs.WORLDMAP),
			client.getWidget(InterfaceID.Orbs.ORB_WORLDMAP),
			client.getWidget(InterfaceID.Orbs.WORLDMAP_BACKING),
			client.getWidget(InterfaceID.Orbs.UNIVERSE)
		};
		for (Widget root : roots)
		{
			Widget match = findByAction(root);
			if (match != null)
			{
				return match;
			}
		}
		return null;
	}

	private static Widget findByAction(Widget widget)
	{
		return find(widget, LocationMapOpen::hasWorldMapAction);
	}

	private static Widget find(Widget widget, Predicate<Widget> match)
	{
		if (widget == null)
		{
			return null;
		}
		if (match.test(widget))
		{
			return widget;
		}
		Widget[][] groups = {
			widget.getChildren(),
			widget.getDynamicChildren(),
			widget.getStaticChildren(),
			widget.getNestedChildren()
		};
		for (Widget[] group : groups)
		{
			if (group == null)
			{
				continue;
			}
			for (Widget child : group)
			{
				Widget found = find(child, match);
				if (found != null)
				{
					return found;
				}
			}
		}
		return null;
	}

	private static boolean hasWorldMapAction(Widget widget)
	{
		String[] actions = widget.getActions();
		if (actions == null)
		{
			return false;
		}
		for (String action : actions)
		{
			if (isWorldMapAction(action))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isWorldMapAction(String action)
	{
		return action != null && action.toLowerCase().contains("world map");
	}
}
