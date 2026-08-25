package com.slayeratlas.data;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class MonsterLocation
{
	private String id;
	private String name;
	private String region;
	private int x;
	private int y;
	private int plane;
	private boolean wilderness;
	private List<String> travel;

	void normalize()
	{
		if (travel == null)
		{
			travel = Collections.emptyList();
		}
	}
}
