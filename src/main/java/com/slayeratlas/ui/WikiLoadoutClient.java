package com.slayeratlas.ui;

import com.google.inject.ImplementedBy;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.RankedGearLoadout;
import com.slayeratlas.data.RankedLoadouts;
import com.slayeratlas.data.SlayerMonster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@ImplementedBy(WikiBucketLoadoutClient.class)
public interface WikiLoadoutClient
{
	void load(SlayerMonster monster, Consumer<List<GearLoadout>> onLoaded);

	default void loadRanked(SlayerMonster monster, Consumer<List<RankedGearLoadout>> onLoaded)
	{
		load(monster, loadouts ->
		{
			List<RankedGearLoadout> ranked = new ArrayList<>();
			if (loadouts != null)
			{
				for (GearLoadout loadout : loadouts)
				{
					RankedGearLoadout converted = RankedLoadouts.fromLoadout(loadout);
					if (converted != null)
					{
						ranked.add(converted);
					}
				}
			}
			onLoaded.accept(ranked);
		});
	}

	static WikiLoadoutClient none()
	{
		return (monster, onLoaded) -> onLoaded.accept(Collections.emptyList());
	}
}
