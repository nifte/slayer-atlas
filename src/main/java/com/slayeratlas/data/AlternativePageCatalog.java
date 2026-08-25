package com.slayeratlas.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class AlternativePageCatalog
{
	private List<MonsterLocation> locations;
	private Map<String, AlternativeOverride> overrides;

	void normalize()
	{
		if (locations == null)
		{
			locations = Collections.emptyList();
		}
		if (overrides == null)
		{
			overrides = Collections.emptyMap();
		}
		for (MonsterLocation location : locations)
		{
			location.normalize();
		}
	}
}
