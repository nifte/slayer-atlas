package com.slayeratlas.ui;

import com.slayeratlas.data.OwnedItemNames;
import com.slayeratlas.data.SlayerMonster;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class WikiImageUrl
{
	private static final String HOST = "oldschool.runescape.wiki";
	private static final String PATH_PREFIX = "/w/Special:Redirect/file/";
	private static final Pattern NUMBERED_PNG = Pattern.compile("\\s*\\(\\d+\\)\\.png$", Pattern.CASE_INSENSITIVE);
	private static final Set<String> SMALL_WORDS = Set.of("a", "an", "and", "de", "of", "or", "the");

	private WikiImageUrl()
	{
	}

	public static String fileName(SlayerMonster monster)
	{
		if (monster == null)
		{
			return "";
		}
		if (monster.getImage() != null && !monster.getImage().isEmpty())
		{
			return monster.getImage();
		}
		if (monster.getName() == null || monster.getName().isEmpty())
		{
			return "";
		}
		return monster.getName() + ".png";
	}

	public static String firstVariant(String fileName)
	{
		if (fileName == null || fileName.isEmpty())
		{
			return "";
		}
		String trimmed = fileName.trim();
		if (trimmed.length() < 5 || !trimmed.regionMatches(true, trimmed.length() - 4, ".png", 0, 4))
		{
			return "";
		}
		if (NUMBERED_PNG.matcher(trimmed).find())
		{
			return "";
		}
		return trimmed.substring(0, trimmed.length() - 4) + " (1).png";
	}

	static String unlockedFileName(String fileName)
	{
		if (fileName == null || fileName.isEmpty())
		{
			return "";
		}
		String trimmed = fileName.trim();
		boolean png = trimmed.length() >= 4
			&& trimmed.regionMatches(true, trimmed.length() - 4, ".png", 0, 4);
		String base = png ? trimmed.substring(0, trimmed.length() - 4) : trimmed;
		String unlocked = OwnedItemNames.imageName(base);
		if (unlocked.isEmpty() || unlocked.equals(base))
		{
			return "";
		}
		return png ? unlocked + trimmed.substring(base.length()) : unlocked;
	}

	public static List<String> fetchNames(String fileName)
	{
		LinkedHashSet<String> names = new LinkedHashSet<>();
		if (fileName == null || fileName.isEmpty())
		{
			return List.of();
		}
		String trimmed = fileName.trim();
		names.add(trimmed);
		String doseless = withoutDose(trimmed);
		if (!doseless.isEmpty())
		{
			names.add(doseless);
		}
		String unlocked = unlockedFileName(trimmed);
		if (!unlocked.isEmpty())
		{
			names.add(unlocked);
		}
		String numbered = firstVariant(trimmed);
		if (!numbered.isEmpty())
		{
			names.add(numbered);
		}
		String unlockedNumbered = firstVariant(unlocked);
		if (!unlockedNumbered.isEmpty())
		{
			names.add(unlockedNumbered);
		}
		for (String variant : capitalizationVariants(trimmed))
		{
			names.add(variant);
			String numberedVariant = firstVariant(variant);
			if (!numberedVariant.isEmpty())
			{
				names.add(numberedVariant);
			}
		}
		return new ArrayList<>(names);
	}

	static String withoutDose(String fileName)
	{
		if (fileName == null || fileName.isEmpty())
		{
			return "";
		}
		String trimmed = fileName.trim();
		String stripped = NUMBERED_PNG.matcher(trimmed).replaceFirst(".png");
		return stripped.equals(trimmed) ? "" : stripped;
	}

	static List<String> capitalizationVariants(String fileName)
	{
		if (fileName == null || fileName.isEmpty())
		{
			return List.of();
		}
		String trimmed = fileName.trim();
		boolean png = trimmed.length() >= 4
			&& trimmed.regionMatches(true, trimmed.length() - 4, ".png", 0, 4);
		String base = png ? trimmed.substring(0, trimmed.length() - 4) : trimmed;
		String suffix = png ? trimmed.substring(base.length()) : "";
		String[] words = base.split(" ");
		List<Integer> indices = new ArrayList<>();
		for (int index = 0; index < words.length; index++)
		{
			if (canCapitalize(words[index]))
			{
				indices.add(index);
			}
		}
		if (indices.isEmpty())
		{
			return List.of();
		}
		List<String> variants = new ArrayList<>();
		int combinations = 1 << indices.size();
		for (int mask = 1; mask < combinations; mask++)
		{
			String[] copy = words.clone();
			for (int bit = 0; bit < indices.size(); bit++)
			{
				if ((mask & (1 << bit)) != 0)
				{
					int wordIndex = indices.get(bit);
					copy[wordIndex] = capitalize(copy[wordIndex]);
				}
			}
			variants.add(String.join(" ", copy) + suffix);
		}
		return variants;
	}

	private static boolean canCapitalize(String word)
	{
		if (word == null || word.isEmpty())
		{
			return false;
		}
		char first = word.charAt(0);
		if (!Character.isLetter(first) || !Character.isLowerCase(first))
		{
			return false;
		}
		return !SMALL_WORDS.contains(word.toLowerCase(Locale.ROOT));
	}

	private static String capitalize(String word)
	{
		if (word == null || word.isEmpty())
		{
			return word;
		}
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	public static String fromFileName(String fileName)
	{
		return fromFileName(fileName, 0);
	}

	public static String fromFileName(String fileName, int width)
	{
		if (fileName == null || fileName.isEmpty())
		{
			return "";
		}
		try
		{
			String query = width > 0 ? "width=" + width : null;
			URI uri = new URI(
				"https",
				HOST,
				PATH_PREFIX + fileName.trim().replace(' ', '_'),
				query,
				null);
			return uri.toASCIIString();
		}
		catch (URISyntaxException ex)
		{
			return "";
		}
	}
}
