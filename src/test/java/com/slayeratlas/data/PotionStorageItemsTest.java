package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import net.runelite.api.gameval.VarPlayerID;
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
		assertNull(PotionStorageItems.slots(null));
		assertFalse(PotionStorageItems.bankOpen(null));
		assertFalse(PotionStorageItems.storeBuilt(null));
	}

	@Test
	public void mapsPotionOrdinalsToStoreWidgetIndexes()
	{
		assertEquals(0, PotionStorageItems.storeIndex(0));
		assertEquals(5, PotionStorageItems.storeIndex(1));
		assertEquals(15, PotionStorageItems.storeIndex(3));
		assertEquals(5, PotionStorageItems.storeIndexOf(List.of(new PotionStorageSlot(12, 4, 5)), 12));
		assertEquals(-1, PotionStorageItems.storeIndexOf(List.of(), 12));
	}

	@Test
	public void tracksPotionStoreQuantityVarps()
	{
		assertTrue(PotionStorageItems.tracksVarp(VarPlayerID.POTIONSTORE_BASE_VAR_1));
		assertTrue(PotionStorageItems.tracksVarp(VarPlayerID.POTIONSTORE_VIALS));
		assertFalse(PotionStorageItems.tracksVarp(VarPlayerID.SLAYER_COUNT));
	}

	@Test
	public void usesWidgetReadsWhenTheStoreIsAlreadyBuilt()
	{
		assertTrue(PotionStorageItems.useWidgets(List.of(), true));
		assertTrue(PotionStorageItems.useWidgets(List.of(12), false));
		assertFalse(PotionStorageItems.useWidgets(List.of(), false));
		assertFalse(PotionStorageItems.useWidgets(null, true));
		assertFalse(PotionStorageItems.useWidgets(null, false));
	}

	@Test
	public void treatsQuantityChangesAsADifferentSlot()
	{
		PotionStorageSlot twelve = new PotionStorageSlot(12, 4, 5);
		PotionStorageSlot three = new PotionStorageSlot(12, 3, 5);
		assertEquals(twelve, new PotionStorageSlot(12, 4, 5));
		assertFalse(twelve.equals(three));
	}
}
