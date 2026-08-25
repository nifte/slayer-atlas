package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.ComponentLookup;
import javax.swing.JButton;
import javax.swing.SwingConstants;
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
	}

	@Test
	public void disablesDpsWhenNoLinkIsAvailable()
	{
		HeaderActionButtons row = new HeaderActionButtons(() ->
		{
		}, null);
		assertFalse(((JButton) ComponentLookup.named(row, "open-dps")).isEnabled());
	}
}
