package com.slayeratlas.bank;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.gameval.InterfaceID;
import org.junit.Test;

public class BankTaskTabClicksTest
{
	@Test
	public void closesWhenThePotionStoreButtonIsClicked()
	{
		assertTrue(BankTaskTabClicks.closesLoadoutTab("Open", "", InterfaceID.Bankmain.POTIONSTORE_BUTTON));
		assertTrue(BankTaskTabClicks.closesLoadoutTab("Potion store", "", 0));
		assertTrue(BankTaskTabClicks.closesLoadoutTab("Potion storage", "", 0));
		assertTrue(BankTaskTabClicks.isPotionStoreTab(15));
		assertFalse(BankTaskTabClicks.isPotionStoreTab(0));
	}

	@Test
	public void closesWhenAnotherBankTabIsClicked()
	{
		assertTrue(BankTaskTabClicks.closesLoadoutTab("View all items", "", 0));
		assertTrue(BankTaskTabClicks.closesLoadoutTab("View tab 2", "Tab 2", 0));
		assertTrue(BankTaskTabClicks.closesLoadoutTab("View tag tab", "Melee", 0));
		assertFalse(BankTaskTabClicks.closesLoadoutTab("View tab ", "slayer-atlas", 0));
		assertFalse(BankTaskTabClicks.closesLoadoutTab("Withdraw-1", "Shark", 0));
	}
}
