package com.slayeratlas.bank;

public final class BankTaskButtonLayout
{
	// Same slot Quest Helper uses, immediately left of the bank settings/equipment buttons.
	public static final int SIZE = 25;
	public static final int X = 408;
	public static final int Y = 5;
	public static final int ICON_INSET = 3;

	private BankTaskButtonLayout()
	{
	}

	public static int iconX()
	{
		return X + ICON_INSET;
	}

	public static int iconY()
	{
		return Y + ICON_INSET;
	}

	public static int iconSize()
	{
		return SIZE - ICON_INSET * 2;
	}

	public static boolean visible(boolean bankOpen, boolean hasTask)
	{
		return visible(bankOpen, hasTask, true);
	}

	public static boolean visible(boolean bankOpen, boolean hasTask, boolean settingEnabled)
	{
		return bankOpen && hasTask && settingEnabled;
	}

	public static boolean showButton(boolean hasTask, boolean settingEnabled)
	{
		return hasTask && settingEnabled;
	}
}
