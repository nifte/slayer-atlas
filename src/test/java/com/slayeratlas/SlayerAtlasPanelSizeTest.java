package com.slayeratlas;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayeratlas.data.MonsterDatabase;
import java.awt.Dimension;
import net.runelite.client.ui.PluginPanel;
import org.junit.Test;

public class SlayerAtlasPanelSizeTest
{
	@Test
	public void doesNotRequestATallClientWindow()
	{
		SlayerAtlasPanel panel = new SlayerAtlasPanel(
			new MonsterDatabase(new Gson()),
			null,
			new SlayerAtlasConfig()
			{
			});

		Dimension preferred = panel.getPreferredSize();
		Dimension minimum = panel.getMinimumSize();
		assertEquals(PluginPanel.PANEL_WIDTH + PluginPanel.SCROLLBAR_WIDTH, preferred.width);
		assertEquals(0, preferred.height);
		assertEquals(PluginPanel.PANEL_WIDTH + PluginPanel.SCROLLBAR_WIDTH, minimum.width);
		assertEquals(0, minimum.height);
	}
}
