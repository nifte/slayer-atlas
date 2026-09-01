package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class SpecialInventoryItemsTest
{
	@Test
	public void includesFishingExplosivesForKraken()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertTrue(names(database.findByTaskName("Cave kraken")).contains("Fishing explosive"));
		assertTrue(names(database.findNamedPage("Kraken")).contains("Fishing explosive"));
	}

	@Test
	public void includesFungicideForZygomites()
	{
		List<String> names = names(new MonsterDatabase(new Gson()).findByTaskName("Zygomites"));
		assertTrue(names.contains("Fungicide spray"));
		assertTrue(names.contains("Fungicide"));
	}

	@Test
	public void includesFinishersForOtherSlayerUniques()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertTrue(names(database.findByTaskName("Lizards")).contains("Ice cooler"));
		assertTrue(names(database.findByTaskName("Rockslugs")).contains("Bag of salt"));
		assertTrue(names(database.findByTaskName("Gargoyles")).contains("Rock hammer"));
		assertTrue(names(database.findByTaskName("Mogres")).contains("Fishing explosive"));
		assertTrue(names(database.findByTaskName("Warped creatures")).contains("Crystal chime"));
	}

	@Test
	public void ignoresNegatedRequiredItemMentions()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertFalse(names(database.findNamedPage("Sea mogre")).contains("Fishing explosive"));
		assertFalse(names(database.findNamedPage("Sulphur Lizard")).contains("Ice cooler"));
	}

	private static List<String> names(SlayerMonster monster)
	{
		java.util.ArrayList<String> names = new java.util.ArrayList<>();
		for (GearItem item : SpecialInventoryItems.forMonster(monster))
		{
			names.add(item.getName());
		}
		return names;
	}
}
