package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class InventorySnakeTest
{
	@Test
	public void mapsTheFirstTwoRowsBackAndForth()
	{
		assertEquals(0, InventorySnake.slot(0));
		assertEquals(1, InventorySnake.slot(1));
		assertEquals(2, InventorySnake.slot(2));
		assertEquals(3, InventorySnake.slot(3));
		assertEquals(7, InventorySnake.slot(4));
		assertEquals(6, InventorySnake.slot(5));
		assertEquals(5, InventorySnake.slot(6));
		assertEquals(4, InventorySnake.slot(7));
	}

	@Test
	public void keepsEvenRowsLeftToRightAndReversesOddRows()
	{
		assertEquals(8, InventorySnake.slot(8));
		assertEquals(11, InventorySnake.slot(11));
		assertEquals(15, InventorySnake.slot(12));
		assertEquals(12, InventorySnake.slot(15));
		assertEquals(24, InventorySnake.slot(24));
		assertEquals(27, InventorySnake.slot(27));
	}

	@Test
	public void placesLogicalItemsIntoSnakeSlots()
	{
		List<GearItem> logical = new ArrayList<>();
		for (int index = 0; index < 8; index++)
		{
			logical.add(GearItem.named("Item " + index));
		}
		List<GearItem> snaked = InventorySnake.apply(logical);
		assertEquals("Item 0", snaked.get(0).getName());
		assertEquals("Item 1", snaked.get(1).getName());
		assertEquals("Item 2", snaked.get(2).getName());
		assertEquals("Item 3", snaked.get(3).getName());
		assertEquals("Item 7", snaked.get(4).getName());
		assertEquals("Item 6", snaked.get(5).getName());
		assertEquals("Item 5", snaked.get(6).getName());
		assertEquals("Item 4", snaked.get(7).getName());
	}

	@Test
	public void isItsOwnInverse()
	{
		for (int index = 0; index < InventoryLoadouts.SIZE; index++)
		{
			assertEquals(index, InventorySnake.slot(InventorySnake.slot(index)));
		}
	}

	@Test
	public void applyLeavesEmptyListsAlone()
	{
		assertEquals(List.of(), InventorySnake.apply(null));
		assertEquals(List.of(), InventorySnake.apply(List.of()));
		List<GearItem> holes = new ArrayList<>();
		holes.add(null);
		assertNull(InventorySnake.apply(holes).get(0));
	}
}
