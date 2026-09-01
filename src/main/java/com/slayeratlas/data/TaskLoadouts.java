package com.slayeratlas.data;

import com.google.inject.ImplementedBy;
import java.util.function.Consumer;

@ImplementedBy(SavedTaskLoadouts.class)
public interface TaskLoadouts
{
	GearLoadout load(String monsterId);

	void save(String monsterId, GearLoadout loadout);

	void clear(String monsterId);

	void captureCurrent(CombatStyle style, Consumer<GearLoadout> onCaptured);

	static TaskLoadouts none()
	{
		return memory(null);
	}

	static TaskLoadouts memory(GearLoadout current)
	{
		return new InMemoryTaskLoadouts(current);
	}
}
