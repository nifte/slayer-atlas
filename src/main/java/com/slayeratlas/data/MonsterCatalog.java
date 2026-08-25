package com.slayeratlas.data;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class MonsterCatalog
{
	private List<MonsterLocation> locations;
	private List<SlayerMonster> monsters;

	void normalize()
	{
		if (locations == null)
		{
			locations = Collections.emptyList();
		}
		if (monsters == null)
		{
			monsters = Collections.emptyList();
		}
		for (MonsterLocation location : locations)
		{
			location.normalize();
		}
		for (SlayerMonster monster : monsters)
		{
			monster.normalize();
		}
	}
}
