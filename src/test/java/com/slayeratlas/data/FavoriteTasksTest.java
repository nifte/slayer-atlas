package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import org.junit.Test;

public class FavoriteTasksTest
{
	@Test
	public void parseIgnoresBlanksAndDuplicates()
	{
		assertEquals(List.of(), FavoriteTaskIds.parse(null));
		assertEquals(List.of(), FavoriteTaskIds.parse(""));
		assertEquals(List.of("dust_devils", "gargoyles"), FavoriteTaskIds.parse(" dust_devils,gargoyles, dust_devils , "));
	}

	@Test
	public void serializeSkipsEmptyIds()
	{
		assertEquals("", FavoriteTaskIds.serialize(null));
		assertEquals("", FavoriteTaskIds.serialize(List.of()));
		assertEquals("dust_devils,gargoyles", FavoriteTaskIds.serialize(List.of("dust_devils", " ", "gargoyles")));
	}

	@Test
	public void memoryStoreTogglesFavorites()
	{
		FavoriteTasks favorites = FavoriteTasks.memory();
		assertFalse(favorites.contains("dust_devils"));
		favorites.set("dust_devils", true);
		assertTrue(favorites.contains("dust_devils"));
		favorites.set("dust_devils", false);
		assertFalse(favorites.contains("dust_devils"));
	}

	@Test
	public void pinsFavoritesFirstAndKeepsRelativeOrder()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster first = database.getMonsters().get(0);
		SlayerMonster later = database.findByTaskName("Dust devils");
		FavoriteTasks favorites = FavoriteTasks.memory();
		favorites.set(later.getId(), true);

		List<SlayerMonster> ordered = FavoriteTasks.pinToTop(database.getMonsters(), favorites);
		assertEquals(later.getId(), ordered.get(0).getId());
		assertEquals(first.getId(), ordered.get(1).getId());
	}

	@Test
	public void searchMatchesKeepRankAmongUnpinnedTasks()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		List<SlayerMonster> dragons = database.search("dragon");
		assertTrue(dragons.size() > 2);
		FavoriteTasks favorites = FavoriteTasks.memory();
		favorites.set(dragons.get(2).getId(), true);

		List<SlayerMonster> ordered = FavoriteTasks.pinToTop(dragons, favorites);
		assertEquals(dragons.get(2).getId(), ordered.get(0).getId());
		assertEquals(dragons.get(0).getId(), ordered.get(1).getId());
		assertEquals(dragons.get(1).getId(), ordered.get(2).getId());
	}
}
