package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
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

	@Test
	public void wrappingTextDoesNotFollowCaretIntoParentScroll()
	{
		JTextArea area = PanelWidgets.wrapped("Notes about this assignment go here.");
		assertTrue(area.getCaret() instanceof DefaultCaret);
		assertEquals(DefaultCaret.NEVER_UPDATE, ((DefaultCaret) area.getCaret()).getUpdatePolicy());
	}

	@Test
	public void wrappingTextUsesItsOwnWidthWhenAlreadySized()
	{
		String text = "Taverley Dungeon (black dragons) extra words so any font wraps";
		JTextArea area = PanelWidgets.wrappingText(text, Color.WHITE, PanelFonts.heading());
		int fallbackHalf = (PluginPanel.PANEL_WIDTH - 20) / 2;
		int wide = PluginPanel.PANEL_WIDTH;
		int narrow = fallbackHalf + 8;
		FontMetrics metrics = area.getFontMetrics(area.getFont());
		assertTrue(metrics.stringWidth(text) > narrow);

		area.setSize(wide, 50);
		int wideHeight = area.getPreferredSize().height;
		area.setSize(narrow, 50);
		int narrowHeight = area.getPreferredSize().height;

		assertTrue(narrowHeight > wideHeight);
	}
}
