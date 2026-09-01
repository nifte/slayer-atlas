package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class InventoryLoadouts
{
	public static final int SIZE = 28;
	public static final String FOOD = "Cooked moonlight antelope";
	private static final Pattern DOSE_SUFFIX = Pattern.compile("\\s*\\(\\d+\\)\\s*$");
	private static final int STYLE_BOOSTS = 2;
	private static final int SUPER_RESTORES = 4;
	private static final int GOADING_POTIONS = 2;
	private static final int MONSTER_POTIONS = 2;

	private InventoryLoadouts()
	{
	}

	public static List<GearItem> forMonster(CombatStyle style, SlayerMonster monster, List<GearItem> extras)
	{
		return forMonster(style, monster, extras, List.of(), GearRecommendation.specialized());
	}

	public static List<GearItem> forMonster(
		CombatStyle style,
		SlayerMonster monster,
		List<GearItem> extras,
		List<GearItem> wikiInventory,
		GearRecommendation recommendation)
	{
		return forMonster(style, monster, extras, wikiInventory, recommendation, null);
	}

	public static List<GearItem> forMonster(
		CombatStyle style,
		SlayerMonster monster,
		List<GearItem> extras,
		List<GearItem> wikiInventory,
		GearRecommendation recommendation,
		GearItem wornShield)
	{
		if (isWikiGrid(wikiInventory) && !ownedFilter(recommendation))
		{
			return ensureAntipoison(
				ensureRequiredTools(
					ensureAntifires(fromWikiGrid(wikiInventory), monster, wornShield, recommendation, true),
					monster,
					recommendation,
					true),
				monster,
				recommendation,
				true);
		}
		List<GearItem> items = new ArrayList<>();
		addUniqueExtras(items, extras, recommendation);
		addUniqueExtras(items, SpecialInventoryItems.forMonster(monster), recommendation);
		if (wikiInventory != null && !wikiInventory.isEmpty())
		{
			addWikiInventory(items, wikiInventory, recommendation);
		}
		addStyleBoost(items, style, recommendation);
		if (style == CombatStyle.MAGIC && isBurstable(monster))
		{
			addOwnedCopies(items, "Goading potion", GOADING_POTIONS, recommendation);
		}
		addMonsterPotions(items, style, monster, recommendation);
		ensureAntifires(items, monster, wornShield, recommendation, false);
		ensureAntipoison(items, monster, recommendation, false);
		if (needsSuperRestore(monster, items))
		{
			addOwnedCopies(items, "Super restore", SUPER_RESTORES, recommendation);
		}
		addFoodAndPrayer(items, monster, recommendation);
		collapseHearts(items, true);
		return filled(items, recommendation);
	}

	static boolean isWikiGrid(List<GearItem> wikiInventory)
	{
		if (wikiInventory == null)
		{
			return false;
		}
		int filled = 0;
		for (GearItem item : wikiInventory)
		{
			if (item != null)
			{
				filled++;
			}
		}
		return wikiInventory.size() >= 16 || filled >= 16;
	}

	private static boolean ownedFilter(GearRecommendation recommendation)
	{
		return recommendation != null && recommendation.filterToOwned();
	}

	private static List<GearItem> fromWikiGrid(List<GearItem> wikiInventory)
	{
		List<GearItem> items = new ArrayList<>();
		for (GearItem item : wikiInventory)
		{
			if (items.size() >= SIZE)
			{
				break;
			}
			items.add(wikiSlot(item));
		}
		while (items.size() < SIZE)
		{
			items.add(null);
		}
		collapseHearts(items, false);
		return items;
	}

	private static GearItem wikiSlot(GearItem item)
	{
		if (item == null || item.getName() == null || item.getName().isEmpty() || isPlaceholder(item.getName()))
		{
			return null;
		}
		String dosed = dose(item.getName());
		return dosed.equals(item.getName()) ? item : GearItem.named(dosed);
	}

	private static void addWikiInventory(
		List<GearItem> items,
		List<GearItem> wikiInventory,
		GearRecommendation recommendation)
	{
		boolean filter = ownedFilter(recommendation);
		OwnedItems owned = recommendation == null ? OwnedItems.none() : recommendation.owned();
		for (GearItem item : wikiInventory)
		{
			if (item == null)
			{
				continue;
			}
			if (filter && !owned.contains(item))
			{
				continue;
			}
			GearItem shown = filter ? owned.shownAs(item) : item;
			addWikiItem(items, shown);
		}
	}

	private static void addWikiItem(List<GearItem> items, GearItem item)
	{
		GearItem usable = usable(item);
		if (usable == null)
		{
			return;
		}
		if (heartRank(usable) > 0)
		{
			addUnique(items, usable);
			return;
		}
		if (items.size() >= SIZE)
		{
			return;
		}
		items.add(usable);
	}

	public static List<GearItem> slots(List<GearItem> items)
	{
		List<GearItem> slots = new ArrayList<>(SIZE);
		if (items != null)
		{
			for (GearItem item : items)
			{
				if (slots.size() >= SIZE)
				{
					break;
				}
				slots.add(item);
			}
		}
		while (slots.size() < SIZE)
		{
			slots.add(null);
		}
		return slots;
	}

	public static List<GearItem> filled(List<GearItem> items)
	{
		return filled(items, GearRecommendation.specialized());
	}

	public static List<GearItem> filled(List<GearItem> items, GearRecommendation recommendation)
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
		GearItem food = fillFood(filled, recommendation);
		while (filled.size() < SIZE)
		{
			filled.add(food);
		}
		return filled;
	}

	private static GearItem fillFood(List<GearItem> items, GearRecommendation recommendation)
	{
		GearItem existing = existingFood(items);
		if (existing != null)
		{
			return existing;
		}
		GearItem picked = OwnedSupplies.pick(OwnedSupplies.FOOD, recommendation);
		if (picked != null)
		{
			return picked;
		}
		return GearItem.named(FOOD);
	}

	private static GearItem existingFood(List<GearItem> items)
	{
		if (items == null)
		{
			return null;
		}
		for (GearItem item : items)
		{
			if (isFillFood(item))
			{
				return item;
			}
		}
		return null;
	}

	private static boolean isFillFood(GearItem item)
	{
		if (item == null || item.getName() == null || item.getName().isEmpty())
		{
			return false;
		}
		for (GearItem food : OwnedSupplies.FOOD)
		{
			if (food != null && food.getName() != null && OwnedItemNames.matches(item.getName(), food.getName()))
			{
				return true;
			}
		}
		String lower = item.getName().toLowerCase(Locale.ROOT);
		return lower.contains("cooked") || lower.contains("karambwan");
	}

	private static void addStyleBoost(List<GearItem> items, CombatStyle style, GearRecommendation recommendation)
	{
		if (style == CombatStyle.MAGIC)
		{
			addUnique(items, magicHeart(recommendation));
			addDivineRunePouch(items, recommendation);
			return;
		}
		if (style == CombatStyle.RANGED)
		{
			addOwnedBoost(items, OwnedSupplies.RANGED_BOOST, recommendation);
			return;
		}
		addOwnedBoost(items, OwnedSupplies.MELEE_BOOST, recommendation);
	}

	private static void addOwnedBoost(List<GearItem> items, List<GearItem> ranks, GearRecommendation recommendation)
	{
		GearItem boost = OwnedSupplies.pick(ranks, recommendation);
		if (boost == null)
		{
			return;
		}
		addCopies(items, boost.getName(), STYLE_BOOSTS);
	}

	private static void addOwnedUnique(List<GearItem> items, GearItem item, GearRecommendation recommendation)
	{
		if (item == null)
		{
			return;
		}
		if (ownedFilter(recommendation) && !recommendation.owned().contains(item))
		{
			return;
		}
		addUnique(items, ownedFilter(recommendation) ? recommendation.owned().shownAs(item) : item);
	}

	private static void addOwnedCopies(List<GearItem> items, String name, int copies, GearRecommendation recommendation)
	{
		GearItem item = GearItem.named(name);
		if (item == null)
		{
			return;
		}
		if (ownedFilter(recommendation) && !recommendation.owned().contains(item))
		{
			return;
		}
		addCopies(items, name, copies);
	}

	private static GearItem magicHeart(GearRecommendation recommendation)
	{
		GearItem saturated = GearItem.named("Saturated heart");
		if (!ownedFilter(recommendation))
		{
			return saturated;
		}
		OwnedItems owned = recommendation.owned();
		if (!owned.contains(saturated) && !owned.contains(GearItem.named("Imbued heart")))
		{
			return null;
		}
		return owned.shownAs(saturated);
	}

	private static List<GearItem> ensureAntifires(
		List<GearItem> items,
		SlayerMonster monster,
		GearItem wornShield,
		GearRecommendation recommendation,
		boolean preserveSlots)
	{
		if (!DragonfireSupplies.needsPotion(monster, wornShield) || containsAntifire(items))
		{
			return items;
		}
		GearItem potion = OwnedSupplies.pick(OwnedSupplies.ANTIFIRE, recommendation);
		if (potion == null)
		{
			return items;
		}
		if (preserveSlots)
		{
			GearItem dosed = GearItem.named(dose(potion.getName()));
			int added = 0;
			for (int index = 0; index < items.size() && added < MONSTER_POTIONS; index++)
			{
				if (items.get(index) == null)
				{
					items.set(index, dosed);
					added++;
				}
			}
			return items;
		}
		addCopies(items, potion.getName(), MONSTER_POTIONS);
		return items;
	}

	private static boolean containsAntifire(List<GearItem> items)
	{
		for (GearItem item : items)
		{
			if (item != null && item.getName() != null
				&& item.getName().toLowerCase(Locale.ROOT).contains("antifire"))
			{
				return true;
			}
		}
		return false;
	}

	private static List<GearItem> ensureRequiredTools(
		List<GearItem> items,
		SlayerMonster monster,
		GearRecommendation recommendation,
		boolean preserveSlots)
	{
		for (GearItem tool : SpecialInventoryItems.forMonster(monster))
		{
			if (tool == null || containsKey(items, tool.getName()))
			{
				continue;
			}
			if (ownedFilter(recommendation) && !recommendation.owned().contains(tool))
			{
				continue;
			}
			if (preserveSlots)
			{
				placeRequiredTool(items, tool);
			}
			else
			{
				addUnique(items, tool);
			}
		}
		return items;
	}

	private static void placeRequiredTool(List<GearItem> items, GearItem tool)
	{
		for (int index = 0; index < items.size(); index++)
		{
			if (items.get(index) == null)
			{
				items.set(index, tool);
				return;
			}
		}
		for (int index = 0; index < items.size(); index++)
		{
			if (isFillFood(items.get(index)))
			{
				items.set(index, tool);
				return;
			}
		}
	}

	private static List<GearItem> ensureAntipoison(
		List<GearItem> items,
		SlayerMonster monster,
		GearRecommendation recommendation,
		boolean preserveSlots)
	{
		if (!PoisonSupplies.needsPotion(monster) || containsAntipoison(items))
		{
			return items;
		}
		GearItem potion = OwnedSupplies.pick(OwnedSupplies.ANTIPOISON, recommendation);
		if (potion == null)
		{
			return items;
		}
		if (preserveSlots)
		{
			placeAntipoison(items, potion);
			return items;
		}
		addCopies(items, potion.getName(), MONSTER_POTIONS);
		return items;
	}

	private static void placeAntipoison(List<GearItem> items, GearItem potion)
	{
		GearItem dosed = GearItem.named(dose(potion.getName()));
		int added = 0;
		for (int index = 0; index < items.size() && added < MONSTER_POTIONS; index++)
		{
			if (items.get(index) == null)
			{
				items.set(index, dosed);
				added++;
			}
		}
		for (int index = 0; index < items.size() && added < MONSTER_POTIONS; index++)
		{
			GearItem current = items.get(index);
			if (current != null && current.getName() != null && isPrayerOrRestore(current.getName()))
			{
				items.set(index, dosed);
				added++;
			}
		}
		for (int index = 0; index < items.size() && added < MONSTER_POTIONS; index++)
		{
			if (isFillFood(items.get(index)))
			{
				items.set(index, dosed);
				added++;
			}
		}
	}

	private static boolean containsAntipoison(List<GearItem> items)
	{
		for (GearItem item : items)
		{
			if (item != null && PoisonSupplies.mentionsCure(item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	private static void addMonsterPotions(
		List<GearItem> items,
		CombatStyle style,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		if (monster == null || monster.getRecommendedPotions() == null)
		{
			return;
		}
		for (String blurb : monster.getRecommendedPotions())
		{
			addPotionFromBlurb(items, style, blurb, recommendation);
		}
	}

	private static void addPotionFromBlurb(
		List<GearItem> items,
		CombatStyle style,
		String blurb,
		GearRecommendation recommendation)
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
		List<GearItem> family = potionFamily(chosen);
		GearItem picked = OwnedSupplies.pick(family, recommendation);
		if (picked == null)
		{
			return;
		}
		addCopies(items, preferDivine(picked.getName()), MONSTER_POTIONS);
	}

	private static List<GearItem> potionFamily(String chosen)
	{
		String lower = chosen.toLowerCase(Locale.ROOT);
		if (lower.contains("antifire"))
		{
			return OwnedSupplies.ANTIFIRE;
		}
		if (lower.contains("venom") || lower.contains("poison") || lower.contains("antidote"))
		{
			return OwnedSupplies.ANTIPOISON;
		}
		return List.of(GearItem.named(preferDivine(chosen)));
	}

	private static void addFoodAndPrayer(
		List<GearItem> items,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		int remaining = SIZE - items.size();
		if (remaining <= 0)
		{
			return;
		}
		int food = Math.min(remaining, InventoryRations.foodSlots(monster));
		int prayer = remaining - food;
		GearItem prayerItem = OwnedSupplies.pick(OwnedSupplies.PRAYER, recommendation);
		GearItem foodItem = OwnedSupplies.pick(OwnedSupplies.FOOD, recommendation);
		addExact(items, prayerItem == null ? null : dose(prayerItem.getName()), prayer);
		addExact(items, foodItem == null ? null : foodItem.getName(), food);
	}

	private static void addExact(List<GearItem> items, String name, int copies)
	{
		if (name == null || name.isEmpty())
		{
			return;
		}
		GearItem item = GearItem.named(name);
		if (item == null)
		{
			return;
		}
		for (int index = 0; index < copies && items.size() < SIZE; index++)
		{
			items.add(item);
		}
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

	private static void addDivineRunePouch(List<GearItem> items, GearRecommendation recommendation)
	{
		GearItem pouch = GearItem.named("Divine rune pouch");
		GearItem plain = GearItem.named("Rune pouch");
		if (ownedFilter(recommendation))
		{
			OwnedItems owned = recommendation.owned();
			if (owned.contains(pouch))
			{
				pouch = owned.shownAs(pouch);
			}
			else if (owned.contains(plain))
			{
				pouch = owned.shownAs(plain);
			}
			else
			{
				return;
			}
		}
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

	private static void addUniqueExtras(
		List<GearItem> items,
		List<GearItem> extras,
		GearRecommendation recommendation)
	{
		if (extras == null)
		{
			return;
		}
		for (GearItem extra : extras)
		{
			addOwnedUnique(items, extra, recommendation);
		}
	}

	private static void addUnique(List<GearItem> items, GearItem item)
	{
		GearItem usable = usable(item);
		if (usable == null)
		{
			return;
		}
		int existing = indexOfKey(items, usable.getName());
		if (existing >= 0)
		{
			if (heartRank(usable) > heartRank(items.get(existing)))
			{
				items.set(existing, usable);
			}
			return;
		}
		if (items.size() >= SIZE)
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
		return GearItem.named(dose(preferDivine(name)));
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
		return indexOfKey(items, name) >= 0;
	}

	private static int indexOfKey(List<GearItem> items, String name)
	{
		String itemKey = key(name);
		for (int index = 0; index < items.size(); index++)
		{
			GearItem existing = items.get(index);
			if (existing != null && existing.getName() != null && key(existing.getName()).equals(itemKey))
			{
				return index;
			}
		}
		return -1;
	}

	private static void collapseHearts(List<GearItem> items, boolean compact)
	{
		int bestIndex = -1;
		int bestRank = 0;
		for (int index = 0; index < items.size(); index++)
		{
			int rank = heartRank(items.get(index));
			if (rank > bestRank)
			{
				bestRank = rank;
				bestIndex = index;
			}
		}
		if (bestIndex < 0)
		{
			return;
		}
		for (int index = items.size() - 1; index >= 0; index--)
		{
			if (index == bestIndex || heartRank(items.get(index)) == 0)
			{
				continue;
			}
			if (compact)
			{
				items.remove(index);
			}
			else
			{
				items.set(index, null);
			}
		}
	}

	private static int heartRank(GearItem item)
	{
		if (item == null || item.getName() == null)
		{
			return 0;
		}
		String lower = item.getName().toLowerCase(Locale.ROOT);
		if (lower.equals("saturated heart"))
		{
			return 2;
		}
		if (lower.equals("imbued heart"))
		{
			return 1;
		}
		return 0;
	}

	private static String key(String name)
	{
		String key = name.toLowerCase(Locale.ROOT)
			.replace("(4)", "")
			.replace("divine ", "")
			.replace(" potion", "")
			.trim();
		if (key.equals("imbued heart") || key.equals("saturated heart"))
		{
			return "magic heart";
		}
		return key;
	}

	private static String dose(String name)
	{
		String trimmed = name.trim();
		if (!isDosedConsumable(trimmed))
		{
			return trimmed;
		}
		String base = DOSE_SUFFIX.matcher(trimmed).replaceAll("").trim();
		int full = base.toLowerCase(Locale.ROOT).endsWith(" mix") ? 2 : 4;
		return base + "(" + full + ")";
	}

	private static boolean isDosedConsumable(String name)
	{
		String lower = name.toLowerCase(Locale.ROOT);
		return lower.contains("potion")
			|| lower.contains("restore")
			|| lower.contains("brew")
			|| lower.contains("antifire")
			|| lower.contains("venom")
			|| lower.contains("antidote")
			|| lower.contains("antipoison")
			|| lower.contains("serum")
			|| lower.endsWith(" mix")
			|| lower.contains(" mix(");
	}
}
