package com.slayeratlas.bank;

import java.util.Map;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;

public final class BankCopiedClicks
{
	private BankCopiedClicks()
	{
	}

	public static int param0(int clickedIndex, Map<Integer, Integer> copies)
	{
		if (copies == null)
		{
			return clickedIndex;
		}
		return copies.getOrDefault(clickedIndex, clickedIndex);
	}

	public static void remap(MenuOptionClicked event, Map<Integer, Integer> copies)
	{
		if (event == null || copies == null || copies.isEmpty()
			|| event.getParam1() != InterfaceID.Bankmain.ITEMS)
		{
			return;
		}
		int source = param0(event.getParam0(), copies);
		if (source != event.getParam0())
		{
			event.getMenuEntry().setParam0(source);
		}
	}
}
