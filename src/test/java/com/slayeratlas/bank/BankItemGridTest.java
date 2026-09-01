package com.slayeratlas.bank;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BankItemGridTest
{
	@Test
	public void packsEightItemsPerRow()
	{
		assertEquals(51, BankItemGrid.x(0));
		assertEquals(51 + 48, BankItemGrid.x(1));
		assertEquals(0, BankItemGrid.y(0));
		assertEquals(36, BankItemGrid.y(8));
		assertEquals(36, BankItemGrid.scrollHeight(1));
		assertEquals(72, BankItemGrid.scrollHeight(9));
	}
}
