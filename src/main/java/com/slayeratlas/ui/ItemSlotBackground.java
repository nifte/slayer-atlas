package com.slayeratlas.ui;

import java.awt.Color;
import net.runelite.client.ui.ColorScheme;

public final class ItemSlotBackground
{
	private static final int HALF_OPACITY = 128;

	public static final Color EMPTY = ColorScheme.DARKER_GRAY_HOVER_COLOR;
	public static final Color HELD = overlay(ColorScheme.PROGRESS_COMPLETE_COLOR.darker().darker());
	public static final Color MISSING = overlay(ColorScheme.PROGRESS_ERROR_COLOR.darker().darker());

	private ItemSlotBackground()
	{
	}

	private static Color overlay(Color accent)
	{
		int keep = 255 - HALF_OPACITY;
		return new Color(
			channel((EMPTY.getRed() * keep + accent.getRed() * HALF_OPACITY) / 255),
			channel((EMPTY.getGreen() * keep + accent.getGreen() * HALF_OPACITY) / 255),
			channel((EMPTY.getBlue() * keep + accent.getBlue() * HALF_OPACITY) / 255));
	}

	private static int channel(int value)
	{
		return Math.max(0, Math.min(255, value));
	}
}
