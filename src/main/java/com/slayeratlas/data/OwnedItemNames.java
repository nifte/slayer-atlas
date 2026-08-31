package com.slayeratlas.data;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OwnedItemNames
{
	private static final Pattern CHARGES = Pattern.compile("\\s*\\(\\d+\\)\\s*$");
	private static final Pattern IMBUED = Pattern.compile("\\s*\\((i|imbued)\\)\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern TRAILING_TAG = Pattern.compile("\\s*\\(([^)]+)\\)\\s*$");
	private static final Set<String> LOCK_TAGS = Set.of("l", "locked", "broken", "mangled");
	private static final Set<String> COSMETIC_TAGS = Set.of(
		"deadman",
		"or",
		"o",
		"empty",
		"uncharged",
		"c",
		"g",
		"t",
		"hallowed",
		"trailblazer",
		"guthix",
		"saradomin",
		"zamorak",
		"ithell",
		"iorwerth",
		"trahaearn",
		"cadarn",
		"crwys",
		"meilyr",
		"hefin",
		"amlodd");
	private static final Pattern IMBUED_GOD_CAPE = Pattern.compile(
		"^imbued (guthix|saradomin|zamorak) cape$");
	private static final Map<String, String> ORNAMENTS = Map.ofEntries(
		Map.entry("blazing blowpipe", "toxic blowpipe"),
		Map.entry("dinh's blazing bulwark", "dinh's bulwark"),
		Map.entry("volcanic abyssal whip", "abyssal whip"),
		Map.entry("frozen abyssal whip", "abyssal whip"),
		Map.entry("echo venator bow", "venator bow"),
		Map.entry("echo ahrim's hood", "ahrim's hood"),
		Map.entry("echo ahrim's robetop", "ahrim's robetop"),
		Map.entry("echo ahrim's robeskirt", "ahrim's robeskirt"),
		Map.entry("echo ahrim's staff", "ahrim's staff"),
		Map.entry("echo virtus mask", "virtus mask"),
		Map.entry("echo virtus robe top", "virtus robe top"),
		Map.entry("echo virtus robe bottom", "virtus robe bottom"),
		Map.entry("sanguine scythe of vitur", "scythe of vitur"),
		Map.entry("holy scythe of vitur", "scythe of vitur"),
		Map.entry("sanguine twisted bow", "twisted bow"),
		Map.entry("holy sanguinesti staff", "sanguinesti staff"),
		Map.entry("saturated heart", "imbued heart"),
		Map.entry("blessed dizana's max cape", "blessed dizana's quiver"),
		Map.entry("dizana's max cape", "blessed dizana's quiver"),
		Map.entry("assembler max cape", "ava's assembler"),
		Map.entry("masori assembler", "ava's assembler"),
		Map.entry("masori assembler max cape", "ava's assembler"),
		Map.entry("accumulator max cape", "ava's accumulator"));

	private OwnedItemNames()
	{
	}

	public static String normalize(String name)
	{
		if (name == null || name.isEmpty())
		{
			return "";
		}
		String trimmed = fold(name.trim().toLowerCase(Locale.ROOT));
		trimmed = stripRepeating(CHARGES, trimmed);
		trimmed = stripCosmeticTags(trimmed);
		trimmed = IMBUED.matcher(trimmed).replaceAll(" (i)");
		return trimmed.replaceAll("\\s+", " ").trim();
	}

	public static String imageName(String name)
	{
		if (name == null || name.isEmpty())
		{
			return "";
		}
		return stripTags(fold(name.trim()), LOCK_TAGS);
	}

	public static Set<String> keys(String name)
	{
		String normalized = normalize(name);
		if (normalized.isEmpty())
		{
			return Set.of();
		}
		Set<String> keys = new HashSet<>();
		keys.add(normalized);
		add(keys, ORNAMENTS.get(normalized));
		add(keys, slayerHelmetBase(normalized));
		add(keys, infinityBase(normalized));
		if (IMBUED_GOD_CAPE.matcher(normalized).matches() || normalized.equals("imbued god cape"))
		{
			keys.add("imbued god cape");
		}
		return keys;
	}

	public static boolean matches(String wikiName, String ownedName)
	{
		Set<String> wiki = keys(wikiName);
		if (wiki.isEmpty())
		{
			return false;
		}
		for (String key : keys(ownedName))
		{
			if (wiki.contains(key))
			{
				return true;
			}
		}
		return false;
	}

	public static String preferredOwnedName(String wikiName, Iterable<String> ownedNames)
	{
		if (wikiName == null || ownedNames == null)
		{
			return null;
		}
		boolean ownsWikiItem = ownsCosmeticForm(wikiName, ownedNames);
		String best = null;
		int bestScore = 0;
		for (String owned : ownedNames)
		{
			if (!matches(wikiName, owned))
			{
				continue;
			}
			int score = variantScore(wikiName, owned, ownsWikiItem);
			if (score > bestScore)
			{
				bestScore = score;
				best = imageName(owned);
			}
		}
		return bestScore > 0 ? best : null;
	}

	public static String cosmeticForm(String name)
	{
		if (name == null || name.isEmpty())
		{
			return "";
		}
		String trimmed = fold(name.trim().toLowerCase(Locale.ROOT));
		trimmed = stripRepeating(CHARGES, trimmed);
		trimmed = stripTags(trimmed, Set.of("empty", "uncharged"));
		trimmed = stripTags(trimmed, LOCK_TAGS);
		trimmed = IMBUED.matcher(trimmed).replaceAll(" (i)");
		return trimmed.replaceAll("\\s+", " ").trim();
	}

	private static boolean ownsCosmeticForm(String wikiName, Iterable<String> ownedNames)
	{
		String wikiForm = cosmeticForm(wikiName);
		if (wikiForm.isEmpty())
		{
			return false;
		}
		for (String owned : ownedNames)
		{
			if (owned != null && wikiForm.equals(cosmeticForm(owned)))
			{
				return true;
			}
		}
		return false;
	}

	private static int variantScore(String wikiName, String ownedName, boolean ownsWikiItem)
	{
		String wikiForm = cosmeticForm(wikiName);
		String ownedForm = cosmeticForm(ownedName);
		if (ownedForm.isEmpty() || ownedForm.equals(wikiForm))
		{
			return 0;
		}
		if (ownsWikiItem && isBaseOf(wikiName, ownedName))
		{
			return 0;
		}
		int score = 10 + ownedForm.length();
		if (ORNAMENTS.containsKey(normalize(ownedName)))
		{
			score += 100;
		}
		return score;
	}

	private static boolean isBaseOf(String upgradeName, String baseName)
	{
		String upgrade = normalize(upgradeName);
		String base = normalize(baseName);
		return ORNAMENTS.containsKey(upgrade) && base.equals(ORNAMENTS.get(upgrade));
	}

	private static String slayerHelmetBase(String name)
	{
		if (name.equals("slayer helmet") || name.equals("slayer helmet (i)"))
		{
			return null;
		}
		if (name.endsWith(" slayer helmet (i)"))
		{
			return "slayer helmet (i)";
		}
		if (name.endsWith(" slayer helmet"))
		{
			return "slayer helmet";
		}
		return null;
	}

	private static String infinityBase(String name)
	{
		if (name.startsWith("dark infinity "))
		{
			return "infinity " + name.substring("dark infinity ".length());
		}
		if (name.startsWith("light infinity "))
		{
			return "infinity " + name.substring("light infinity ".length());
		}
		return null;
	}

	private static String fold(String name)
	{
		return name.replace('_', ' ')
			.replace('\u2018', '\'')
			.replace('\u2019', '\'')
			.replace('\u02BC', '\'');
	}

	private static String stripCosmeticTags(String name)
	{
		return stripTags(stripTags(name, LOCK_TAGS), COSMETIC_TAGS);
	}

	private static String stripTags(String name, Set<String> tags)
	{
		String current = name;
		while (true)
		{
			Matcher matcher = TRAILING_TAG.matcher(current);
			if (!matcher.find())
			{
				return current;
			}
			String tag = matcher.group(1).trim().toLowerCase(Locale.ROOT);
			if (!tags.contains(tag))
			{
				return current;
			}
			current = current.substring(0, matcher.start()).trim();
		}
	}

	private static String stripRepeating(Pattern pattern, String value)
	{
		String current = value;
		while (true)
		{
			String stripped = pattern.matcher(current).replaceAll("");
			if (stripped.equals(current))
			{
				return stripped;
			}
			current = stripped;
		}
	}

	private static void add(Set<String> keys, String extra)
	{
		if (extra != null && !extra.isEmpty())
		{
			keys.add(extra);
		}
	}
}
