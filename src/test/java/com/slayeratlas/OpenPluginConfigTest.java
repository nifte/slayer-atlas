package com.slayeratlas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import net.runelite.api.MenuAction;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.junit.Test;

public class OpenPluginConfigTest
{
	@Test
	public void clickAsksRuneLiteToConfigureThePlugin()
	{
		Plugin plugin = new TestPlugin();
		OverlayMenuClicked click = OpenPluginConfig.click(plugin);
		assertEquals(MenuAction.RUNELITE_OVERLAY_CONFIG, click.getEntry().getMenuAction());
		assertEquals("Configure", click.getEntry().getOption());
		assertSame(plugin, click.getOverlay().getPlugin());
		assertEquals("Test Plugin", click.getOverlay().getPlugin().getName());
	}

	@PluginDescriptor(name = "Test Plugin")
	private static final class TestPlugin extends Plugin
	{
	}
}
