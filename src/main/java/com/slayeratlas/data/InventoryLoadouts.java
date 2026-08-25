package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class InventoryLoadouts
{
	public static final int SIZE = 28;
	public static final String FOOD = "Cooked moonlight antelope";
	private static final int PRAYER_POTIONS = 4;
	private static final int SUPER_RESTORES = 2;
	private static final int GOADING_POTIONS = 2;

	private InventoryLoadouts()
	{
	}

	public static List<GearItem> forMonster(CombatStyle style, SlayerMonster monster, List<GearItem> extras)
	{
		List<GearItem> items = new ArrayList<>();
		addUniqueExtras(items, extras);
		addStyleBoost(items, style);
		addCopies(items, "Prayer potion", PRAYER_POTIONS);
		if (style == CombatStyle.MAGIC && isBurstable(monster))
		{
			addCopies(items, "Goading potion", GOADING_POTIONS);
		}
		addMonsterPotions(items, style, monster);
		if (needsSuperRestore(monster, items))
		{
			addCopies(items, "Super restore", SUPER_RESTORES);
		}
		return filled(items);
	}

	public static List<GearItem> filled(List<GearItem> items)
	{
		List<GearItem> filled = new ArrayList<>();
		if (items != null)
		{
			for (GearItem item : items)
			{
				GearItem usable = usable(item);
				if (usable != null && filled.size() < SIZE)
				{
					filled.add(usable);
				}
			}
		}
		GearItem food = GearItem.named(FOOD);
		while (filled.size() < SIZE)
		{
			filled.add(food);
		}
		return filled;
	}

	private static void addStyleBoost(List<GearItem> items, CombatStyle style)
	{
		if (style == CombatStyle.MAGIC)
		{
			addUnique(items, GearItem.named("Imbued heart"));
			addDivineRunePouch(items);
			return;
		}
		if (style == CombatStyle.RANGED)
		{
			addCopies(items, "Divine bastion potion", 1);
			return;
		}
		addCopies(items, "Divine super combat potion", 1);
	}

	private static void addMonsterPotions(List<GearItem> items, CombatStyle style, SlayerMonster monster)
	{
		if (monster == null || monster.getRecommendedPotions() == null)
		{
			return;
		}
		for (String blurb : monster.getRecommendedPotions())
		{
			addPotionFromBlurb(items, style, blurb);
		}
	}

	private static void addPotionFromBlurb(List<GearItem> items, CombatStyle style, String blurb)
	{
		if (blurb == null || blurb.isEmpty())
		{
			return;
		}
		String lower = blurb.toLowerCase(Locale.ROOT);
		if (lower.startsWith("none") || lower.contains("waterskin") || lower.contains("optional")
			|| lower.contains("if affordable"))
		{
			return;
		}
		String chosen = pickForStyle(blurb, style).replaceAll("(?i)\\s+if affordable$", "").trim();
		if (chosen.isEmpty() || isPrayerOrRestore(chosen) || isWrongStyleBoost(chosen, style)
			|| isMagicBoost(chosen))
		{
			return;
		}
		addCopies(items, preferDivine(chosen), 1);
	}

	private static String pickForStyle(String blurb, CombatStyle style)
	{
		String[] parts = blurb.split("(?i)\\s+or\\s+");
		if (parts.length == 1)
		{
			return parts[0].split(",")[0].trim();
		}
		for (String part : parts)
		{
			String option = part.split(",")[0].trim();
			String lower = option.toLowerCase(Locale.ROOT);
			if (style == CombatStyle.RANGED && lower.contains("rang"))
			{
				return option;
			}
			if (style == CombatStyle.MAGIC && isMagicBoost(option))
			{
				return option;
			}
			if (style == CombatStyle.MELEE && (lower.contains("combat") || lower.contains("attack")
				|| lower.contains("strength")))
			{
				return option;
			}
		}
		return parts[0].split(",")[0].trim();
	}

	private static boolean isWrongStyleBoost(String name, CombatStyle style)
	{
		String lower = name.toLowerCase(Locale.ROOT);
		boolean ranging = lower.contains("rang");
		boolean combat = lower.contains("combat") || lower.contains("attack") || lower.contains("strength");
		if (style != CombatStyle.RANGED && ranging)
		{
			return true;
		}
		if (style != CombatStyle.MELEE && combat && !ranging)
		{
			return true;
		}
		return false;
	}

	private static boolean isMagicBoost(String name)
	{
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.contains("magic potion") || lower.contains("forgotten brew")
			|| lower.contains("battlemage");
	}

	private static boolean isBurstable(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		return mentionsBurst(monster.getRecommendedStyle())
			|| mentionsBurst(monster.getWeakness())
			|| mentionsBurst(monster.getNotes())
			|| mentionsBurst(join(monster.getRecommendedEquipment()));
	}

	private static boolean mentionsBurst(String text)
	{
		if (text == null || text.isEmpty())
		{
			return false;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		return lower.contains("burst") || lower.contains("barrage");
	}

	private static boolean isPrayerOrRestore(String name)
	{
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.contains("prayer") || lower.contains("restore");
	}

	private static boolean needsSuperRestore(SlayerMonster monster, List<GearItem> items)
	{
		return containsSaradominBrew(items) || mentionsStatDrain(monster);
	}

	private static boolean containsSaradominBrew(List<GearItem> items)
	{
		for (GearItem item : items)
		{
			if (item != null && item.getName() != null
				&& item.getName().toLowerCase(Locale.ROOT).contains("saradomin brew"))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean mentionsStatDrain(SlayerMonster monster)
	{
		if (monster == null)
		{
			return false;
		}
		return mentionsDrain(monster.getWeakness())
			|| mentionsDrain(monster.getNotes())
			|| mentionsDrain(monster.getAttackStyle())
			|| mentionsDrain(join(monster.getRecommendedPotions()));
	}

	private static boolean mentionsDrain(String text)
	{
		if (text == null || text.isEmpty())
		{
			return false;
		}
		String lower = text.toLowerCase(Locale.ROOT);
		return lower.contains("drain");
	}

	private static String join(List<String> values)
	{
		if (values == null || values.isEmpty())
		{
			return "";
		}
		return String.join(" ", values);
	}

	private static String preferDivine(String name)
	{
		String trimmed = name.trim();
		String lower = trimmed.toLowerCase(Locale.ROOT);
		if (lower.equals("super combat potion") || lower.equals("super combat")
			|| lower.equals("divine super combat") || lower.equals("divine super combat potion"))
		{
			return "Divine super combat potion";
		}
		if (lower.equals("ranging potion") || lower.equals("super ranging potion")
			|| lower.equals("divine ranging potion") || lower.contains("ranging potion"))
		{
			return "Divine bastion potion";
		}
		if (lower.equals("bastion potion") || lower.equals("divine bastion potion"))
		{
			return "Divine bastion potion";
		}
		if (lower.equals("super attack potion") || lower.equals("divine super attack potion"))
		{
			return "Divine super attack potion";
		}
		if (lower.equals("super strength potion") || lower.equals("divine super strength potion"))
		{
			return "Divine super strength potion";
		}
		if (lower.equals("super defence potion") || lower.equals("divine super defence potion"))
		{
			return "Divine super defence potion";
		}
		if (lower.startsWith("divine "))
		{
			return trimmed;
		}
		return trimmed;
	}

	private static void addCopies(List<GearItem> items, String name, int copies)
	{
		GearItem item = GearItem.named(dose(name));
		if (item == null)
		{
			return;
		}
		int have = countKey(items, item.getName());
		for (int index = have; index < copies && items.size() < SIZE; index++)
		{
			items.add(item);
		}
	}

	private static int countKey(List<GearItem> items, String name)
	{
		String key = key(name);
		int count = 0;
		for (GearItem existing : items)
		{
			if (existing != null && existing.getName() != null && key(existing.getName()).equals(key))
			{
				count++;
			}
		}
		return count;
	}

	private static void addUniqueExtras(List<GearItem> items, List<GearItem> extras)
	{
		if (extras == null)
		{
			return;
		}
		for (GearItem extra : extras)
		{
			addUnique(items, extra);
		}
	}

	private static void addDivineRunePouch(List<GearItem> items)
	{
		GearItem pouch = GearItem.named("Divine rune pouch");
		for (int index = 0; index < items.size(); index++)
		{
			GearItem existing = items.get(index);
			if (existing == null || existing.getName() == null)
			{
				continue;
			}
			String name = existing.getName().toLowerCase(Locale.ROOT);
			if (name.equals("rune pouch") || name.equals("divine rune pouch"))
			{
				items.set(index, pouch);
				return;
			}
		}
		addUnique(items, pouch);
	}

	private static void addUnique(List<GearItem> items, GearItem item)
	{
		GearItem usable = usable(item);
		if (usable == null || containsKey(items, usable.getName()) || items.size() >= SIZE)
		{
			return;
		}
		items.add(usable);
	}

	private static GearItem usable(GearItem item)
	{
		if (item == null || item.getName() == null)
		{
			return null;
		}
		String name = item.getName().trim();
		if (name.isEmpty() || isPlaceholder(name) || isWornGear(name))
		{
			return null;
		}
		if (name.toLowerCase(Locale.ROOT).contains("ranging potion"))
		{
			return GearItem.named(dose("Divine bastion potion"));
		}
		String remapped = preferDivine(name);
		if (!remapped.equals(name))
		{
			return GearItem.named(dose(remapped));
		}
		return item;
	}

	private static boolean isWornGear(String name)
	{
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.contains("defender")
			|| lower.contains("shield")
			|| lower.contains("helmet")
			|| lower.contains("helm")
			|| lower.contains("platebody")
			|| lower.contains("chestplate")
			|| lower.contains("tassets")
			|| lower.contains("platelegs")
			|| lower.contains("boots")
			|| lower.contains("gloves")
			|| lower.contains("cape")
			|| lower.contains("amulet")
			|| lower.contains("necklace")
			|| lower.contains("ring")
			|| lower.contains("blessing");
	}

	private static boolean isPlaceholder(String name)
	{
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.equals("n/a") || lower.equals("none") || lower.equals("-") || lower.equals("na");
	}

	private static boolean containsKey(List<GearItem> items, String name)
	{
		String key = key(name);
		for (GearItem existing : items)
		{
			if (existing != null && existing.getName() != null && key(existing.getName()).equals(key))
			{
				return true;
			}
		}
		return false;
	}

	private static String key(String name)
	{
		return name.toLowerCase(Locale.ROOT)
			.replace("(4)", "")
			.replace("divine ", "")
			.replace(" potion", "")
			.trim();
	}

	private static String dose(String name)
	{
		String trimmed = name.trim();
		String lower = trimmed.toLowerCase(Locale.ROOT);
		if ((lower.contains("potion") || lower.contains("restore") || lower.contains("brew")
			|| lower.contains("antifire") || lower.contains("venom") || lower.contains("antidote"))
			&& !trimmed.contains("("))
		{
			return trimmed + "(4)";
		}
		return trimmed;
	}
}
