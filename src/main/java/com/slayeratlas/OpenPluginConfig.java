package com.slayeratlas;

import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.MenuAction;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;

public final class OpenPluginConfig
{
	private OpenPluginConfig()
	{
	}

	public static OverlayMenuClicked click(Plugin plugin)
	{
		return new OverlayMenuClicked(
			new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", ""),
			new ConfigOverlay(plugin));
	}

	private static final class ConfigOverlay extends Overlay
	{
		private ConfigOverlay(Plugin plugin)
		{
			super(plugin);
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			return null;
		}
	}
}
