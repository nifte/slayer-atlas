package com.slayeratlas.bank;

public final class BankItemGrid
{
	public static final int COLUMNS = 8;
	public static final int ITEM_WIDTH = 36;
	public static final int ITEM_HEIGHT = 32;
	public static final int X_PADDING = 12;
	public static final int Y_PADDING = 4;
	public static final int START_X = 51;
	public static final int START_Y = 0;
	public static final int CELL_WIDTH = ITEM_WIDTH + X_PADDING;
	public static final int CELL_HEIGHT = ITEM_HEIGHT + Y_PADDING;

	private BankItemGrid()
	{
	}

	public static int x(int slot)
	{
		return START_X + (slot % COLUMNS) * CELL_WIDTH;
	}

	public static int y(int slot)
	{
		return START_Y + (slot / COLUMNS) * CELL_HEIGHT;
	}

	public static int scrollHeight(int itemCount)
	{
		int rows = Math.max(1, (itemCount + COLUMNS - 1) / COLUMNS);
		return START_Y + rows * CELL_HEIGHT;
	}
}
