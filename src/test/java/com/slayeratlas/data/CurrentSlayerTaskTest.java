package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CurrentSlayerTaskTest
{
	@Test
	public void sameAssignmentIgnoresRemainingCount()
	{
		CurrentSlayerTask first = new CurrentSlayerTask("Skeletal Wyverns", "Asgarnia", 31, 31);
		CurrentSlayerTask afterKill = new CurrentSlayerTask("Skeletal Wyverns", "Asgarnia", 30, 31);
		assertTrue(first.sameAssignment(afterKill));
		assertFalse(first.equals(afterKill));
	}

	@Test
	public void differentNameOrLocationIsANewAssignment()
	{
		CurrentSlayerTask wyverns = new CurrentSlayerTask("Skeletal Wyverns", "Asgarnia", 31, 31);
		assertFalse(wyverns.sameAssignment(new CurrentSlayerTask("Abyssal demons", "Asgarnia", 31, 31)));
		assertFalse(wyverns.sameAssignment(new CurrentSlayerTask("Skeletal Wyverns", "Kourend", 31, 31)));
		assertFalse(wyverns.sameAssignment(null));
	}
}
