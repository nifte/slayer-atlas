package com.slayeratlas.path;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;

@Singleton
public class ShortestPathService
{
	public static final String PLUGIN_NAME = "Shortest Path";
	private static final String NAMESPACE = "shortestpath";
	private static final String PATH = "path";
	private static final String CLEAR = "clear";
	private static final String TARGET = "target";

	private final PluginManager pluginManager;
	private final EventBus eventBus;
	private final ClientThread clientThread;

	@Inject
	public ShortestPathService(PluginManager pluginManager, EventBus eventBus, ClientThread clientThread)
	{
		this.pluginManager = pluginManager;
		this.eventBus = eventBus;
		this.clientThread = clientThread;
	}

	public boolean isPluginActive()
	{
		for (Plugin plugin : pluginManager.getPlugins())
		{
			if (PLUGIN_NAME.equals(plugin.getName()) && pluginManager.isPluginEnabled(plugin))
			{
				return true;
			}
		}
		return false;
	}

	public void pathTo(WorldPoint point)
	{
		if (point == null)
		{
			return;
		}
		Map<String, Object> data = new HashMap<>();
		data.put(TARGET, point);
		post(PATH, data);
	}

	public void pathTo(Collection<WorldPoint> points)
	{
		if (points == null || points.isEmpty())
		{
			return;
		}
		if (points.size() == 1)
		{
			pathTo(points.iterator().next());
			return;
		}
		Set<WorldPoint> targets = new HashSet<>(points);
		Map<String, Object> data = new HashMap<>();
		data.put(TARGET, targets);
		post(PATH, data);
	}

	public void clear()
	{
		clientThread.invoke(() -> eventBus.post(new PluginMessage(NAMESPACE, CLEAR)));
	}

	private void post(String action, Map<String, Object> data)
	{
		clientThread.invoke(() -> eventBus.post(new PluginMessage(NAMESPACE, action, data)));
	}
}
