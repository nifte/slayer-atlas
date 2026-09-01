package com.slayeratlas.map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LocationMapEdgeTest
{
	@Test
	public void picksTheEdgeThePointOverflowsMost()
	{
		assertEquals(LocationMapEdge.WEST, LocationMapEdge.of(0, 20, 10, 0, 50, 40));
		assertEquals(LocationMapEdge.EAST, LocationMapEdge.of(80, 20, 10, 0, 50, 40));
		assertEquals(LocationMapEdge.SOUTH, LocationMapEdge.of(30, -20, 10, 0, 50, 40));
		assertEquals(LocationMapEdge.NORTH, LocationMapEdge.of(30, 80, 10, 0, 50, 40));
	}

	@Test
	public void prefersTheLargerOverflowOnACorner()
	{
		assertEquals(LocationMapEdge.NORTH, LocationMapEdge.of(0, 100, 10, 0, 50, 40));
		assertEquals(LocationMapEdge.WEST, LocationMapEdge.of(-100, 45, 10, 0, 50, 40));
	}

	@Test
	public void defaultsToSouthWhenTheMapIsUnavailable()
	{
		assertEquals(LocationMapEdge.SOUTH, LocationMapEdge.of(null, null));
	}
}
