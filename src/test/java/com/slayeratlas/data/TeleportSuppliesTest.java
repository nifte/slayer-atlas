package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TeleportSuppliesTest
{
	@Test
	public void treatsHouseTabletsAndSkillcapesAsTeleports()
	{
		assertTrue(TeleportSupplies.isTeleport("Teleport to house"));
		assertTrue(TeleportSupplies.isTeleport("Teleport to house (tablet)"));
		assertTrue(TeleportSupplies.isTeleport("Construction cape (t)"));
		assertTrue(TeleportSupplies.isTeleport("Crafting cape"));
		assertTrue(TeleportSupplies.isTeleport("Sailor's amulet"));
		assertTrue(TeleportSupplies.isTeleport("Sailors amulet"));
		assertTrue(TeleportSupplies.isTeleport("Sailor’s amulet"));
		assertTrue(TeleportSupplies.isTeleport("Amulet of the sailor"));
		assertTrue(TeleportSupplies.isTeleport("Varrock teleport"));
		assertFalse(TeleportSupplies.isTeleport("Kharyrll teleport"));
		assertFalse(TeleportSupplies.isTeleport("Rune pouch"));
		assertFalse(TeleportSupplies.isTeleport("Slayer ring"));
	}

	@Test
	public void doesNotTreatCombinedMaxCapesAsTheHouseTeleportCape()
	{
		assertFalse(TeleportSupplies.isTeleport("Fire max cape"));
		assertFalse(TeleportSupplies.isTeleport("Infernal max cape"));
		assertTrue(TeleportSupplies.isTeleport("Max cape"));
		assertTrue(TeleportSupplies.isTeleport("Max cape (l)"));
	}

	@Test
	public void stripsTeleportsFromACompactInventory()
	{
		List<GearItem> items = new ArrayList<>();
		items.add(GearItem.named("Shark"));
		items.add(GearItem.named("Teleport to house (tablet)"));
		items.add(GearItem.named("Construction cape (t)"));
		items.add(GearItem.named("Rune pouch"));
		TeleportSupplies.strip(items, false);
		assertEquals(2, items.size());
		assertEquals("Shark", items.get(0).getName());
		assertEquals("Rune pouch", items.get(1).getName());
	}

	@Test
	public void nullsTeleportSlotsWhenPreservingAWikiGrid()
	{
		List<GearItem> items = new ArrayList<>();
		items.add(GearItem.named("Shark"));
		items.add(new GearItem("Teleport to House", "Teleport to House.png"));
		items.add(GearItem.named("Sailor's amulet"));
		items.add(GearItem.named("Rune pouch"));
		TeleportSupplies.strip(items, true);
		assertEquals(4, items.size());
		assertEquals("Shark", items.get(0).getName());
		assertNull(items.get(1));
		assertNull(items.get(2));
		assertEquals("Rune pouch", items.get(3).getName());
	}
}
