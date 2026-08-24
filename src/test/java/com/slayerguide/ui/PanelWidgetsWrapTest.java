package com.slayerguide.ui;

import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import net.runelite.client.ui.PluginPanel;
import org.junit.Test;

public class PanelWidgetsWrapTest
{
	@Test
	public void titleSubtextWrapsWithinPanelWidth()
	{
		String text = "Search any assignment, or follow your current task.";
		int panelWidth = PluginPanel.PANEL_WIDTH - 20;
		JTextArea area = PanelWidgets.wrapped(text);
		JPanel parent = new JPanel();
		parent.setSize(panelWidth, 400);
		parent.add(area);

		FontMetrics metrics = area.getFontMetrics(area.getFont());
		assertTrue(
			"Title subtext should be wider than the plugin panel when unwrapped.",
			metrics.stringWidth(text) > panelWidth);

		Dimension preferred = area.getPreferredSize();
		assertTrue(
			"Wrapped subtext should use more than one line in the plugin panel.",
			preferred.height > metrics.getHeight() + 2);
		assertTrue(preferred.width <= panelWidth);
		assertTrue(
			"Wrapped subtext should stay a few lines, not stretch the client window.",
			preferred.height < metrics.getHeight() * 6);
	}

	@Test
	public void wrappingIgnoresTinyParentWidth()
	{
		String text = "Search any assignment, or follow your current task.";
		JTextArea area = PanelWidgets.wrapped(text);
		JPanel parent = new JPanel();
		parent.setSize(1, 400);
		parent.add(area);

		FontMetrics metrics = area.getFontMetrics(area.getFont());
		Dimension preferred = area.getPreferredSize();
		assertTrue(
			"A 1px parent must not wrap one character per line.",
			preferred.height < metrics.getHeight() * 6);
	}
}
