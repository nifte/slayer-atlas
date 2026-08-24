package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.junit.Test;

public class ViewportWidthPanelTest
{
	@Test
	public void tracksViewportWidthOnly()
	{
		ViewportWidthPanel view = new ViewportWidthPanel();
		assertTrue(view.getScrollableTracksViewportWidth());
		assertFalse(view.getScrollableTracksViewportHeight());
	}

	@Test
	public void staysInsideTheViewportWhenAScrollbarIsPresent()
	{
		ViewportWidthPanel view = new ViewportWidthPanel();
		view.add(PanelWidgets.wrapped(
			"Slayer helmet (i) if on task. Best-in-slot melee or a budget strength setup."));
		JScrollPane scroll = new JScrollPane(view);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scroll.setSize(222, 300);
		scroll.doLayout();

		int viewportWidth = scroll.getViewport().getWidth();
		assertTrue(viewportWidth > 0);
		assertTrue(viewportWidth <= scroll.getWidth() - 8);
		assertEquals(viewportWidth, view.getWidth());
		assertTrue(view.getWidth() < scroll.getWidth());
	}
}
