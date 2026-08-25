package com.slayeratlas.data;

public final class AlternativeMonsters
{
	private AlternativeMonsters()
	{
	}

	public static String lookupName(String alternative)
	{
		if (alternative == null)
		{
			return "";
		}
		String text = alternative.trim();
		int paren = text.indexOf('(');
		if (paren > 0)
		{
			text = text.substring(0, paren).trim();
		}
		return text;
	}

	public static String imageFile(String alternative)
	{
		String name = lookupName(alternative);
		return name.isEmpty() ? "" : name + ".png";
	}

	public static String slug(String alternative)
	{
		return TaskMatcher.normalize(lookupName(alternative)).replace(' ', '_');
	}

	public static SlayerMonster resolve(MonsterDatabase database, String alternative, SlayerMonster current)
	{
		SlayerMonster found = find(database, alternative, current);
		if (found != null)
		{
			return found;
		}
		SlayerMonster created = SlayerMonster.forAlternative(lookupName(alternative));
		if (created != null)
		{
			created.inheritTaskContext(current);
		}
		return created;
	}

	public static SlayerMonster find(MonsterDatabase database, String alternative, SlayerMonster current)
	{
		if (database == null)
		{
			return null;
		}
		String name = lookupName(alternative);
		if (name.isEmpty())
		{
			return null;
		}
		SlayerMonster named = database.findNamedPage(name);
		if (named != null && !sameMonster(current, named))
		{
			return named;
		}
		for (SlayerMonster monster : database.getMonsters())
		{
			if (!TaskMatcher.matchesMonster(name, monster) || sameMonster(current, monster))
			{
				continue;
			}
			return monster;
		}
		return null;
	}

	private static boolean sameMonster(SlayerMonster current, SlayerMonster candidate)
	{
		return current != null && candidate != null && current.getId().equals(candidate.getId());
	}
}
