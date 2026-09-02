package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Cursor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class SettingsButtonTest
{
	@Test
	public void isASmallIconButton()
	{
		SettingsButton button = new SettingsButton(() ->
		{
		});
		assertEquals("open-settings", button.getName());
		assertEquals(PanelCopy.OPEN_SETTINGS, button.getToolTipText());
		assertEquals(CogIcon.idle(), button.getIcon());
		assertEquals(CogIcon.hover(), button.getRolloverIcon());
		assertTrue(button.isRolloverEnabled());
		assertFalse(button.isContentAreaFilled());
		assertEquals(Cursor.DEFAULT_CURSOR, button.getCursor().getType());
		assertEquals(20, button.getPreferredSize().width);
		assertEquals(20, button.getPreferredSize().height);
	}

	@Test
	public void clickOpensSettings()
	{
		AtomicBoolean opened = new AtomicBoolean();
		new SettingsButton(() -> opened.set(true)).doClick();
		assertTrue(opened.get());
	}

	@Test
	public void clickDoesNothingWithoutAnOpener()
	{
		SettingsButton button = new SettingsButton(null);
		assertNotNull(button);
		button.doClick();
	}
}
