package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PotionStorageItemsTest
{
	@Test
	public void parsesDoseAndQuantityLabels()
	{
		assertEquals(945, PotionStorageItems.parseDoses("Doses: 945"));
		assertEquals(1234, PotionStorageItems.parseDoses("Quantity: 1,234"));
		assertEquals(0, PotionStorageItems.parseDoses(""));
		assertEquals(0, PotionStorageItems.parseDoses("Doses"));
	}

	@Test
	public void skipsCaptureWhenTheClientIsMissing()
	{
		assertNull(PotionStorageItems.fromClient(null));
		assertFalse(PotionStorageItems.bankOpen(null));
		assertFalse(PotionStorageItems.storeBuilt(null));
	}
}
