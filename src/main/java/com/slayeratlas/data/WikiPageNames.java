package com.slayeratlas.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public final class WikiPageNames
{
	private WikiPageNames()
	{
	}

	public static List<String> candidates(SlayerMonster monster)
	{
		LinkedHashSet<String> names = new LinkedHashSet<>();
		if (monster == null)
		{
			return new ArrayList<>();
		}
		addNameVariants(names, monster.getName());
		addFromWikiUrl(names, monster.getWiki());
		return new ArrayList<>(names);
	}

	public static boolean matches(String pageName, List<String> candidates)
	{
		String page = normalize(pageName);
		if (page.isEmpty())
		{
			return false;
		}
		for (String candidate : candidates)
		{
			String needle = normalize(candidate);
			if (needle.isEmpty())
			{
				continue;
			}
			if (page.equals(needle) || page.startsWith(needle + "/"))
			{
				return true;
			}
		}
		return false;
	}

	private static void addFromWikiUrl(LinkedHashSet<String> names, String wiki)
	{
		if (wiki == null || wiki.isEmpty())
		{
			return;
		}
		try
		{
			String path = URI.create(wiki).getPath();
			if (path == null || !path.contains("/w/"))
			{
				addNameVariants(names, wiki.replace('_', ' '));
				return;
			}
			String title = path.substring(path.indexOf("/w/") + 3).replace('_', ' ');
			addNameVariants(names, title);
		}
		catch (IllegalArgumentException ignored)
		{
			addNameVariants(names, wiki.replace('_', ' '));
		}
	}

	private static void addNameVariants(LinkedHashSet<String> names, String raw)
	{
		String title = normalize(raw);
		if (title.isEmpty())
		{
			return;
		}
		addTitle(names, title);
		if (title.toLowerCase(Locale.ROOT).startsWith("slayer task/"))
		{
			addTitle(names, title.substring("slayer task/".length()));
		}
	}

	private static void addTitle(LinkedHashSet<String> names, String title)
	{
		if (title.isEmpty())
		{
			return;
		}
		names.add(title);
		if (!title.toLowerCase(Locale.ROOT).endsWith("/strategies"))
		{
			names.add(title + "/Strategies");
		}
		String singular = singularize(title);
		if (!singular.equalsIgnoreCase(title))
		{
			names.add(singular);
			if (!singular.toLowerCase(Locale.ROOT).endsWith("/strategies"))
			{
				names.add(singular + "/Strategies");
			}
		}
		if (!title.toLowerCase(Locale.ROOT).startsWith("slayer task/"))
		{
			names.add("Slayer task/" + title);
			if (!singular.equalsIgnoreCase(title))
			{
				names.add("Slayer task/" + singular);
			}
		}
	}

	private static String singularize(String title)
	{
		int slash = title.lastIndexOf('/');
		if (slash >= 0)
		{
			return title.substring(0, slash + 1) + TaskMatcher.singularize(normalize(title.substring(slash + 1)));
		}
		return TaskMatcher.singularize(title.toLowerCase(Locale.ROOT));
	}

	private static String normalize(String value)
	{
		if (value == null)
		{
			return "";
		}
		return value.replace('_', ' ').trim().toLowerCase(Locale.ROOT);
	}
}
