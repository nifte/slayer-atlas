package com.slayeratlas.map;

import com.slayeratlas.data.MonsterLocation;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

@Slf4j
@Singleton
public class LocationMapPins
{
	private static final int OPEN_RETRY_TICKS = 250;

	private final Client client;
	private final ClientThread clientThread;
	private final WorldMapPointManager worldMapPointManager;
	private LocationWorldMapPoint pin;
	private WorldPoint pendingJump;
	private int waitTicks;
	private boolean mapWasOpen;
	private boolean switchedLayer;

	@Inject
	public LocationMapPins(Client client, ClientThread clientThread, WorldMapPointManager worldMapPointManager)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.worldMapPointManager = worldMapPointManager;
	}

	public void show(MonsterLocation location)
	{
		WorldPoint point = LocationMap.point(location);
		if (point == null)
		{
			return;
		}
		String name = location.getName() == null || location.getName().isEmpty()
			? "Location"
			: location.getName();
		clientThread.invokeLater(() -> display(point, name));
	}

	public void clear()
	{
		clientThread.invokeLater(this::removeMarker);
	}

	public void onClientTick()
	{
		boolean open = LocationMapOpen.isOpen(client);
		if (mapWasOpen && !open)
		{
			removeMarker();
		}
		mapWasOpen = open;
		if (pin != null && open)
		{
			pin.updateEdge();
			bringToFront();
		}
		if (pendingJump == null)
		{
			return;
		}
		if (jumpIfOpen())
		{
			return;
		}
		waitTicks++;
		if (waitTicks > OPEN_RETRY_TICKS)
		{
			log.warn("World map did not open for {}", pendingJump);
			pendingJump = null;
			waitTicks = 0;
		}
	}

	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != InterfaceID.WORLDMAP)
		{
			return;
		}
		bringToFront();
		jumpIfOpen();
	}

	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() != ScriptID.WORLDMAP_LOADMAP)
		{
			return;
		}
		bringToFront();
		jumpIfOpen();
	}

	public void onWidgetClosed(WidgetClosed event)
	{
		if (event.getGroupId() != InterfaceID.WORLDMAP || !event.isUnload())
		{
			return;
		}
		removeMarker();
	}

	private void display(WorldPoint point, String name)
	{
		try
		{
			pin = LocationWorldMapPoint.of(point, name, client);
			bringToFront();
		}
		catch (RuntimeException ex)
		{
			log.warn("Could not add a world map pin for {}", name, ex);
		}
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		try
		{
			client.clearHintArrow();
			client.setHintArrow(point);
		}
		catch (RuntimeException ex)
		{
			log.warn("Could not set a hint arrow for {}", name, ex);
		}
		pendingJump = point;
		waitTicks = 0;
		switchedLayer = false;
		if (!jumpIfOpen())
		{
			try
			{
				LocationMapOpen.open(client);
			}
			catch (RuntimeException ex)
			{
				log.warn("Could not open the world map for {}", name, ex);
			}
		}
		log.info("Showing {} on the world map at {}", name, point);
	}

	private boolean jumpIfOpen()
	{
		if (pendingJump == null || !LocationMapOpen.isOpen(client))
		{
			return false;
		}
		if (!LocationMapLayer.contains(client, pendingJump))
		{
			if (!switchedLayer)
			{
				try
				{
					switchedLayer = LocationMapLayer.switchTo(client, pendingJump);
				}
				catch (RuntimeException ex)
				{
					log.warn("Could not switch the world map layer for {}", pendingJump, ex);
				}
			}
			return false;
		}
		if (!LocationMapLayer.isReady(client))
		{
			return false;
		}
		try
		{
			LocationMapOpen.center(client, pendingJump);
			bringToFront();
		}
		catch (RuntimeException ex)
		{
			log.warn("Could not center the world map on {}", pendingJump, ex);
			return false;
		}
		pendingJump = null;
		waitTicks = 0;
		switchedLayer = false;
		return true;
	}

	private void removeMarker()
	{
		pin = null;
		pendingJump = null;
		waitTicks = 0;
		mapWasOpen = false;
		switchedLayer = false;
		worldMapPointManager.removeIf(LocationWorldMapPoint.class::isInstance);
		if (client != null && client.getGameState() == GameState.LOGGED_IN)
		{
			client.clearHintArrow();
		}
	}

	void bringToFront()
	{
		bringToFront(worldMapPointManager, pin);
	}

	static void bringToFront(WorldMapPointManager manager, LocationWorldMapPoint pin)
	{
		if (manager == null || pin == null)
		{
			return;
		}
		manager.removeIf(LocationWorldMapPoint.class::isInstance);
		manager.add(pin);
	}
}
