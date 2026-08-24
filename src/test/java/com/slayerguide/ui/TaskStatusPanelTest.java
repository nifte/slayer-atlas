package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;

import com.slayerguide.data.CurrentSlayerTask;
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
		});
		panel.update(new CurrentSlayerTask("Skeletal Wyverns", null, 31, 31), null);
		JTextArea label = (JTextArea) panel.getComponent(0);
		assertEquals(ColorScheme.BRAND_ORANGE, label.getForeground());
		assertEquals("Skeletal Wyverns (current task)", label.getText());
	}
}
