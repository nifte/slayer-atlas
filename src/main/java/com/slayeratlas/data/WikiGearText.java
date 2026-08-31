package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WikiGearText
{
	private static final Pattern FILE = Pattern.compile("\\[\\[File:([^|\\]]+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern LINK_NAME = Pattern.compile("\\|link=([^|\\]]+)");
	private static final Pattern WIKI_LINK = Pattern.compile("\\[\\[([^\\]|#]+)(?:\\|[^\\]]+)?\\]\\]");
	private static final Pattern PLINK = Pattern.compile("\\{\\{plink\\|([^}|]+)", Pattern.CASE_INSENSITIVE);

	private WikiGearText()
	{
	}

	public static GearItem firstItem(String wikitext)
	{
		List<GearItem> parsed = items(wikitext);
		return parsed.isEmpty() ? null : parsed.get(0);
	}

	public static List<GearItem> items(String wikitext)
	{
		if (wikitext == null || wikitext.isEmpty())
		{
			return List.of();
		}
		List<GearItem> parsed = new ArrayList<>();
		for (String part : splitOptions(wikitext))
		{
			GearItem item = parseOne(part);
			if (item != null)
			{
				parsed.add(item);
			}
		}
		return parsed;
	}

	private static String[] splitOptions(String wikitext)
	{
		String normalized = wikitext.replace("]]/[[", "]] / [[").replace("]]/ [[", "]] / [[");
		return normalized.split("\\s+/\\s+|\\s+>\\s+|(?i)\\s+or\\s+");
	}

	private static GearItem parseOne(String wikitext)
	{
		if (wikitext == null || wikitext.isEmpty())
		{
			return null;
		}
		String name = name(wikitext);
		if (name.isEmpty() || isPlaceholder(name))
		{
			return null;
		}
		return new GearItem(name, fileName(wikitext, name));
	}

	private static String name(String wikitext)
	{
		Matcher plink = PLINK.matcher(wikitext);
		if (plink.find())
		{
			return clean(plink.group(1));
		}
		Matcher link = LINK_NAME.matcher(wikitext);
		if (link.find())
		{
			return clean(link.group(1));
		}
		Matcher wiki = WIKI_LINK.matcher(wikitext);
		while (wiki.find())
		{
			String value = wiki.group(1);
			if (!value.toLowerCase().startsWith("file:"))
			{
				return clean(value);
			}
		}
		Matcher file = FILE.matcher(wikitext);
		if (file.find())
		{
			return stripExtension(clean(file.group(1)));
		}
		return clean(wikitext);
	}

	private static String fileName(String wikitext, String name)
	{
		Matcher file = FILE.matcher(wikitext);
		if (file.find())
		{
			return clean(file.group(1));
		}
		String image = OwnedItemNames.imageName(name);
		return (image.isEmpty() ? name : image) + ".png";
	}

	private static String stripExtension(String fileName)
	{
		if (fileName.toLowerCase().endsWith(".png"))
		{
			return fileName.substring(0, fileName.length() - 4);
		}
		return fileName;
	}

	private static boolean isPlaceholder(String name)
	{
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.equals("n/a")
			|| lower.equals("none")
			|| lower.equals("-")
			|| lower.equals("na")
			|| lower.contains("<")
			|| lower.contains("{")
			|| lower.contains("[[")
			|| name.length() > 80;
	}

	private static String clean(String value)
	{
		return value.replace('_', ' ').trim();
	}
}
