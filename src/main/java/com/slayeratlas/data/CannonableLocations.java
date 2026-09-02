package com.slayeratlas.data;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Location-level dwarf multicannon rules. Travel text wins when it is explicit;
 * otherwise only catalog IDs with a known OSRS fact are treated as cannonable.
 */
public final class CannonableLocations
{
	private static final Set<String> CANNONABLE = Set.of(
		"abyssal_area",
		"ancient_cavern",
		"asgarnia_ice_dungeon",
		"bandit_camp_wildy",
		"black_dragon_taverley",
		"brimhaven_dungeon",
		"brine_rat_cavern",
		"canifis_ghouls",
		"catacombs_kourend",
		"chaos_temple_wildy",
		"chasm_of_fire",
		"corsair_cove_dungeon",
		"crabclaw_isle",
		"crandor",
		"croc_elid",
		"dark_warrior_fortress",
		"deep_wilderness_dungeon",
		"dorgesh_kaan_south",
		"east_dragons",
		"earth_warrior_tunnel",
		"edgeville_dungeon",
		"feldip_hills",
		"forgotten_cemetery",
		"fossil_island",
		"fremennik_slayer_dungeon",
		"frozen_waste_plateau",
		"giants_den",
		"harpie_karamja",
		"heroes_guild",
		"hill_giant_lair",
		"ice_mountain",
		"iorwerth_dungeon",
		"isle_of_souls",
		"jiggig",
		"jorunn_cave",
		"jungle_horror_shore",
		"kalphite_cave",
		"kalphite_lair",
		"karamja_volcano",
		"karuulm_slayer_dungeon",
		"kharidian_lizards",
		"lava_dragon_isle",
		"lighthouse",
		"lithkren",
		"lizardman_canyon",
		"lizardman_settlement",
		"lumbridge_swamp_caves",
		"lunar_isle",
		"magic_axe_hut",
		"meiyerditch_laboratories",
		"mos_leharmless_cave",
		"mount_karuulm",
		"mourner_tunnels",
		"myths_guild",
		"ogre_enclave",
		"red_dragon_isle",
		"rellekka_crabs",
		"rune_dragons",
		"smoke_devil_dungeon",
		"smoke_dungeon",
		"stronghold_of_security",
		"stronghold_slayer_cave",
		"taverley_dungeon",
		"trollheim",
		"varrock_sewers",
		"waterfall_dungeon",
		"waterbirth_island",
		"weiss",
		"west_dragons",
		"white_wolf_mountain",
		"wilderness_god_wars",
		"wilderness_slayer_cave",
		"wyvern_cave");

	private static final Set<String> NOT_CANNONABLE = Set.of(
		"ancient_prison",
		"artio_den",
		"bryophyta_lair",
		"callisto_den",
		"cerberus_lair",
		"charred_dungeon",
		"crash_site_cavern",
		"dagannoth_kings",
		"dusk_lair",
		"fight_caves",
		"god_wars_dungeon",
		"gwd_armadyl",
		"gwd_zamorak",
		"hydra_lair",
		"inferno",
		"kalphite_queen",
		"kbd_lair",
		"kraken_boss",
		"kraken_cove",
		"obor_arena",
		"porazdir_lair",
		"revenant_caves",
		"royal_titans",
		"scorpia_cave",
		"shellbane_gryphon_cave",
		"slayer_tower",
		"sire_lair",
		"skotizo_altar",
		"spindel_den",
		"thermomy_lair",
		"venenatis_den",
		"vorkath_isle");

	private CannonableLocations()
	{
	}

	public static boolean isCannonable(MonsterLocation location)
	{
		if (location == null)
		{
			return false;
		}
		return isCannonable(location.getId(), location);
	}

	public static boolean isCannonable(String locationId)
	{
		return isCannonable(locationId, null);
	}

	static boolean isCannonable(String locationId, MonsterLocation location)
	{
		Boolean fromText = location == null ? null : fromTravel(location.getTravel());
		if (fromText != null)
		{
			return fromText;
		}
		if (locationId == null || locationId.isEmpty())
		{
			return false;
		}
		if (NOT_CANNONABLE.contains(locationId))
		{
			return false;
		}
		return CANNONABLE.contains(locationId);
	}

	static Boolean fromTravel(List<String> travel)
	{
		if (travel == null || travel.isEmpty())
		{
			return null;
		}
		String text = String.join(" ", travel).toLowerCase(Locale.ROOT);
		if (text.contains("no cannon")
			|| text.contains("not cannon")
			|| text.contains("cannon will not")
			|| text.contains("cannot cannon")
			|| text.contains("can't cannon")
			|| text.contains("solo instance")
			|| text.contains("instanced")
			|| text.contains("single-way"))
		{
			return false;
		}
		if (text.contains("cannonable"))
		{
			return true;
		}
		return null;
	}
}
