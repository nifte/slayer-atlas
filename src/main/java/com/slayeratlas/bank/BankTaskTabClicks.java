package com.slayeratlas.bank;

import java.util.Locale;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.util.Text;

public final class BankTaskTabClicks
{
	static final int POTION_STORE_TAB = 15;

	private BankTaskTabClicks()
	{
	}

	public static boolean isPotionStoreTab(int tab)
	{
		return tab == POTION_STORE_TAB;
	}

	public static boolean closesLoadoutTab(String menuOption, String menuTarget, int widgetId)
	{
		if (widgetId == InterfaceID.Bankmain.POTIONSTORE_BUTTON)
		{
			return true;
		}
		if (menuOption == null)
		{
			return false;
		}
		if (isPotionStoreOption(menuOption))
		{
			return true;
		}
		if (menuOption.equals("View all items") || menuOption.startsWith("View tag tab"))
		{
			return true;
		}
		return menuOption.startsWith("View tab")
			&& !BankTaskTabInterface.TAB_NAME.equals(Text.removeTags(emptyIfNull(menuTarget)));
	}

	static boolean isPotionStoreOption(String menuOption)
	{
		String option = menuOption.toLowerCase(Locale.ROOT);
		return option.startsWith("potion store") || option.startsWith("potion storage");
	}

	private static String emptyIfNull(String value)
	{
		return value == null ? "" : value;
	}
}
