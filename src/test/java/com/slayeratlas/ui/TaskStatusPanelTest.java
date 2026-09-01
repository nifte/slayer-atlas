package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import org.junit.Test;

public class TaskStatusPanelTest
{
	@Test
	public void usesOrangeTextLikeTheCurrentListEntry()
	{
		TaskStatusPanel panel = new TaskStatusPanel(() ->
		{
		}, MonsterImageLoader.none());
		panel.update(new CurrentSlayerTask("Skeletal Wyverns", null, 31, 31), null);
		JTextArea label = (JTextArea) ComponentLookup.named(panel, "current-task-label");
		assertEquals(ColorScheme.BRAND_ORANGE, label.getForeground());
		assertEquals("Skeletal Wyverns (Current)", label.getText());
	}

	@Test
	public void showsAPortraitBesideTheTaskName()
	{
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		TaskStatusPanel panel = new TaskStatusPanel(() ->
		{
		}, MonsterImageLoader.none());
		panel.update(new CurrentSlayerTask("Skeletal Wyverns", null, 31, 31), wyverns);

		Component portrait = ComponentLookup.named(panel, "current-task-portrait");
		assertNotNull(portrait);
		assertTrue(portrait instanceof MonsterPortrait);
		assertEquals(MonsterImageSizes.LIST, portrait.getPreferredSize().width);
	}

	@Test
	public void centersTheTaskNameWithThePortrait()
	{
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findByTaskName("Cave kraken");
		TaskStatusPanel panel = new TaskStatusPanel(() ->
		{
		}, MonsterImageLoader.none());
		panel.update(new CurrentSlayerTask("Cave kraken", null, 142, 142), kraken);
		panel.setSize(PluginPanel.PANEL_WIDTH, 48);
		layoutTree(panel);

		Component portrait = ComponentLookup.named(panel, "current-task-portrait");
		JTextArea label = (JTextArea) ComponentLookup.named(panel, "current-task-label");
		assertEquals(label.getPreferredSize().height, label.getHeight());

		Point portraitCenter = SwingUtilities.convertPoint(
			portrait,
			new Point(0, portrait.getHeight() / 2),
			panel);
		Point labelCenter = SwingUtilities.convertPoint(
			label,
			new Point(0, label.getHeight() / 2),
			panel);
		assertEquals(portraitCenter.y, labelCenter.y);
	}

	private static void layoutTree(Container root)
	{
		root.doLayout();
		for (Component child : root.getComponents())
		{
			if (child instanceof Container)
			{
				layoutTree((Container) child);
			}
		}
	}
}
