package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.ComponentLookup;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import net.runelite.client.ui.PluginPanel;
import org.junit.Test;

public class HeaderActionButtonsTest
{
	@Test
	public void placesWikiAndDpsSideBySide()
	{
		HeaderActionButtons row = new HeaderActionButtons(() ->
		{
		}, () ->
		{
		});
		assertEquals(2, row.getComponentCount());
		JButton wiki = (JButton) ComponentLookup.named(row, "open-wiki");
		JButton dps = (JButton) ComponentLookup.named(row, "open-dps");
		assertEquals(PanelCopy.OPEN_WIKI, wiki.getText());
		assertEquals(PanelCopy.OPEN_DPS, dps.getText());
		assertNotNull(wiki.getIcon());
		assertNotNull(dps.getIcon());
		assertEquals(SwingConstants.LEFT, wiki.getHorizontalTextPosition());
		assertEquals(SwingConstants.LEFT, dps.getHorizontalTextPosition());
		assertTrue(dps.isEnabled());
		assertEquals(Cursor.DEFAULT_CURSOR, wiki.getCursor().getType());
		assertEquals(wiki.getCursor(), dps.getCursor());
		assertTrue(wiki.isRolloverEnabled());
		assertTrue(dps.isRolloverEnabled());
	}

	@Test
	public void disablesDpsWhenNoLinkIsAvailable()
	{
		HeaderActionButtons row = new HeaderActionButtons(() ->
		{
		}, null);
		assertFalse(((JButton) ComponentLookup.named(row, "open-dps")).isEnabled());
	}

	@Test
	public void reducesHorizontalPaddingSoWikiAndDpsFitBesideThePortrait()
	{
		JButton untouched = PanelWidgets.button(PanelCopy.OPEN_WIKI);
		HeaderActionButtons row = new HeaderActionButtons(() ->
		{
		}, () ->
		{
		});
		JButton wiki = (JButton) ComponentLookup.named(row, "open-wiki");
		JButton dps = (JButton) ComponentLookup.named(row, "open-dps");

		assertEquals(untouched.getMargin().top, wiki.getMargin().top);
		assertEquals(untouched.getMargin().bottom, wiki.getMargin().bottom);
		assertTrue(wiki.getMargin().left < untouched.getMargin().left);
		assertTrue(wiki.getMargin().right < untouched.getMargin().right);
		assertEquals(wiki.getMargin(), dps.getMargin());

		int columnWidth = PluginPanel.PANEL_WIDTH - 20 - MonsterImageSizes.DETAIL - 8;
		row.setSize(columnWidth, row.getPreferredSize().height);
		row.doLayout();

		assertLabelFits(wiki, PanelCopy.OPEN_WIKI);
		assertLabelFits(dps, PanelCopy.OPEN_DPS);
	}

	private static void assertLabelFits(JButton button, String label)
	{
		FontMetrics metrics = button.getFontMetrics(button.getFont());
		Insets insets = button.getInsets();
		int iconWidth = button.getIcon() == null ? 0 : button.getIcon().getIconWidth();
		int needed = metrics.stringWidth(label)
			+ iconWidth
			+ button.getIconTextGap()
			+ insets.left
			+ insets.right;
		assertTrue(needed <= button.getWidth());
	}
}
