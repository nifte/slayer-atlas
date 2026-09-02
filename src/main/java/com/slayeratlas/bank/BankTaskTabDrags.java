package com.slayeratlas.bank;

import net.runelite.api.gameval.InterfaceID;

public final class BankTaskTabDrags
{
	private BankTaskTabDrags()
	{
	}

	public static boolean blocksReorder(boolean tabActive, int draggedWidgetId)
	{
		return blocksReorder(tabActive, draggedWidgetId, true);
	}

	public static boolean blocksReorder(boolean tabActive, int draggedWidgetId, boolean preventDrags)
	{
		return preventDrags && tabActive && draggedWidgetId == InterfaceID.Bankmain.ITEMS;
	}
}
