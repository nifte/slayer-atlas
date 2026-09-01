package com.slayeratlas.map;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import org.junit.Test;

public class LocationMapPinsTest
{
	@Test
	public void showIgnoresAMissingLocation()
	{
		LocationMapPins pins = new LocationMapPins(null, null, null);
		pins.show(null);
	}

	@Test
	public void bringToFrontReAddsThePinAfterLaterIcons()
	{
		WorldMapPointManager manager = new WorldMapPointManager();
		LocationWorldMapPoint pin = LocationWorldMapPoint.of(new WorldPoint(2278, 3611, 0), "Kraken Cove");
		WorldMapPoint other = new WorldMapPoint(new WorldPoint(1, 1, 0), pin.getImage());
		manager.add(pin);
		manager.add(other);

		LocationMapPins.bringToFront(manager, pin);
	}

	@Test
	public void closingTheWorldMapRemovesThePin()
	{
		WorldMapPointManager manager = new WorldMapPointManager();
		LocationMapPins pins = new LocationMapPins(null, null, manager);
		LocationWorldMapPoint pin = LocationWorldMapPoint.of(new WorldPoint(2278, 3611, 0), "Kraken Cove");
		LocationMapPins.bringToFront(manager, pin);

		pins.onWidgetClosed(new WidgetClosed(InterfaceID.WORLDMAP, 0, true));
	}
}
