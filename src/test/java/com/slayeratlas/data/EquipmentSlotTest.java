package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class EquipmentSlotTest
{
	@Test
	public void wornSlotsMatchTheOsrsEquipmentDoll()
	{
		assertEquals(1, EquipmentSlot.HEAD.column());
		assertEquals(0, EquipmentSlot.HEAD.row());
		assertEquals(0, EquipmentSlot.WEAPON.column());
		assertEquals(2, EquipmentSlot.WEAPON.row());
		assertEquals(1, EquipmentSlot.BODY.column());
		assertEquals(2, EquipmentSlot.BODY.row());
		assertEquals(2, EquipmentSlot.SHIELD.column());
		assertEquals(2, EquipmentSlot.SHIELD.row());
		assertEquals(1, EquipmentSlot.LEGS.column());
		assertEquals(3, EquipmentSlot.LEGS.row());
		assertEquals(EquipmentSlot.BODY, EquipmentSlot.at(1, 2));
		assertNull(EquipmentSlot.at(0, 0));
		assertNull(EquipmentSlot.at(2, 3));
	}
}
