package com.slayeratlas.data;

import java.util.LinkedHashSet;
import java.util.Set;

final class InMemoryFavoriteTasks implements FavoriteTasks
{
	private final Set<String> ids = new LinkedHashSet<>();

	@Override
	public boolean contains(String monsterId)
	{
		return monsterId != null && ids.contains(monsterId);
	}

	@Override
	public void set(String monsterId, boolean favorite)
	{
		if (monsterId == null || monsterId.isEmpty())
		{
			return;
		}
		if (favorite)
		{
			ids.add(monsterId);
		}
		else
		{
			ids.remove(monsterId);
		}
	}
}
