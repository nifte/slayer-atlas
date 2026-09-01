package com.slayeratlas.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LocationMapLayerTest
{
	@Test
	public void recognizesTheGielinorSurfaceName()
	{
		assertTrue(LocationMapLayer.isSurfaceName("Gielinor"));
		assertTrue(LocationMapLayer.isSurfaceName("Gielinor "));
		assertTrue(LocationMapLayer.isSurfaceName("surface"));
		assertFalse(LocationMapLayer.isSurfaceName("Kourend Underground"));
		assertFalse(LocationMapLayer.isSurfaceName("Taverley Dungeon"));
		assertFalse(LocationMapLayer.isSurfaceName(null));
		assertFalse(LocationMapLayer.isSurfaceName(""));
	}

	@Test
	public void usesTheSelectMenuOption()
	{
		assertEquals(1, LocationMapLayer.selectOp(null));
		assertEquals(1, LocationMapLayer.selectOp(new String[] {"Select"}));
		assertEquals(2, LocationMapLayer.selectOp(new String[] {"Examine", "Select"}));
	}
}
