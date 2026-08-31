package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;

public class WikiInventoryTextTest
{
	@Test
	public void parsesAnInventoryTemplateInWikiOrder()
	{
		List<GearItem> items = WikiInventoryText.parse(
			"{{Inventory|align=right|Divine super combat potion|Shark|Prayer potion}}");
		assertEquals(3, items.size());
		assertEquals("Divine super combat potion", items.get(0).getName());
		assertEquals("Shark", items.get(1).getName());
		assertEquals("Prayer potion", items.get(2).getName());
	}

	@Test
	public void parsesLoadoutInventorySlotsAndSkipsWornGear()
	{
		List<GearItem> items = WikiInventoryText.parse(
			"{{Loadout|head={{plink|Slayer helmet (i)}}|weapon={{plink|Osmumten's fang}}"
				+ "|inventory1=Shark|inventory2={{plink|Prayer potion}}}}");
		assertEquals(2, items.size());
		assertEquals("Shark", items.get(0).getName());
		assertEquals("Prayer potion", items.get(1).getName());
	}

	@Test
	public void returnsEmptyWhenThePageHasNoInventoryTemplate()
	{
		assertTrue(WikiInventoryText.parse("Bring a herb sack and some food.").isEmpty());
	}

	@Test
	public void parsesAFullInventoryGridIncludingQuantitiesEmptiesAndDuplicates()
	{
		List<GearItem> items = WikiInventoryText.parse(
			"{{Inventory\n"
				+ "|align = right\n"
				+ "|Fishing explosive\\200|Saturated heart|Bracelet of slaughter|Bracelet of slaughter\n"
				+ "|Volatile Nightmare staff|Elidinis' ward (f)||Prayer potion\n"
				+ "|Prayer potion|Prayer potion|Prayer potion|Prayer potion\n"
				+ "|Prayer potion|Prayer potion|Prayer potion|Prayer potion\n"
				+ "|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope\n"
				+ "|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope\n"
				+ "|Cooked sunlight antelope|Cooked sunlight antelope|Cooked sunlight antelope|Divine rune pouch\n"
				+ "}}");
		assertEquals(28, items.size());
		assertEquals("Fishing explosive", items.get(0).getName());
		assertEquals("Saturated heart", items.get(1).getName());
		assertEquals("Bracelet of slaughter", items.get(2).getName());
		assertEquals("Bracelet of slaughter", items.get(3).getName());
		assertEquals("Volatile Nightmare staff", items.get(4).getName());
		assertEquals("Elidinis' ward (f)", items.get(5).getName());
		assertEquals(null, items.get(6));
		assertEquals("Prayer potion", items.get(7).getName());
		assertEquals("Divine rune pouch", items.get(27).getName());
		int prayer = 0;
		int food = 0;
		for (GearItem item : items)
		{
			if (item != null && "Prayer potion".equals(item.getName()))
			{
				prayer++;
			}
			if (item != null && "Cooked sunlight antelope".equals(item.getName()))
			{
				food++;
			}
		}
		assertEquals(9, prayer);
		assertEquals(11, food);
	}

	@Test
	public void prefersTheInventoryTemplateOverLoadoutSlots()
	{
		List<GearItem> items = WikiInventoryText.parse(
			"{{Loadout|weapon={{plink|Trident of the swamp}}|inventory1=Shark}}"
				+ "{{Inventory|align=right|Fishing explosive|Prayer potion|Prayer potion}}");
		assertEquals(3, items.size());
		assertEquals("Fishing explosive", items.get(0).getName());
		assertEquals("Prayer potion", items.get(1).getName());
		assertEquals("Prayer potion", items.get(2).getName());
	}
}
