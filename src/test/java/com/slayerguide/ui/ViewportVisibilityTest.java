package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import org.junit.Test;

public class ViewportVisibilityTest
{
	@Test
	public void treatsOnscreenRowsAsVisible()
	{
		Rectangle view = new Rectangle(0, 0, 200, 300);
		assertTrue(ViewportVisibility.intersectsView(view, new Rectangle(8, 40, 32, 32)));
	}

	@Test
	public void skipsRowsBelowTheViewport()
	{
		Rectangle view = new Rectangle(0, 0, 200, 300);
		assertFalse(ViewportVisibility.intersectsView(view, new Rectangle(8, 800, 32, 32)));
	}

	@Test
	public void preloadsTheNextRow()
	{
		Rectangle view = new Rectangle(0, 0, 200, 300);
		assertTrue(ViewportVisibility.intersectsView(
			view,
			new Rectangle(8, 300 + ViewportVisibility.PRELOAD_PIXELS - 1, 32, 32)));
	}

	@Test
	public void ignoresEmptyBounds()
	{
		assertFalse(ViewportVisibility.intersectsView(new Rectangle(0, 0, 200, 300), new Rectangle(0, 0, 0, 32)));
	}
}
