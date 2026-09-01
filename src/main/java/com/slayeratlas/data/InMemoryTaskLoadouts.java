package com.slayeratlas.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

final class InMemoryTaskLoadouts implements TaskLoadouts
{
	private final Map<String, GearLoadout> saved = new HashMap<>();
	private final GearLoadout current;

	InMemoryTaskLoadouts(GearLoadout current)
	{
		this.current = current;
	}

	@Override
	public GearLoadout load(String monsterId)
	{
		return monsterId == null ? null : saved.get(monsterId);
	}

	@Override
	public void save(String monsterId, GearLoadout loadout)
	{
		if (monsterId == null || monsterId.isEmpty() || loadout == null)
		{
			return;
		}
		saved.put(monsterId, loadout);
	}

	@Override
	public void clear(String monsterId)
	{
		if (monsterId != null)
		{
			saved.remove(monsterId);
		}
	}

	@Override
	public void captureCurrent(CombatStyle style, Consumer<GearLoadout> onCaptured)
	{
		if (onCaptured != null)
		{
			onCaptured.accept(current);
		}
	}
}
