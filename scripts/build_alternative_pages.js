const fs = require("fs");
const path = require("path");

const DPS = (id) => `https://tools.runescape.wiki/osrs-dps/?monster=${id}`;

const loc = (id, name, region, x, y, travel, extra = {}) => ({
  id,
  name,
  region,
  x,
  y,
  plane: extra.plane || 0,
  wilderness: Boolean(extra.wild),
  travel,
});

const locations = [
  loc("kbd_lair", "King Black Dragon Lair", "Wilderness", 2271, 4680, [
    "Pull the lever in the Wilderness Dungeon north of the Lava Maze (level 42 Wilderness).",
    "A KBD heads teleport lands in the lair if you have one.",
    "Games necklace to Corporeal Beast, then run north-west to the dungeon.",
    "Bring antifire protection. The entrance is in multi-combat Wilderness.",
  ]),
  loc("cerberus_lair", "Cerberus' Lair", "Asgarnia", 1304, 1290, [
    "Taverley Dungeon to the hellhound area, then enter the iron winch (91 Slayer).",
    "Slayer ring teleport to Taverley Dungeon / Cerberus if unlocked.",
    "Bring three Cerberus crystals or an eternal key to skip the key room.",
  ]),
  loc("callisto_den", "Callisto's den", "Wilderness", 3292, 3844, [
    "Burning amulet to the Lava Maze, then run south-east.",
    "Games necklace to Corporeal Beast and run north-east.",
    "Deep Wilderness multi-boss. Expect PKers.",
  ], { wild: true }),
  loc("artio_den", "Artio's den", "Varlamore", 1769, 11541, [
    "Quetzal transport toward the Hunter Guild, then enter Artio's cave.",
    "Pendant of ates or Civitas teleport, then fly a quetzal west.",
    "Single-way wilderness bear boss alternative to Callisto.",
  ]),
  loc("venenatis_den", "Venenatis' den", "Wilderness", 3318, 3741, [
    "Burning amulet to the Graveyard, then run east.",
    "Games necklace to Corporeal Beast and run east.",
    "Multi-combat Wilderness spider boss.",
  ], { wild: true }),
  loc("spindel_den", "Spindel's cave", "Morytania", 1632, 3298, [
    "Drakan's medallion to Ver Sinhaza, then run west to the Web Chasm.",
    "Fairy ring CKS and run south-east toward Slepe / the chasm.",
    "Single-way Venenatis alternative.",
  ]),
  loc("crash_site_cavern", "Crash Site Cavern", "Karamja", 2026, 5610, [
    "Gnome glider to the Crash Site, then enter the cavern (Monkey Madness II).",
    "Ape Atoll teleport and run to the crash site.",
    "Demonic gorillas require 70 Agility or the MM2 path.",
  ]),
  loc("skotizo_altar", "Skotizo's lair", "Kourend", 1693, 9886, [
    "Use a dark totem on the Catacombs of Kourend altar.",
    "Kourend Castle teleport, statue into the catacombs, then run to the centre.",
    "Each kill consumes a dark totem.",
  ]),
  loc("fight_caves", "TzHaar Fight Cave", "Karamja", 2438, 5168, [
    "Amulet of glory to Karamja, then enter the volcano and the Fight Cave.",
    "Fire cape teleport to Mor Ul Rek if you already have a cape.",
    "TzTok-Jad is the final wave. This is a long instanced cave.",
  ]),
  loc("inferno", "The Inferno", "Karamja", 2271, 5345, [
    "Fire cape to enter Mor Ul Rek, then enter The Inferno.",
    "TzKal-Zuk is the final wave. This is a long, difficult instance.",
  ]),
  loc("scorpia_cave", "Scorpia's cave", "Wilderness", 3232, 3946, [
    "Ghorrock teleport and run south-east, or burning amulet to the Lava Maze and north.",
    "Poisonous Wilderness boss. Bring antipoison and expect PKers.",
  ], { wild: true }),
  loc("obor_arena", "Obor's arena", "Misthalin", 3097, 9824, [
    "Edgeville Dungeon hill giant rooms; use a giant key on the gate.",
    "Glory to Edgeville, down the trapdoor, then south to the giants.",
    "Each kill consumes a giant key.",
  ]),
  loc("bryophyta_lair", "Bryophyta's lair", "Misthalin", 3174, 9898, [
    "Varrock Sewers moss giant area; use a mossy key on the gate.",
    "Varrock teleport, enter the sewers, and run to the moss giants.",
    "Each kill consumes a mossy key.",
  ]),
  loc("royal_titans", "Royal Titans", "Asgarnia", 2980, 9580, [
    "Asgarnian Ice Dungeon (fairy ring AIQ). The Titans are west of the main cavern.",
    "60 Agility shortcut just west of the dungeon ladder.",
    "Giantsoul amulet teleports to the entrance after you own one.",
  ]),
  loc("dagannoth_kings", "Dagannoth Kings", "Fremennik Province", 1913, 4367, [
    "Waterbirth Island dungeon to the kings' lair (pet rock or a team, or the lighthouse path).",
    "Waterbirth teleport or talk to Jarvald in Rellekka.",
    "Prime is magic, Rex is melee, Supreme is ranged.",
  ]),
  loc("kalphite_queen", "Kalphite Queen lair", "Kharidian Desert", 3485, 9509, [
    "Kalphite Lair (two ropes the first time), then down to the Queen chamber.",
    "Desert amulet 4 or fairy ring BIQ and run north-west.",
    "Protect from Magic or ranged depending on her form.",
  ]),
  loc("darkmeyer", "Darkmeyer", "Morytania", 3605, 3254, [
    "Drakan's medallion to Darkmeyer after Sins of the Father.",
    "Vyrewatch Sentinels patrol the city streets.",
    "A slayer's staff or Ivandis flail / blisterwood is required to damage vyres.",
  ]),
  loc("jatizso", "Jatizso ice trolls", "Fremennik Isles", 2398, 3812, [
    "Fremennik Isles after The Fremennik Isles quest; boat from Rellekka.",
    "Ice trolls surround Jatizso. Weiss ice trolls also count.",
  ]),
  loc("porazdir_lair", "Porazdir", "Kourend", 1435, 3671, [
    "Fought during A Kingdom Divided; afterwards check the wiki for the replay location.",
    "Counts as a black demon. Bring demonbane if you have it.",
  ]),
  loc("deathwing_wildy", "Deathwing", "Wilderness", 3330, 3666, [
    "Wilderness bats east of the Graveyard of Shadows include Deathwings.",
    "Burning amulet to the Graveyard, then look along the eastern woods.",
  ], { wild: true }),
  loc("buffalo_herd", "Varlamore buffalo", "Varlamore", 1480, 3100, [
    "Quetzal transport across the Avium Savannah where buffalo graze.",
    "Pendant of ates or Civitas teleport, then fly toward the savanna.",
  ]),
  loc("gwd_zamorak", "Zamorak God Wars", "Troll Country", 2925, 5330, [
    "God Wars Dungeon Zamorak encampment. Wear a Zamorak item.",
    "Trollheim teleport and run north to the hole.",
    "K'ril Tsutsaroth and Balfrug Kreeyath are inside.",
  ], { plane: 2 }),
  loc("gwd_armadyl", "Armadyl God Wars", "Troll Country", 2832, 5296, [
    "God Wars Dungeon Armadyl encampment. Ranged, magic, or a halberd required.",
    "Wear an Armadyl item. 70 Ranged is needed for the dungeon approach.",
  ], { plane: 2 }),
];

const SKILL_REQUIREMENT =
  /^\d+\s+(Attack|Strength|Defence|Defense|Hitpoints|Ranged|Prayer|Magic|Cooking|Woodcutting|Fletching|Fishing|Firemaking|Crafting|Smithing|Mining|Herblore|Agility|Thieving|Slayer|Farming|Runecraft(?:ing)?|Hunter|Construction|Sailing|Combat)\b/i;

function page(combat, dpsId, extra = {}) {
  const override = {
    combatLevelMin: Array.isArray(combat) ? combat[0] : combat,
    combatLevelMax: Array.isArray(combat) ? combat[1] : combat,
  };
  if (dpsId) {
    override.dps = DPS(dpsId);
  }
  const merged = Object.assign(override, extra);
  if (merged.requiredItems) {
    const items = [];
    const levels = [...(merged.requirements || [])];
    for (const item of merged.requiredItems) {
      if (SKILL_REQUIREMENT.test(item.trim())) {
        if (!levels.includes(item)) {
          levels.push(item);
        }
      } else {
        items.push(item);
      }
    }
    merged.requiredItems = items;
    if (levels.length) {
      merged.requirements = levels;
    }
  }
  return merged;
}

const overrides = {
  "Abhorrent spectre": page(253, 7402, {
    notes: "Superior aberrant spectre. Same locations as the task; wear a nose peg or slayer helmet.",
    image: "Abhorrent spectre.png",
  }),
  "Abyssal Sire": page(350, 5886, {
    slayerLevel: 85,
    locationIds: ["sire_lair"],
    recommendedLocationId: "sire_lair",
    attackStyle: "Melee and magic",
    notes: "Instanced abyssal demon boss. Slower than catacombs bursting but much better unique drops.",
    aliases: ["sire"],
  }),
  "Albino bat": page(52, 1039, {
    notes: "Larger bat variant. Counts for bat tasks.",
  }),
  "Alchemical Hydra": page(426, 8615, {
    slayerLevel: 95,
    locationIds: ["hydra_lair"],
    recommendedLocationId: "hydra_lair",
    attackStyle: "Ranged and magic",
    protectionPrayer: "Protect from Missiles or Magic",
    recommendedStyle: "Ranged or melee",
    requiredItems: ["Boots of stone, brimstone, or granite"],
    notes: "Four-phase hydra boss. Swap prayers as she changes colour. Best hydra task money.",
    aliases: ["hydra boss", "alch hydra"],
  }),
  "Ammonite Crab": page(25, 7799, {
    locationIds: ["fossil_island"],
    recommendedLocationId: "fossil_island",
    notes: "AFK crabs on Fossil Island's east beach. Counts for crab tasks.",
  }),
  "Ancient wyvern": page(210, 7795, {
    slayerLevel: 82,
    locationIds: ["wyvern_cave"],
    recommendedLocationId: "wyvern_cave",
    requiredItems: ["Elemental, mind, or ancient wyvern shield"],
    notes: "Highest wyvern. Bring a wyvern shield. Counts for Fossil Island wyvern tasks.",
    image: "Ancient Wyvern.png",
  }),
  "Ancient zygomite": page(109, 7797, {
    locationIds: ["fossil_island"],
    recommendedLocationId: "fossil_island",
    requiredItems: ["Fungicide spray"],
    notes: "Fossil Island zygomite. Finish them with fungicide.",
  }),
  "Araxxor": page(890, 13668, {
    slayerLevel: 92,
    locationIds: ["morytania_spider_cave"],
    recommendedLocationId: "morytania_spider_cave",
    attackStyle: "Melee and ranged",
    notes: "Araxyte boss. Much slower than cave araxytes but far better unique drops. Bring antivenom.",
    aliases: ["araxxor"],
  }),
  "Artio": page(320, 11992, {
    locationIds: ["artio_den"],
    recommendedLocationId: "artio_den",
    notes: "Single-way Callisto alternative in Varlamore. Counts for bear tasks.",
  }),
  "Baby black dragon": page(83, 1871, {
    locationIds: ["black_dragon_taverley", "evil_chicken", "myths_guild"],
    recommendedLocationId: "black_dragon_taverley",
    notes: "Weaker black dragons. Still require antifire protection. Counts for black dragon tasks.",
  }),
  "Baby blue dragon": page(48, 241, {
    locationIds: ["heroes_guild", "taverley_dungeon", "myths_guild"],
    recommendedLocationId: "heroes_guild",
    notes: "Low-combat blue dragons. Antifire still recommended. Counts for blue dragon tasks.",
  }),
  "Baby green dragon": page(48, 5194, {
    locationIds: ["myths_guild", "west_dragons", "east_dragons"],
    recommendedLocationId: "myths_guild",
    notes: "Low-combat green dragons. Wilderness babies are for Krystilia only.",
  }),
  "Baby red dragon": page(48, 244, {
    locationIds: ["myths_guild", "brimhaven_dungeon", "red_dragon_isle"],
    recommendedLocationId: "myths_guild",
    notes: "Low-combat red dragons. Bring antifire protection.",
  }),
  "Balfrug Kreeyath": page(151, 3132, {
    locationIds: ["gwd_zamorak"],
    recommendedLocationId: "gwd_zamorak",
    attackStyle: "Magic",
    protectionPrayer: "Protect from Magic",
    notes: "Zamorak GWD minion. Counts for black demon tasks. Wear a Zamorak item.",
  }),
  "Basilisk Knights": page(204, 9293, {
    slayerLevel: 60,
    locationIds: ["jorunn_cave"],
    recommendedLocationId: "jorunn_cave",
    image: "Basilisk Knight.png",
    requiredItems: ["Mirror shield or slayer helmet"],
    notes: "Jormungand's Prison basilisks. Better loot than regular basilisks. Wear a mirror shield or slayer helmet.",
  }),
  "Basilisk Sentinel": page(358, 9258, {
    slayerLevel: 60,
    locationIds: ["jorunn_cave"],
    recommendedLocationId: "jorunn_cave",
    requiredItems: ["Mirror shield or slayer helmet"],
    notes: "Superior Basilisk Knight. Same prison as the knights.",
    image: "Basilisk Sentinel.png",
  }),
  "Black Heather": page(34, 301, {
    locationIds: ["bandit_camp_wildy"],
    recommendedLocationId: "bandit_camp_wildy",
    notes: "Named Wilderness bandit. Counts for bandit tasks. Bring PvP protection.",
  }),
  "Branda the Fire Queen": page(350, 12596, {
    locationIds: ["royal_titans"],
    recommendedLocationId: "royal_titans",
    attackStyle: "Melee and ranged",
    notes: "Royal Titan. Fight her with Eldric in the Asgarnian Ice Dungeon. Counts for fire giant tasks.",
    aliases: ["royal titans", "branda"],
  }),
  "Bronze dragon": page(131, 270, {
    locationIds: ["brimhaven_dungeon", "catacombs_kourend"],
    recommendedLocationId: "catacombs_kourend",
    requiredItems: ["Anti-dragon shield or dragonfire ward", "Antifire potion"],
    notes: "Weakest metal dragon. Catacombs is safer than Brimhaven. Bring antifire.",
  }),
  "Brutal blue dragon": page(271, 7273, {
    locationIds: ["catacombs_kourend"],
    recommendedLocationId: "catacombs_kourend",
    attackStyle: "Melee, magic, and ranged",
    notes: "Strong catacombs blue dragon. Uses all three styles. Dragonbane and antifire recommended.",
  }),
  "Brutal green dragon": page(227, 2918, {
    locationIds: ["ancient_cavern"],
    recommendedLocationId: "ancient_cavern",
    notes: "Ancient Cavern green dragons. Require Barbarian Firemaking. Bring antifire.",
  }),
  "Brutal red dragon": page(289, 7274, {
    locationIds: ["catacombs_kourend"],
    recommendedLocationId: "catacombs_kourend",
    notes: "Strong catacombs red dragon. Dragonbane and antifire recommended.",
  }),
  "Bryophyta": page(128, 8195, {
    locationIds: ["bryophyta_lair"],
    recommendedLocationId: "bryophyta_lair",
    requiredItems: ["Mossy key"],
    notes: "Moss giant boss in Varrock Sewers. Each kill uses a mossy key. Counts for moss giant tasks.",
  }),
  "Buffalo": page(9, 13004, {
    locationIds: ["buffalo_herd"],
    recommendedLocationId: "buffalo_herd",
    notes: "Varlamore cattle. Counts for cow tasks.",
  }),
  "Callisto": page(470, 6609, {
    locationIds: ["callisto_den"],
    recommendedLocationId: "callisto_den",
    notes: "Wilderness bear boss. Multi-combat. Artio is the safer single-way alternative.",
  }),
  "Cave abomination": page(206, 7401, {
    notes: "Superior cave horror. Wear a witchwood icon. Same Mos Le'Harmless cave as the task.",
  }),
  "Cerberus": page(318, 5862, {
    slayerLevel: 91,
    locationIds: ["cerberus_lair"],
    recommendedLocationId: "cerberus_lair",
    attackStyle: "Melee, magic, and ranged",
    protectionPrayer: "Protect from Magic",
    recommendedStyle: "Melee or ranged",
    requiredItems: ["Three Cerberus crystals or an eternal key"],
    requirements: ["91 Slayer"],
    notes: "Hellhound boss. Prayer-flick her mage hits and keep ghosts off you. Does not count for dog tasks.",
    aliases: ["cerb"],
  }),
  "Chasm Crawler": page(68, 7389, {
    notes: "Superior cave crawler. Same Fremennik Slayer Dungeon / Lumbridge swamp locations.",
  }),
  "Chicken": page(1, 1173, {
    locationIds: ["falador_farm", "lumbridge", "champions_guild"],
    recommendedLocationId: "falador_farm",
    notes: "Counts for bird tasks. Anywhere with chickens works.",
  }),
  "Choke devil": page(264, 7404, {
    requiredItems: ["Face mask or Slayer helmet"],
    notes: "Superior dust devil. Burst in the Catacombs or Smoke Dungeon.",
  }),
  "Cockathrice": page(89, 7393, {
    requiredItems: ["Mirror shield or slayer helmet"],
    notes: "Superior cockatrice. Wear a mirror shield or slayer helmet.",
  }),
  "Cow calf": page(2, 2792, {
    locationIds: ["lumbridge", "falador_farm"],
    recommendedLocationId: "lumbridge",
    notes: "Young cows. Counts for cow tasks.",
  }),
  "Crushing hand": page(45, 7388, {
    notes: "Superior crawling hand. Slayer Tower as usual.",
  }),
  "Dagannoth fledgeling": page(70, 2264, {
    locationIds: ["waterbirth_island", "lighthouse"],
    recommendedLocationId: "lighthouse",
    notes: "Younger dagannoth. Lighthouse basement is the usual spot.",
  }),
  "Dagannoth Prime": page(303, 2266, {
    locationIds: ["dagannoth_kings"],
    recommendedLocationId: "dagannoth_kings",
    attackStyle: "Magic",
    protectionPrayer: "Protect from Magic",
    recommendedStyle: "Ranged",
    notes: "Magic Dagannoth King. Wear magic defence and range him. Counts for dagannoth tasks.",
  }),
  "Dagannoth Rex": page(303, 2267, {
    locationIds: ["dagannoth_kings"],
    recommendedLocationId: "dagannoth_kings",
    attackStyle: "Melee",
    protectionPrayer: "Protect from Melee",
    recommendedStyle: "Magic",
    notes: "Melee Dagannoth King. Safe-spot with magic. Counts for dagannoth tasks.",
  }),
  "Dagannoth Supreme": page(303, 2265, {
    locationIds: ["dagannoth_kings"],
    recommendedLocationId: "dagannoth_kings",
    attackStyle: "Ranged",
    protectionPrayer: "Protect from Missiles",
    recommendedStyle: "Magic",
    notes: "Ranged Dagannoth King. Protect from Missiles. Counts for dagannoth tasks.",
  }),
  "Deathwing": page(83, 1039, {
    locationIds: ["deathwing_wildy"],
    recommendedLocationId: "deathwing_wildy",
    notes: "Wilderness bat. Counts for bat tasks. Krystilia only if you need Wilderness credit.",
  }),
  "Demonic gorilla": page(275, 7144, {
    locationIds: ["crash_site_cavern"],
    recommendedLocationId: "crash_site_cavern",
    attackStyle: "Melee, magic, and ranged",
    protectionPrayer: "Protect from Melee",
    recommendedStyle: "Melee or ranged",
    notes: "Prayer-swap whenever they switch style. Counts for black demon tasks. Requires Monkey Madness II.",
    aliases: ["demonic gorillas"],
    requirements: ["Monkey Madness II"],
  }),
  "Deviant spectres": page(169, 7279, {
    locationIds: ["catacombs_kourend"],
    recommendedLocationId: "catacombs_kourend",
    requiredItems: ["Nose peg or Slayer helmet"],
    notes: "Catacombs spectres. Stronger than Slayer Tower aberrants. Wear face protection.",
    image: "Deviant spectre.png",
  }),
  "Donny the lad": page(34, 302, {
    locationIds: ["bandit_camp_wildy"],
    recommendedLocationId: "bandit_camp_wildy",
    notes: "Named Wilderness bandit. Counts for bandit tasks.",
  }),
  "Dreadborn Araxyte": page(281, 13680, {
    notes: "Superior araxyte. Same Morytania Spider Cave as the task. Bring antivenom.",
  }),
  "Elder Aquanite": page(305, 15502, {
    slayerLevel: 78,
    locationIds: ["ynysdail_cavern"],
    recommendedLocationId: "ynysdail_cavern",
    attackStyle: "Magic",
    protectionPrayer: "Protect from Magic",
    notes: "Superior aquanite. Can disable Protect from Magic if you leave it on too long.",
    image: "Elder aquanite (lure).png",
  }),
  "Elder Chaos druid": page(129, 6607, {
    locationIds: ["chaos_temple_wildy"],
    recommendedLocationId: "chaos_temple_wildy",
    notes: "Wilderness chaos druids. Fast XP but expect PKers. Counts for chaos druid tasks.",
  }),
  "Feral vampyre": page(61, 3237, {
    notes: "Low-level vampyre. Counts for vampyre tasks. A slayer staff / Ivandis / blisterwood is needed for vyres, not ferals.",
    image: "Feral Vampyre.png",
  }),
  "Flight Kilisa": page(159, 3165, {
    locationIds: ["gwd_armadyl"],
    recommendedLocationId: "gwd_armadyl",
    attackStyle: "Melee",
    notes: "Armadyl GWD minion. Counts for aviansie tasks. They fly — use ranged, magic, or a halberd.",
  }),
  "Flockleader Geerin": page(149, 3164, {
    locationIds: ["gwd_armadyl"],
    recommendedLocationId: "gwd_armadyl",
    attackStyle: "Ranged",
    protectionPrayer: "Protect from Missiles",
    notes: "Armadyl GWD minion. Counts for aviansie tasks.",
  }),
  "Frost Crab": page(15, 13789, {
    locationIds: ["ruins_of_tapoyauik"],
    recommendedLocationId: "ruins_of_tapoyauik",
    notes: "Varlamore / sailing frost crabs. Counts for crab tasks.",
  }),
  "Giant bat": page(27, 2834, {
    notes: "Standard large bat. Counts for bat tasks.",
  }),
  "Giant rockslug": page(86, 7392, {
    requiredItems: ["Bag of salt or slayer helmet"],
    notes: "Superior rockslug. Finish with a bag of salt unless your slayer helmet does it.",
  }),
  "Greater abyssal demon": page(342, 7410, {
    notes: "Superior abyssal demon. Same locations as the task. Demonbane recommended.",
  }),
  "Greater Nechryael": page(200, 7278, {
    locationIds: ["catacombs_kourend", "iorwerth_dungeon"],
    recommendedLocationId: "catacombs_kourend",
    notes: "Stronger nechryael. Catacombs bursting is the usual method.",
  }),
  "Grizzly bear": page(21, 2838, {
    notes: "Standard bear. Counts for bear tasks. Callisto and Artio are the boss alternatives.",
  }),
  "Grotesque Guardians": page([248, 328], 7851, {
    slayerLevel: 75,
    locationIds: ["dusk_lair"],
    recommendedLocationId: "dusk_lair",
    attackStyle: "Melee, magic, and ranged",
    requiredItems: ["Brittle key"],
    requirements: ["75 Slayer"],
    notes: "Dawn and Dusk on the Slayer Tower roof. Counts for gargoyle tasks. Bring a rock hammer or slayer helmet.",
    aliases: ["gg", "dusk", "dawn"],
    image: "Dusk.png",
  }),
  "Guard dog": page(44, 114, {
    locationIds: ["brimhaven", "varrock"],
    recommendedLocationId: "brimhaven",
    notes: "Counts for dog tasks. McGrubor's Wood and Brimhaven are common spots.",
  }),
  "Ice troll": page(120, 650, {
    locationIds: ["jatizso", "weiss"],
    recommendedLocationId: "weiss",
    notes: "Jatizso or Weiss. Counts for troll tasks. Weiss is closer with icy basalt.",
  }),
  "Insatiable Bloodveld": page(202, 7397, {
    notes: "Superior bloodveld. Same locations as the task.",
  }),
  "Iorwerth warrior": page(108, 3429, {
    locationIds: ["iorwerth_dungeon", "lletya", "prifddinas"],
    recommendedLocationId: "iorwerth_dungeon",
    notes: "Elf warrior variant. Iorwerth Dungeon is the usual cannon/burst spot.",
    image: "Iorwerth Warrior.png",
  }),
  "Iron dragon": page(189, 272, {
    locationIds: ["brimhaven_dungeon", "catacombs_kourend"],
    recommendedLocationId: "catacombs_kourend",
    requiredItems: ["Anti-dragon shield or dragonfire ward", "Antifire potion"],
    notes: "Mid-tier metal dragon. Dragonbane and antifire. Catacombs is safer than Brimhaven.",
  }),
  "Jackal": page(21, 4185, {
    locationIds: ["al_kharid", "kharidian_lizards"],
    recommendedLocationId: "al_kharid",
    notes: "Desert dogs. Counts for dog tasks.",
  }),
  "K'ril Tsutsaroth": page(650, 3129, {
    locationIds: ["gwd_zamorak"],
    recommendedLocationId: "gwd_zamorak",
    attackStyle: "Melee and magic",
    protectionPrayer: "Protect from Melee",
    notes: "Zamorak GWD boss. Counts for greater demon tasks. Wear a Zamorak item. Demonbane is strong.",
    aliases: ["kril", "k'ril"],
  }),
  "Kalphite Queen": page(333, 963, {
    locationIds: ["kalphite_queen"],
    recommendedLocationId: "kalphite_queen",
    attackStyle: "Melee, magic, and ranged",
    protectionPrayer: "Protect from Magic",
    notes: "Two-form kalphite boss. Swap prayers when she changes form. Counts for kalphite tasks.",
    aliases: ["kq"],
  }),
  "King Black Dragon": page(276, 239, {
    locationIds: ["kbd_lair"],
    recommendedLocationId: "kbd_lair",
    attackStyle: "Melee and magic",
    protectionPrayer: "Protect from Melee",
    recommendedStyle: "Melee or ranged with dragon hunter gear",
    requiredItems: ["Anti-dragon shield or dragonfire ward", "Antifire potion"],
    recommendedPotions: ["Extended super antifire", "Super combat or ranging potion", "Prayer potion"],
    notes: "Does not count for Krystilia. Super antifire lets you drop the shield. Wilderness lever entrance.",
    aliases: ["kbd"],
  }),
  "King kurask": page(295, 7405, {
    requiredItems: ["Leaf-bladed weapon, broad bolts, or magic dart"],
    notes: "Superior kurask. Same Fremennik Slayer Dungeon as the task.",
  }),
  "Kraken": page(291, 496, {
    slayerLevel: 87,
    locationIds: ["kraken_boss"],
    recommendedLocationId: "kraken_boss",
    attackStyle: "Magic",
    protectionPrayer: "Protect from Magic",
    recommendedStyle: "Magic",
    notes: "Instanced cave kraken boss. Disturbed tentacles first, then the boss. Counts for cave kraken tasks.",
  }),
  "Kree'arra": page(580, 3162, {
    locationIds: ["gwd_armadyl"],
    recommendedLocationId: "gwd_armadyl",
    attackStyle: "Ranged and magic",
    protectionPrayer: "Protect from Missiles",
    recommendedStyle: "Ranged",
    notes: "Armadyl GWD boss. Counts for aviansie tasks. They fly — ranged or magic only.",
    aliases: ["kree"],
  }),
  "Lizardman shaman": page(150, 6766, {
    locationIds: ["lizardman_canyon", "lizardman_settlement"],
    recommendedLocationId: "lizardman_canyon",
    attackStyle: "Ranged and magic",
    requiredItems: ["Shayzien helm (5) to ignore poison splats"],
    notes: "Jump and poison-splat mechanics. Shayzien helm (5) skips the poison. Counts for lizardman tasks.",
  }),
  "Long-tailed wyvern": page(152, 7792, {
    locationIds: ["wyvern_cave"],
    recommendedLocationId: "wyvern_cave",
    requiredItems: ["Elemental, mind, or ancient wyvern shield"],
    notes: "Fossil Island wyvern variant. Bring a wyvern shield.",
    image: "Long-tailed Wyvern.png",
  }),
  "Malevolent Mage": page(162, 7396, {
    notes: "Superior Infernal Mage. Slayer Tower as usual. Protect from Magic.",
  }),
  "Mithril dragon": page(304, 2919, {
    locationIds: ["ancient_cavern"],
    recommendedLocationId: "ancient_cavern",
    requiredItems: ["Anti-dragon shield or dragonfire ward", "Antifire potion"],
    notes: "Ancient Cavern. Requires Barbarian Firemaking. Dragonbane and antifire.",
  }),
  "Monstrous basilisk": page(135, 7395, {
    requiredItems: ["Mirror shield or slayer helmet"],
    notes: "Superior basilisk. Fremennik Slayer Dungeon. Wear a mirror shield or slayer helmet.",
  }),
  "Moonlight Cockatrice": page(49, 13030, {
    locationIds: ["nagua_temple", "ruins_of_tapoyauik"],
    recommendedLocationId: "nagua_temple",
    requiredItems: ["Mirror shield or slayer helmet"],
    notes: "Varlamore cockatrice. Wear a mirror shield or slayer helmet.",
  }),
  "Mourner": page(108, 3429, {
    locationIds: ["lletya", "prifddinas"],
    recommendedLocationId: "lletya",
    notes: "Level 108 mourners count as elves. West Ardougne / Lletya depending on quest progress.",
    image: "Mourner.png",
  }),
  "Mutated Bloodveld": page(123, 7276, {
    locationIds: ["meiyerditch_laboratories", "iorwerth_dungeon", "catacombs_kourend"],
    recommendedLocationId: "meiyerditch_laboratories",
    notes: "Preferred bloodveld variant. Meiyerditch Laboratories is the usual cannon spot.",
  }),
  "Mutated zygomite": page(86, 537, {
    locationIds: ["fossil_island"],
    recommendedLocationId: "fossil_island",
    requiredItems: ["Fungicide spray"],
    notes: "Fossil Island zygomite. Finish with fungicide.",
  }),
  "Nechryarch": page(300, 7411, {
    notes: "Superior nechryael. Same locations as the task. Burst in the Catacombs.",
  }),
  "Night beast": page(374, 7409, {
    notes: "Superior dark beast. Same locations as the task.",
  }),
  "Nuclear smoke devil": page(280, 7406, {
    slayerLevel: 93,
    requiredItems: ["Face mask or Slayer helmet"],
    notes: "Superior smoke devil. Same dungeon as the task.",
  }),
  "Obor": page(106, 7416, {
    locationIds: ["obor_arena"],
    recommendedLocationId: "obor_arena",
    requiredItems: ["Giant key"],
    notes: "Hill giant boss. Each kill uses a giant key. Counts for hill giant tasks.",
  }),
  "Pit Scorpion": page(28, 3026, {
    notes: "Smaller scorpion. Counts for scorpion tasks.",
  }),
  "Porazdir": page(235, 7515, {
    locationIds: ["porazdir_lair"],
    recommendedLocationId: "porazdir_lair",
    notes: "Demon from A Kingdom Divided. Counts for black demon tasks. Demonbane recommended.",
    requirements: ["A Kingdom Divided"],
  }),
  "Pyrelord": page(60, 6762, {
    notes: "Superior pyrefiend. Same locations as the task.",
  }),
  "Repugnant spectre": page(335, 7403, {
    requiredItems: ["Nose peg or Slayer helmet"],
    notes: "Superior deviant spectre. Catacombs. Wear face protection.",
  }),
  "Rock Crab": page(13, 100, {
    locationIds: ["rellekka_crabs"],
    recommendedLocationId: "rellekka_crabs",
    notes: "Classic AFK crabs east of Rellekka. Counts for crab tasks.",
  }),
  "Sand Crab": page(15, 5935, {
    locationIds: ["crabclaw_isle", "isle_of_souls"],
    recommendedLocationId: "crabclaw_isle",
    notes: "AFK crabs on Crabclaw Isle or Hosidius shores. Counts for crab tasks.",
  }),
  "Scorpia": page(225, 6615, {
    locationIds: ["scorpia_cave"],
    recommendedLocationId: "scorpia_cave",
    notes: "Wilderness scorpion boss. Poisonous. Bring antipoison. Counts for scorpion tasks.",
  }),
  "Screaming banshee": page(70, 7390, {
    requiredItems: ["Earmuffs or Slayer helmet"],
    notes: "Superior banshee. Slayer Tower. Wear earmuffs or a slayer helmet.",
  }),
  "Screaming twisted banshee": page(144, 7391, {
    requiredItems: ["Earmuffs or Slayer helmet"],
    notes: "Superior twisted banshee. Catacombs. Wear earmuffs or a slayer helmet.",
  }),
  "Seagull": page(2, 1338, {
    locationIds: ["port_sarim"],
    recommendedLocationId: "port_sarim",
    notes: "Counts for bird tasks. Port Sarim docks are full of them.",
  }),
  "Skeleton Hellhound": page([97, 214], 5054, {
    notes: "Vet'ion's hellhounds (Wilderness) or the quest skeleton hellhound. Counts for hellhound tasks.",
  }),
  "Skogre": page(44, 872, {
    locationIds: ["jiggig"],
    recommendedLocationId: "jiggig",
    notes: "Undead ogre at Jiggig after Zogre Flesh Eaters. Counts for ogre tasks.",
  }),
  "Skotizo": page(321, 7286, {
    locationIds: ["skotizo_altar"],
    recommendedLocationId: "skotizo_altar",
    requiredItems: ["Dark totem"],
    notes: "Catacombs altar boss. Each kill uses a dark totem. Counts for black and greater demon tasks.",
  }),
  "Speedy Keith": page(34, 303, {
    locationIds: ["bandit_camp_wildy"],
    recommendedLocationId: "bandit_camp_wildy",
    notes: "Named Wilderness bandit. Counts for bandit tasks.",
  }),
  "Spiked Turoth": page(244, 10397, {
    requiredItems: ["Leaf-bladed weapon, broad bolts, or magic dart"],
    notes: "Superior turoth. Same Fremennik Slayer Dungeon as the task.",
  }),
  "Spindel": page(302, 11998, {
    locationIds: ["spindel_den"],
    recommendedLocationId: "spindel_den",
    notes: "Single-way Venenatis alternative in the Web Chasm. Counts for spider tasks.",
  }),
  "Spiritual mage": page(120, 3161, {
    slayerLevel: 83,
    locationIds: ["god_wars_dungeon"],
    recommendedLocationId: "god_wars_dungeon",
    attackStyle: "Magic",
    protectionPrayer: "Protect from Magic",
    notes: "Best spiritual creature (dragon boots). Requires 83 Slayer. Wear a god item.",
  }),
  "Spiritual ranger": page(115, 3160, {
    slayerLevel: 63,
    locationIds: ["god_wars_dungeon"],
    recommendedLocationId: "god_wars_dungeon",
    attackStyle: "Ranged",
    protectionPrayer: "Protect from Missiles",
    notes: "God Wars spiritual ranger. Wear a god item.",
  }),
  "Spiritual warrior": page(115, 3159, {
    slayerLevel: 68,
    locationIds: ["god_wars_dungeon"],
    recommendedLocationId: "god_wars_dungeon",
    notes: "God Wars spiritual warrior. Wear a god item.",
  }),
  "Spitting wyvern": page(139, 7794, {
    locationIds: ["wyvern_cave"],
    recommendedLocationId: "wyvern_cave",
    requiredItems: ["Elemental, mind, or ancient wyvern shield"],
    notes: "Fossil Island wyvern variant. Bring a wyvern shield.",
    image: "Spitting Wyvern.png",
  }),
  "Steel dragon": page(246, 274, {
    locationIds: ["brimhaven_dungeon", "catacombs_kourend"],
    recommendedLocationId: "catacombs_kourend",
    requiredItems: ["Anti-dragon shield or dragonfire ward", "Antifire potion"],
    notes: "High-defence metal dragon. Dragonbane and antifire. Catacombs is safer than Brimhaven.",
  }),
  "Swamp Crab": page(55, 8297, {
    locationIds: ["mos_leharmless_cave", "isle_of_souls"],
    recommendedLocationId: "isle_of_souls",
    notes: "Higher-level crabs. Counts for crab tasks.",
  }),
  "Taloned wyvern": page(147, 7793, {
    locationIds: ["wyvern_cave"],
    recommendedLocationId: "wyvern_cave",
    requiredItems: ["Elemental, mind, or ancient wyvern shield"],
    notes: "Fossil Island wyvern variant. Bring a wyvern shield.",
    image: "Taloned Wyvern.png",
  }),
  "Terrorbird": page(28, 2064, {
    locationIds: ["stronghold_slayer_cave"],
    recommendedLocationId: "stronghold_slayer_cave",
    notes: "Gnome Stronghold birds. Counts for bird tasks.",
  }),
  "Thermonuclear smoke devil": page(301, 499, {
    slayerLevel: 93,
    locationIds: ["thermomy_lair"],
    recommendedLocationId: "thermomy_lair",
    requiredItems: ["Face mask or Slayer helmet"],
    notes: "Smoke devil boss. Counts for smoke devil tasks. Wear a face mask or slayer helmet.",
    aliases: ["thermo"],
  }),
  "Troll general": page(113, 4120, {
    locationIds: ["trollheim"],
    recommendedLocationId: "trollheim",
    notes: "Stronger troll in Trollheim / Death Plateau. Counts for troll tasks.",
  }),
  "Twisted Banshee": page(89, 7272, {
    locationIds: ["catacombs_kourend"],
    recommendedLocationId: "catacombs_kourend",
    requiredItems: ["Earmuffs or Slayer helmet"],
    notes: "Catacombs banshee. Wear earmuffs or a slayer helmet.",
  }),
  "TzKal-Zuk": page(1400, 7706, {
    locationIds: ["inferno"],
    recommendedLocationId: "inferno",
    attackStyle: "Melee, magic, and ranged",
    notes: "Inferno final boss. Counts for TzHaar tasks. This is a long, difficult instance.",
    aliases: ["zuk"],
  }),
  "TzTok-Jad": page(702, 3127, {
    locationIds: ["fight_caves"],
    recommendedLocationId: "fight_caves",
    attackStyle: "Melee, magic, and ranged",
    protectionPrayer: "Protect from Magic",
    notes: "Fight Cave final boss. Prayer-switch mage and ranged. Counts for TzHaar tasks.",
    aliases: ["jad"],
  }),
  "Undead cow": page(2, 2992, {
    locationIds: ["lumbridge"],
    recommendedLocationId: "lumbridge",
    attribute: "Undead",
    notes: "Undead cows north-west of Lumbridge. Counts for cow tasks. Salve works.",
  }),
  "Venenatis": page(464, 6610, {
    locationIds: ["venenatis_den"],
    recommendedLocationId: "venenatis_den",
    notes: "Wilderness spider boss. Multi-combat. Spindel is the safer single-way alternative.",
  }),
  "Vitreous Jelly": page(206, 7399, {
    notes: "Superior jelly. Same locations as the task.",
  }),
  "Vorkath": page(732, 8059, {
    locationIds: ["vorkath_isle"],
    recommendedLocationId: "vorkath_isle",
    attackStyle: "Magic, ranged, and dragonfire",
    protectionPrayer: "Protect from Magic",
    recommendedStyle: "Ranged or melee with dragon hunter gear",
    requiredItems: ["Anti-dragon shield, dragonfire ward, or dragonfire shield", "Antifire potion"],
    recommendedPotions: ["Extended super antifire", "Ranging or super combat potion", "Prayer potion"],
    notes: "Counts for blue dragon tasks after Dragon Slayer II. Woox-walk acid and pray mage.",
    aliases: ["vork"],
    requirements: ["Dragon Slayer II"],
  }),
  "Vyrewatch Sentinel": page(151, 9756, {
    locationIds: ["darkmeyer"],
    recommendedLocationId: "darkmeyer",
    requiredItems: ["Blisterwood, Ivandis flail, or a slayer's staff (e)"],
    notes: "Darkmeyer vyres. Need blisterwood / Ivandis / slayer staff (e). Counts for vampyre tasks.",
    requirements: ["Sins of the Father"],
  }),
  "Warped Jelly": page(112, 7277, {
    locationIds: ["catacombs_kourend"],
    recommendedLocationId: "catacombs_kourend",
    notes: "Catacombs jelly. Counts for jelly tasks.",
  }),
  "Warped terrorbird": page(96, 12491, {
    locationIds: ["poison_waste_dungeon"],
    recommendedLocationId: "poison_waste_dungeon",
    notes: "Poison Waste dungeon. Counts for warped creature tasks. Requires The Path of Glouphrie.",
    image: "Warped Terrorbird.png",
    requirements: ["The Path of Glouphrie"],
  }),
  "Warped tortoise": page(121, 12490, {
    locationIds: ["poison_waste_dungeon"],
    recommendedLocationId: "poison_waste_dungeon",
    notes: "Poison Waste dungeon. Counts for warped creature tasks.",
    image: "Warped Tortoise.png",
    requirements: ["The Path of Glouphrie"],
  }),
  "Wingman Skree": page(143, 3163, {
    locationIds: ["gwd_armadyl"],
    recommendedLocationId: "gwd_armadyl",
    attackStyle: "Magic",
    protectionPrayer: "Protect from Magic",
    notes: "Armadyl GWD minion. Counts for aviansie tasks.",
  }),
  "Zogre": page(44, 866, {
    locationIds: ["jiggig"],
    recommendedLocationId: "jiggig",
    notes: "Undead ogre at Jiggig after Zogre Flesh Eaters. Brutal arrows or crumble undead help.",
  }),
};

locations.push(
  loc("jorunn_cave", "Jormungand's Prison", "Fremennik Province", 2436, 10445, [
    "Fremennik Slayer Dungeon to the basilisk area, then into Jormungand's Prison (60 Slayer).",
    "Slayer ring teleport to the Fremennik Slayer Dungeon.",
    "Wear a mirror shield or slayer helmet.",
  ]),
);

const out = {
  locations,
  overrides,
};

const dest = path.join(
  __dirname,
  "..",
  "src",
  "main",
  "resources",
  "com",
  "slayeratlas",
  "data",
  "alternative_pages.json",
);
fs.writeFileSync(dest, JSON.stringify(out, null, 2) + "\n");
console.log(`Wrote ${locations.length} locations and ${Object.keys(overrides).length} overrides to ${dest}`);
