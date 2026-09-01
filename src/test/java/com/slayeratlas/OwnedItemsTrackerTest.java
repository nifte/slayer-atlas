package com.slayeratlas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.gameval.VarPlayerID;
import org.junit.Test;

public class OwnedItemsTrackerTest
{
	@Test
	public void tracksPotionStoreVarps()
	{
		assertTrue(OwnedItemsTracker.tracksPotionStore(VarPlayerID.POTIONSTORE_BASE_VAR_1));
		assertTrue(OwnedItemsTracker.tracksPotionStore(VarPlayerID.POTIONSTORE_VIALS));
		assertFalse(OwnedItemsTracker.tracksPotionStore(VarPlayerID.SLAYER_COUNT));
	}
}
