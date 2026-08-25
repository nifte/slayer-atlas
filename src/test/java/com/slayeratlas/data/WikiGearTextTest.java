package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WikiGearTextTest
{
	@Test
	public void readsNameAndFileFromPlinkStyleCell()
	{
		GearItem item = WikiGearText.firstItem(
			" [[File:Slayer helmet (i).png|link=Slayer helmet (i)]] [[Slayer helmet (i)|Slayer helmet (i)]]");
		assertEquals("Slayer helmet (i)", item.getName());
		assertEquals("Slayer helmet (i).png", item.getImageFile());
	}

	@Test
	public void takesTheFirstOptionWhenCellListsAlternatives()
	{
		GearItem item = WikiGearText.firstItem(
			" [[File:Dragonfire ward.png|link=Dragonfire ward]] [[Dragonfire ward|Dragonfire ward]]"
				+ " / [[File:Twisted buckler.png|link=Twisted buckler]] [[Twisted buckler|Twisted buckler]]");
		assertEquals("Dragonfire ward", item.getName());
		assertEquals("Dragonfire ward.png", item.getImageFile());
	}

	@Test
	public void emptyWhenMissing()
	{
		assertNull(WikiGearText.firstItem(null));
		assertNull(WikiGearText.firstItem(""));
		assertNull(WikiGearText.firstItem("N/A"));
	}

	@Test
	public void readsPlinkTemplates()
	{
		GearItem item = WikiGearText.firstItem("{{plink|Osmumten's fang}}");
		assertEquals("Osmumten's fang", item.getName());
		assertTrue(item.getImageFile().contains("Osmumten's fang"));
	}
}
