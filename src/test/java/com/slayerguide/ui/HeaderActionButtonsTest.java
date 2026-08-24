package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.slayerguide.ComponentLookup;
import javax.swing.JButton;
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
		assertEquals(PanelCopy.OPEN_WIKI, ((JButton) ComponentLookup.named(row, "open-wiki")).getText());
		assertEquals(PanelCopy.OPEN_DPS, ((JButton) ComponentLookup.named(row, "open-dps")).getText());
		assertTrue(((JButton) ComponentLookup.named(row, "open-dps")).isEnabled());
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
