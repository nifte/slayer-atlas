package com.slayerguide.ui;

import java.awt.Point;
import javax.swing.JScrollPane;

public final class ScrollReset
{
	private ScrollReset()
	{
	}

	public static void toTop(JScrollPane scroll)
	{
		if (scroll == null)
		{
			return;
		}
		scroll.getVerticalScrollBar().setValue(0);
		scroll.getHorizontalScrollBar().setValue(0);
		scroll.getViewport().setViewPosition(new Point(0, 0));
	}
}
