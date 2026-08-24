package com.slayerguide.ui;

import java.awt.Rectangle;

public final class ViewportVisibility
{
	public static final int PRELOAD_PIXELS = 48;

	private ViewportVisibility()
	{
	}

	public static boolean intersectsView(Rectangle viewRect, Rectangle componentBounds)
	{
		if (viewRect == null || componentBounds == null)
		{
			return false;
		}
		if (componentBounds.width <= 0 || componentBounds.height <= 0)
		{
			return false;
		}
		Rectangle expanded = new Rectangle(viewRect);
		expanded.grow(0, PRELOAD_PIXELS);
		return expanded.intersects(componentBounds);
	}
}
