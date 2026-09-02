package com.slayeratlas.bank;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.gameval.InterfaceID;
import org.junit.Test;

public class BankTaskTabDragsTest
{
	@Test
	public void blocksBankItemDragsWhileTheFilterTabIsOpen()
	{
		assertTrue(BankTaskTabDrags.blocksReorder(true, InterfaceID.Bankmain.ITEMS));
		assertFalse(BankTaskTabDrags.blocksReorder(false, InterfaceID.Bankmain.ITEMS));
		assertFalse(BankTaskTabDrags.blocksReorder(true, InterfaceID.Bankmain.POTIONSTORE_ITEMS));
		assertFalse(BankTaskTabDrags.blocksReorder(true, -1));
	}

	@Test
	public void allowsDraggingWhenTheSettingIsOff()
	{
		assertFalse(BankTaskTabDrags.blocksReorder(true, InterfaceID.Bankmain.ITEMS, false));
		assertTrue(BankTaskTabDrags.blocksReorder(true, InterfaceID.Bankmain.ITEMS, true));
	}
}
