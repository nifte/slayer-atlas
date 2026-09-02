package com.slayeratlas.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import org.junit.Test;

public class BankTaskTabLayoutTest
{
	@Test
	public void hidesExtraWidgetsAfterTheRealBankStacks()
	{
		assertFalse(BankTaskTabLayout.isSurplusStack(1, 1));
		assertTrue(BankTaskTabLayout.isSurplusStack(2, 1));
		assertFalse(BankTaskTabLayout.isSurplusStack(2, 2));
		assertTrue(BankTaskTabLayout.isSurplusStack(3, 2));
	}

	@Test
	public void countsNoStacksWhenTheBankIsMissing()
	{
		assertEquals(Map.of(), BankTaskTabLayout.stackCounts(null));
	}
}
