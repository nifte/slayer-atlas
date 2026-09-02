package com.slayeratlas.bank;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import org.junit.Test;

public class BankCopiedClicksTest
{
	@Test
	public void sendsCopiedSlotsToTheRealBankWidget()
	{
		Map<Integer, Integer> copies = Map.of(18, 3, 19, 3);
		assertEquals(3, BankCopiedClicks.param0(18, copies));
		assertEquals(3, BankCopiedClicks.param0(19, copies));
		assertEquals(3, BankCopiedClicks.param0(3, copies));
		assertEquals(7, BankCopiedClicks.param0(7, copies));
	}

	@Test
	public void leavesClicksAloneWhenNothingWasCopied()
	{
		assertEquals(18, BankCopiedClicks.param0(18, Map.of()));
		assertEquals(18, BankCopiedClicks.param0(18, null));
	}
}
