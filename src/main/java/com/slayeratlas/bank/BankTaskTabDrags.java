package com.slayeratlas.bank;

import net.runelite.api.gameval.InterfaceID;

public final class BankTaskTabDrags
{
	private BankTaskTabDrags()
	{
	}

	public static boolean blocksReorder(boolean tabActive, int draggedWidgetId)
	{
		return tabActive && draggedWidgetId == InterfaceID.Bankmain.ITEMS;
	}
}
