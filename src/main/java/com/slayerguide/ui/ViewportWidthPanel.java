package com.slayerguide.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import net.runelite.client.ui.ColorScheme;

public class ViewportWidthPanel extends JPanel implements Scrollable
{
	public ViewportWidthPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setAlignmentX(Component.LEFT_ALIGNMENT);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 16;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return Math.max(visibleRect.height - 16, 16);
	}

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}
}
