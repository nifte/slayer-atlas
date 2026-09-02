package com.slayeratlas.bank;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BankTaskPotionItemsTest
{
	@Test
	public void keepsPotionStorageWidgetsAfterAWithdraw()
	{
		assertTrue(BankTaskPotionItems.keepStorageWidget(false, true));
		assertTrue(BankTaskPotionItems.keepStorageWidget(true, false));
		assertTrue(BankTaskPotionItems.keepStorageWidget(true, true));
		assertFalse(BankTaskPotionItems.keepStorageWidget(false, false));
	}
}
