package com.slayeratlas.ui;

import java.awt.Color;
import net.runelite.client.ui.ColorScheme;

public final class ItemSlotBackground
{
	public static final Color EMPTY = ColorScheme.DARKER_GRAY_HOVER_COLOR;
	public static final Color HELD = ColorScheme.PROGRESS_COMPLETE_COLOR.darker().darker().darker().darker();
	public static final Color MISSING = ColorScheme.PROGRESS_ERROR_COLOR.darker().darker();

	private ItemSlotBackground()
	{
	}
}
