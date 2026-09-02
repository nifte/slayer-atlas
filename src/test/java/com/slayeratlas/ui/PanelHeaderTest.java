package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.ComponentLookup;
import java.awt.BorderLayout;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import org.junit.Test;

public class PanelHeaderTest
{
	@Test
	public void placesTheTitleOnTheLeftAndSettingsOnTheRight()
	{
		PanelHeader header = new PanelHeader(() ->
		{
		});
		BorderLayout layout = (BorderLayout) header.getLayout();
		assertEquals("panel-header", header.getName());
		assertTrue(ComponentLookup.containsText(header, PanelCopy.TITLE));
		assertSame(ComponentLookup.named(header, "panel-title"), layout.getLayoutComponent(BorderLayout.WEST));
		assertSame(ComponentLookup.named(header, "open-settings"), layout.getLayoutComponent(BorderLayout.EAST));
		assertEquals(Integer.MAX_VALUE, header.getMaximumSize().width);
	}

	@Test
	public void settingsCogOpensTheConfigurationMenu()
	{
		AtomicBoolean opened = new AtomicBoolean();
		PanelHeader header = new PanelHeader(() -> opened.set(true));
		((JButton) ComponentLookup.named(header, "open-settings")).doClick();
		assertTrue(opened.get());
	}
}
