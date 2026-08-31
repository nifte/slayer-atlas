package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
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
	public void readsLinkEqualsNamesFromFileMarkup()
	{
		GearItem item = WikiGearText.firstItem(
			"<span class=\"plink-template\">[[File:Imbued Guthix cape.png|link=Imbued god cape]]</span>"
				+ "[[Imbued god cape|Imbued god cape]]");
		assertEquals("Imbued god cape", item.getName());
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
	public void splitsEveryOptionInARankedCell()
	{
		List<GearItem> items = WikiGearText.items(
			" [[Trident of the swamp]] / [[Trident of the seas]] > [[Sanguinesti staff]]"
				+ " or [[Tumeken's shadow]]");
		assertEquals(4, items.size());
		assertEquals("Trident of the swamp", items.get(0).getName());
		assertEquals("Trident of the seas", items.get(1).getName());
		assertEquals("Sanguinesti staff", items.get(2).getName());
		assertEquals("Tumeken's shadow", items.get(3).getName());
	}

	@Test
	public void splitsUnspacedWikiLinks()
	{
		List<GearItem> items = WikiGearText.items("[[Trident of the swamp]]/[[Trident of the seas]]");
		assertEquals(2, items.size());
		assertEquals("Trident of the swamp", items.get(0).getName());
		assertEquals("Trident of the seas", items.get(1).getName());
	}

	@Test
	public void emptyWhenMissing()
	{
		assertNull(WikiGearText.firstItem(null));
		assertNull(WikiGearText.firstItem(""));
		assertNull(WikiGearText.firstItem("N/A"));
		assertNull(WikiGearText.firstItem("<div class=\"no-item\"></div>"));
	}

	@Test
	public void readsPlinkTemplates()
	{
		GearItem item = WikiGearText.firstItem("{{plink|Osmumten's fang}}");
		assertEquals("Osmumten's fang", item.getName());
		assertTrue(item.getImageFile().contains("Osmumten's fang"));
	}
}
