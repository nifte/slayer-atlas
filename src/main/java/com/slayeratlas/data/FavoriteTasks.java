package com.slayeratlas.data;

import com.google.inject.ImplementedBy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ImplementedBy(ConfigFavoriteTasks.class)
public interface FavoriteTasks
{
	boolean contains(String monsterId);

	void set(String monsterId, boolean favorite);

	static FavoriteTasks none()
	{
		return memory();
	}

	static FavoriteTasks memory()
	{
		return new InMemoryFavoriteTasks();
	}

	static List<SlayerMonster> pinToTop(List<SlayerMonster> monsters, FavoriteTasks favorites)
	{
		if (monsters == null || monsters.isEmpty())
		{
			return monsters == null ? List.of() : List.copyOf(monsters);
		}
		List<SlayerMonster> ordered = new ArrayList<>(monsters);
		if (favorites != null)
		{
			ordered.sort(Comparator.comparing((SlayerMonster monster) -> !favorites.contains(monster.getId())));
		}
		return ordered;
	}
}
