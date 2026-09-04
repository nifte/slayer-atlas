package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class BisRanks
{
	private BisRanks()
	{
	}

	public static RankedGearLoadout forStyle(CombatStyle style, SlayerMonster monster)
	{
		return forStyle(style, monster, GearRecommendation.specialized());
	}

	public static RankedGearLoadout forStyle(
		CombatStyle style,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		Map<EquipmentSlot, List<GearItem>> ranks = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (slot.onWornGrid())
			{
				ranks.put(slot, ladder(style, slot, monster, recommendation));
			}
		}
		return new RankedGearLoadout("", style, true, ranks, List.of());
	}

	public static RankedGearLoadout merge(RankedGearLoadout ranked, SlayerMonster monster)
	{
		return merge(ranked, monster, GearRecommendation.specialized());
	}

	public static RankedGearLoadout merge(
		RankedGearLoadout ranked,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		if (ranked == null)
		{
			return null;
		}
		Map<EquipmentSlot, List<GearItem>> ranks = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (slot.onWornGrid())
			{
				ranks.put(
					slot,
					mergeSlot(
						ladder(ranked.getStyle(), slot, monster, recommendation),
						ranked.ranks(slot),
						slot,
						monster,
						recommendation));
			}
		}
		List<GearItem> specials = new ArrayList<>(ranked.getSpecials());
		return new RankedGearLoadout(
			ranked.getPageName(),
			ranked.getCaption(),
			ranked.getStyle(),
			ranked.isPrimary(),
			ranks,
			specials,
			ranked.getWikiInventory());
	}

	static List<GearItem> ladder(CombatStyle style, EquipmentSlot slot, SlayerMonster monster)
	{
		return ladder(style, slot, monster, GearRecommendation.specialized());
	}

	static List<GearItem> ladder(
		CombatStyle style,
		EquipmentSlot slot,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		if (slot == EquipmentSlot.HEAD)
		{
			return heads(style);
		}
		if (slot == EquipmentSlot.CAPE)
		{
			return capes(style);
		}
		if (slot == EquipmentSlot.NECK)
		{
			return necks(style, monster);
		}
		if (slot == EquipmentSlot.AMMO)
		{
			return ammo(style, monster);
		}
		if (slot == EquipmentSlot.WEAPON)
		{
			return weapons(style, monster);
		}
		if (slot == EquipmentSlot.BODY)
		{
			return bodies(style);
		}
		if (slot == EquipmentSlot.SHIELD)
		{
			return shields(style, monster, recommendation);
		}
		if (slot == EquipmentSlot.LEGS)
		{
			return legs(style);
		}
		if (slot == EquipmentSlot.HANDS)
		{
			return hands(style);
		}
		if (slot == EquipmentSlot.FEET)
		{
			return feet();
		}
		if (slot == EquipmentSlot.RING)
		{
			return rings(style);
		}
		return List.of();
	}

	private static List<GearItem> heads(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return List.of(
				SlayerHelmet.IMBUED,
				GearItem.named("Masori mask (f)"),
				GearItem.named("Armadyl helmet"),
				GearItem.named("Robin hood hat"));
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(
				SlayerHelmet.IMBUED,
				GearItem.named("Ancestral hat"),
				GearItem.named("Ahrim's hood"),
				GearItem.named("Mystic hat"));
		}
		return List.of(
			SlayerHelmet.IMBUED,
			GearItem.named("Torva full helm"),
			GearItem.named("Neitiznot faceguard"),
			GearItem.named("Helm of neitiznot"),
			GearItem.named("Berserker helm"));
	}

	private static List<GearItem> capes(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return List.of(
				RangedCapes.BLESSED_QUIVER,
				RangedCapes.QUIVER,
				RangedCapes.ASSEMBLER,
				RangedCapes.ACCUMULATOR,
				RangedCapes.ATTRACTOR);
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(
				GearItem.named("Imbued Saradomin cape"),
				GearItem.named("Imbued god cape"),
				GearItem.named("God cape"),
				GearItem.named("Ardougne cloak 4"));
		}
		return List.of(
			GearItem.named("Infernal cape"),
			GearItem.named("Fire cape"),
			GearItem.named("Mythical cape"),
			GearItem.named("Ardougne cloak 4"),
			GearItem.named("Obsidian cape"));
	}

	private static List<GearItem> necks(CombatStyle style, SlayerMonster monster)
	{
		List<GearItem> ranks = new ArrayList<>(UndeadGear.necks(monster));
		if (style == CombatStyle.RANGED)
		{
			addAll(ranks, List.of(
				GearItem.named("Necklace of rupture"),
				GearItem.named("Necklace of anguish"),
				GearItem.named("Amulet of fury"),
				GearItem.named("Amulet of glory")));
			return ranks;
		}
		if (style == CombatStyle.MAGIC)
		{
			addAll(ranks, List.of(
				GearItem.named("Occult necklace"),
				GearItem.named("Amulet of fury"),
				GearItem.named("Amulet of glory"),
				GearItem.named("Amulet of magic")));
			return ranks;
		}
		addAll(ranks, List.of(
			GearItem.named("Amulet of rancour"),
			GearItem.named("Amulet of torture"),
			GearItem.named("Amulet of fury"),
			GearItem.named("Amulet of glory"),
			GearItem.named("Amulet of strength")));
		return ranks;
	}

	private static List<GearItem> ammo(CombatStyle style, SlayerMonster monster)
	{
		List<GearItem> ranks = new ArrayList<>();
		if (style == CombatStyle.RANGED && DemonbaneGear.applies(monster))
		{
			ranks.add(DemonbaneGear.DRAGON_ARROW);
		}
		addAll(ranks, LeafBladedGear.ammo(style, monster));
		if (style == CombatStyle.RANGED)
		{
			addAll(ranks, List.of(
				GearItem.named("Ruby dragon bolts (e)"),
				GearItem.named("Ruby bolts (e)"),
				GearItem.named("Broad bolts"),
				GearItem.named("Mithril bolts")));
			return ranks;
		}
		addAll(ranks, List.of(
			GearItem.named("Rada's blessing 4"),
			GearItem.named("Rada's blessing 3"),
			GearItem.named("Holy blessing")));
		return ranks;
	}

	private static List<GearItem> weapons(CombatStyle style, SlayerMonster monster)
	{
		List<GearItem> ranks = new ArrayList<>();
		addAll(ranks, LeafBladedGear.ranks(style, monster));
		addAll(ranks, VampyreGear.ranks(style, monster));
		addAll(ranks, demonWeapons(style, monster));
		addAll(ranks, dragonWeapons(style, monster));
		addAll(ranks, KalphiteGear.ranks(style, monster));
		addAll(ranks, CrushWeapons.ranks(style, monster));
		addAll(ranks, styleWeapons(style));
		return ranks;
	}

	private static List<GearItem> demonWeapons(CombatStyle style, SlayerMonster monster)
	{
		if (!DemonbaneGear.applies(monster))
		{
			return List.of();
		}
		if (style == CombatStyle.RANGED)
		{
			return List.of(DemonbaneGear.SCORCHING_BOW);
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(DemonbaneGear.PURGING_STAFF);
		}
		return List.of(
			DemonbaneGear.EMBERLIGHT,
			GearItem.named("Arclight"),
			GearItem.named("Darklight"));
	}

	private static List<GearItem> dragonWeapons(CombatStyle style, SlayerMonster monster)
	{
		if (!DragonbaneGear.applies(monster))
		{
			return List.of();
		}
		return List.of(DragonbaneGear.weapon(style));
	}

	private static List<GearItem> styleWeapons(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return List.of(
				GearItem.named("Zaryte crossbow"),
				GearItem.named("Bow of faerdhinen"),
				GearItem.named("Twisted bow"),
				GearItem.named("Venator bow"),
				GearItem.named("Toxic blowpipe"),
				GearItem.named("Dragon crossbow"),
				GearItem.named("Rune crossbow"));
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(
				GearItem.named("Tumeken's shadow"),
				GearItem.named("Eye of Ayak"),
				GearItem.named("Sanguinesti staff"),
				GearItem.named("Trident of the swamp"),
				GearItem.named("Trident of the seas"),
				GearItem.named("Iban's staff"));
		}
		return List.of(
			GearItem.named("Ghrazi rapier"),
			GearItem.named("Inquisitor's mace"),
			GearItem.named("Blade of saeldor"),
			GearItem.named("Osmumten's fang"),
			GearItem.named("Abyssal tentacle"),
			GearItem.named("Abyssal whip"),
			GearItem.named("Dragon scimitar"),
			GearItem.named("Rune scimitar"));
	}

	private static List<GearItem> bodies(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return List.of(
				GearItem.named("Masori body (f)"),
				GearItem.named("Armadyl chestplate"),
				GearItem.named("Black d'hide body"));
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(
				GearItem.named("Ancestral robe top"),
				GearItem.named("Ahrim's robetop"),
				GearItem.named("Mystic robe top"));
		}
		return List.of(
			GearItem.named("Torva platebody"),
			GearItem.named("Bandos chestplate"),
			GearItem.named("Fighter torso"),
			GearItem.named("Rune platebody"));
	}

	private static List<GearItem> shields(
		CombatStyle style,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		List<GearItem> ranks = new ArrayList<>();
		GearItem special = OffhandGear.forMonster(style, monster, recommendation);
		if (special != null)
		{
			ranks.add(special);
		}
		boolean fireFirst = DragonbaneGear.applies(monster)
			&& DragonfireSupplies.needsDragonfireOffhand(monster, recommendation);
		if (fireFirst)
		{
			addAll(ranks, dragonfireOffhands());
		}
		addAll(ranks, styleOffhands(style));
		if (DragonbaneGear.applies(monster) && !fireFirst)
		{
			addAll(ranks, dragonfireOffhands());
		}
		return ranks;
	}

	private static List<GearItem> dragonfireOffhands()
	{
		return List.of(
			OffhandGear.DRAGONFIRE_SHIELD,
			OffhandGear.DRAGONFIRE_WARD,
			OffhandGear.WYVERN_SHIELD,
			GearItem.named("Anti-dragon shield"));
	}

	private static List<GearItem> styleOffhands(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return List.of(
				OffhandGear.RANGED,
				GearItem.named("Odium ward"),
				GearItem.named("Book of law"));
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(
				OffhandGear.MAGIC,
				GearItem.named("Elidinis' ward"),
				GearItem.named("Arcane spirit shield"),
				GearItem.named("Malediction ward"),
				GearItem.named("Mage's book"));
		}
		return List.of(
			OffhandGear.MELEE,
			GearItem.named("Dragon defender"),
			GearItem.named("Rune defender"),
			GearItem.named("Adamant defender"));
	}

	private static List<GearItem> legs(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return List.of(
				GearItem.named("Masori chaps (f)"),
				GearItem.named("Armadyl chainskirt"),
				GearItem.named("Black d'hide chaps"));
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(
				GearItem.named("Ancestral robe bottom"),
				GearItem.named("Ahrim's robeskirt"),
				GearItem.named("Mystic robe bottom"));
		}
		return List.of(
			GearItem.named("Torva platelegs"),
			GearItem.named("Bandos tassets"),
			GearItem.named("Rune platelegs"));
	}

	private static List<GearItem> hands(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return List.of(
				GearItem.named("Zaryte vambraces"),
				GearItem.named("Barrows gloves"),
				GearItem.named("Black d'hide vambraces"));
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(
				GearItem.named("Confliction gauntlets"),
				GearItem.named("Tormented bracelet"),
				GearItem.named("Barrows gloves"),
				GearItem.named("Mystic gloves"));
		}
		return List.of(
			GearItem.named("Ferocious gloves"),
			GearItem.named("Barrows gloves"),
			GearItem.named("Dragon gloves"),
			GearItem.named("Rune gloves"));
	}

	private static List<GearItem> feet()
	{
		return List.of(
			GearItem.named("Avernic treads (max)"),
			GearItem.named("Primordial boots"),
			GearItem.named("Pegasian boots"),
			GearItem.named("Eternal boots"),
			GearItem.named("Dragon boots"),
			GearItem.named("Infinity boots"));
	}

	private static List<GearItem> rings(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return List.of(
				GearItem.named("Venator ring"),
				GearItem.named("Archers ring (i)"),
				GearItem.named("Archers ring"));
		}
		if (style == CombatStyle.MAGIC)
		{
			return List.of(
				GearItem.named("Magus ring"),
				GearItem.named("Seers ring (i)"),
				GearItem.named("Seers ring"));
		}
		return List.of(
			GearItem.named("Ultor ring"),
			GearItem.named("Berserker ring (i)"),
			GearItem.named("Berserker ring"),
			GearItem.named("Warrior ring"));
	}

	static List<GearItem> mergeSlot(List<GearItem> ladder, List<GearItem> wiki)
	{
		return mergeSlot(ladder, wiki, null, null);
	}

	static List<GearItem> mergeSlot(
		List<GearItem> ladder,
		List<GearItem> wiki,
		EquipmentSlot slot,
		SlayerMonster monster)
	{
		return mergeSlot(ladder, wiki, slot, monster, GearRecommendation.specialized());
	}

	static List<GearItem> mergeSlot(
		List<GearItem> ladder,
		List<GearItem> wiki,
		EquipmentSlot slot,
		SlayerMonster monster,
		GearRecommendation recommendation)
	{
		if (slot == EquipmentSlot.SHIELD
			&& DragonbaneGear.applies(monster)
			&& OffhandGear.prefersWikiRanks(wiki, monster, recommendation))
		{
			return mergeWikiFirst(ladder, wiki);
		}
		List<GearItem> source = wiki == null ? List.of() : wiki;
		List<GearItem> ranked = new ArrayList<>();
		if (ladder != null)
		{
			for (GearItem preferred : ladder)
			{
				if (preferred == null)
				{
					continue;
				}
				GearItem existing = find(source, preferred.getName());
				addUnique(ranked, existing != null ? existing : preferred);
			}
		}
		for (GearItem item : source)
		{
			addUnique(ranked, item);
		}
		return ranked;
	}

	private static List<GearItem> mergeWikiFirst(List<GearItem> ladder, List<GearItem> wiki)
	{
		List<GearItem> ranked = new ArrayList<>();
		if (wiki != null)
		{
			for (GearItem item : wiki)
			{
				addUnique(ranked, item);
			}
		}
		if (ladder != null)
		{
			for (GearItem preferred : ladder)
			{
				addUnique(ranked, preferred);
			}
		}
		return ranked;
	}

	private static void addAll(List<GearItem> target, List<GearItem> items)
	{
		if (items == null)
		{
			return;
		}
		for (GearItem item : items)
		{
			addUnique(target, item);
		}
	}

	private static void addUnique(List<GearItem> target, GearItem item)
	{
		if (item == null || item.getName() == null || find(target, item.getName()) != null)
		{
			return;
		}
		target.add(item);
	}

	private static GearItem find(List<GearItem> items, String name)
	{
		if (items == null || name == null)
		{
			return null;
		}
		for (GearItem item : items)
		{
			if (item != null && item.getName() != null && OwnedItemNames.matches(item.getName(), name))
			{
				return item;
			}
		}
		return null;
	}
}
