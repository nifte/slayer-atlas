package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FavoriteTaskIds
{
	private FavoriteTaskIds()
	{
	}

	public static List<String> parse(String stored)
	{
		if (stored == null || stored.isEmpty())
		{
			return List.of();
		}
		List<String> ids = new ArrayList<>();
		for (String part : stored.split(","))
		{
			String id = part.trim();
			if (!id.isEmpty() && !ids.contains(id))
			{
				ids.add(id);
			}
		}
		return ids;
	}

	public static String serialize(Collection<String> ids)
	{
		if (ids == null || ids.isEmpty())
		{
			return "";
		}
		StringBuilder stored = new StringBuilder();
		for (String id : ids)
		{
			if (id == null)
			{
				continue;
			}
			String trimmed = id.trim();
			if (trimmed.isEmpty())
			{
				continue;
			}
			if (stored.length() > 0)
			{
				stored.append(',');
			}
			stored.append(trimmed);
		}
		return stored.toString();
	}
}
