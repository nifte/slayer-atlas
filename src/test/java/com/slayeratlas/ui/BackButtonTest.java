package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import org.junit.Test;

public class BackButtonTest
{
	@Test
	public void isFullWidthWithCenteredLabelAndArrow()
	{
		BackButton button = new BackButton(() ->
		{
		});
		assertEquals(PanelCopy.BACK_TO_LIST, button.getText());
		assertNotNull(button.getIcon());
		assertEquals(SwingConstants.CENTER, button.getHorizontalAlignment());
		assertEquals(Integer.MAX_VALUE, button.getMaximumSize().width);
		Dimension preferred = button.getPreferredSize();
		assertTrue(preferred.width > preferred.height);
	}

	@Test
	public void usesTheSameHoverChromeAsWikiButtons()
	{
		BackButton back = new BackButton(() ->
		{
		});
		HeaderLinkButton wiki = new HeaderLinkButton("open-wiki", PanelCopy.OPEN_WIKI, () ->
		{
		});
		assertEquals(wiki.getBackground(), back.getBackground());
		assertEquals(wiki.isRolloverEnabled(), back.isRolloverEnabled());
		assertEquals(wiki.getCursor(), back.getCursor());
		assertEquals(Cursor.DEFAULT_CURSOR, back.getCursor().getType());
		assertTrue(back.isRolloverEnabled());
	}
}
