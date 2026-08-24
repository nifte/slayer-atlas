package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayerguide.ComponentLookup;
import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.MonsterDatabase;
import com.slayerguide.data.SlayerMonster;
import java.awt.Component;
import javax.swing.JTextArea;
import net.runelite.client.ui.ColorScheme;
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
		assertEquals("Skeletal Wyverns (current task)", label.getText());
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
}
