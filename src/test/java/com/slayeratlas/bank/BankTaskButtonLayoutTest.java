package com.slayeratlas.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BankTaskButtonLayoutTest
{
	@Test
	public void matchesQuestHelpersButtonNextToBankSettings()
	{
		assertEquals(25, BankTaskButtonLayout.SIZE);
		assertEquals(408, BankTaskButtonLayout.X);
		assertEquals(5, BankTaskButtonLayout.Y);
		assertEquals(411, BankTaskButtonLayout.iconX());
		assertEquals(8, BankTaskButtonLayout.iconY());
		assertEquals(19, BankTaskButtonLayout.iconSize());
	}

	@Test
	public void showsOnlyWhenTheBankIsOpenAndATaskIsActive()
	{
		assertTrue(BankTaskButtonLayout.visible(true, true));
		assertFalse(BankTaskButtonLayout.visible(true, false));
		assertFalse(BankTaskButtonLayout.visible(false, true));
		assertFalse(BankTaskButtonLayout.visible(false, false));
	}
}
