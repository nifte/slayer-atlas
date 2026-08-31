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

	public static List<String> inventoryPages(SlayerMonster monster)
	{
		List<String> all = candidates(monster);
		List<String> ordered = new ArrayList<>();
		addMatching(ordered, all, name -> name.toLowerCase(Locale.ROOT).endsWith("/strategies"));
		addMatching(ordered, all, name -> name.toLowerCase(Locale.ROOT).startsWith("slayer task/"));
		addMatching(ordered, all, name -> true);
		if (ordered.size() > 6)
		{
			return new ArrayList<>(ordered.subList(0, 6));
		}
		return ordered;
	}

	private static void addMatching(List<String> ordered, List<String> all, java.util.function.Predicate<String> match)
	{
		for (String name : all)
		{
			if (name != null && match.test(name) && !containsIgnoreCase(ordered, name))
			{
				ordered.add(name);
			}
		}
	}

	private static boolean containsIgnoreCase(List<String> names, String name)
	{
		for (String existing : names)
		{
			if (existing.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}

	public static List<String> candidates(SlayerMonster monster)
	{
		LinkedHashSet<String> names = new LinkedHashSet<>();
		if (monster == null)
		{
			return new ArrayList<>();
		}
		addNameVariants(names, monster.getName());
		if (monster.getAliases() != null)
		{
			for (String alias : monster.getAliases())
			{
				if (!namesAnAlternative(monster, alias))
				{
					addNameVariants(names, alias);
				}
			}
		}
		addFromWikiUrl(names, monster.getWiki());
		return new ArrayList<>(names);
	}

	public static boolean matches(String pageName, SlayerMonster monster)
	{
		if (monster == null || !matches(pageName, candidates(monster)))
		{
			return false;
		}
		return !belongsToAlternative(pageName, monster);
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

	private static boolean belongsToAlternative(String pageName, SlayerMonster monster)
	{
		if (monster.getAlternatives() == null)
		{
			return false;
		}
		String title = pageTitle(pageName);
		if (title.isEmpty() || TaskMatcher.namesMatch(title, monster.getName()))
		{
			return false;
		}
		for (String alternative : monster.getAlternatives())
		{
			String name = AlternativeMonsters.lookupName(alternative);
			if (name.isEmpty() || TaskMatcher.namesMatch(name, monster.getName()))
			{
				continue;
			}
			if (TaskMatcher.namesMatch(title, name) || initials(name).equals(TaskMatcher.normalize(title)))
			{
				return true;
			}
			LinkedHashSet<String> pages = new LinkedHashSet<>();
			addNameVariants(pages, name);
			if (matches(pageName, new ArrayList<>(pages)))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean namesAnAlternative(SlayerMonster monster, String alias)
	{
		if (monster.getAlternatives() == null || alias == null || alias.isEmpty())
		{
			return false;
		}
		for (String alternative : monster.getAlternatives())
		{
			String name = AlternativeMonsters.lookupName(alternative);
			if (name.isEmpty())
			{
				continue;
			}
			if (TaskMatcher.namesMatch(alias, name) || initials(name).equals(TaskMatcher.normalize(alias)))
			{
				return true;
			}
		}
		return false;
	}

	private static String initials(String name)
	{
		String normalized = TaskMatcher.normalize(name);
		if (normalized.isEmpty() || !normalized.contains(" "))
		{
			return "";
		}
		StringBuilder letters = new StringBuilder();
		for (String word : normalized.split(" "))
		{
			if (!word.isEmpty())
			{
				letters.append(word.charAt(0));
			}
		}
		return letters.length() > 1 ? letters.toString() : "";
	}

	private static String pageTitle(String pageName)
	{
		String page = normalize(pageName);
		if (page.startsWith("slayer task/"))
		{
			page = page.substring("slayer task/".length());
		}
		int slash = page.indexOf('/');
		return slash < 0 ? page : page.substring(0, slash);
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
