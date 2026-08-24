package com.slayerguide.ui;

import java.awt.Font;
import net.runelite.client.ui.FontManager;

public final class PanelFonts
{
	private PanelFonts()
	{
	}

	public static Font heading()
	{
		return FontManager.getRunescapeBoldFont();
	}

	public static Font body()
	{
		return FontManager.getRunescapeFont();
	}

	public static Font bodyBold()
	{
		return FontManager.getRunescapeBoldFont();
	}
}
