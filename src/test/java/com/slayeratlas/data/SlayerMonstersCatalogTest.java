package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class SlayerMonstersCatalogTest
{
	private MonsterDatabase database;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
	}

	@Test
	public void containsEveryWikiSlayerMonsterAndVariant()
	{
		for (String name : wikiSlayerMonsterNames())
		{
			assertNotNull("Missing slayer monster or variant: " + name, pageFor(name));
		}
	}

	@Test
	public void wyrmVariantLocationsMatchTheWiki()
	{
		assertLocations("Wyrm", "karuulm_slayer_dungeon", "wyrmscraig");
		assertLocations("Shadow Wyrm", "karuulm_slayer_dungeon", "wyrmscraig");
		assertLocations("Wyrmling", "neypotzli", "wyrmscraig");
		assertLocations("Lava Strykewyrm", "charred_dungeon", "wyrmscraig");
		assertLocations("Magma strykewyrm", "charred_dungeon", "wyrmscraig");
	}

	@Test
	public void wikiSlayerMonsterLocationsMatchTheList()
	{
		assertLocations("Crawling Hands", "slayer_tower", "meiyerditch_laboratories");
		assertLocations("Cave crawlers", "fremennik_slayer_dungeon", "lumbridge_swamp_caves", "dorgesh_kaan_south", "ruins_of_tapoyauik");
		assertLocations("Rockslugs", "fremennik_slayer_dungeon", "lumbridge_swamp_caves", "dorgesh_kaan_south", "tonali_cavern");
		assertLocations("Basilisks", "fremennik_slayer_dungeon", "jorunn_cave");
		assertLocations("Basilisk Knights", "jorunn_cave");
		assertLocations("Grimy Lizard", "neypotzli", "tonali_cavern");
		assertLocations("Earthen nagua", "tonali_cavern");
		assertLocations("Sulphur nagua", "neypotzli");
		assertLocations("Dark beasts", "iorwerth_dungeon", "mourner_tunnels");
		assertLocations("Venators", "vampyrium");
		assertLocations("Blood-starved venator", "vampyrium");
		assertLocations("Sea mogre", "ardent_ocean");
		assertLocations("Pyrelord", "isle_of_souls", "sisterhood_sanctuary");
		assertLocations("Greater Nechryael", "catacombs_kourend", "iorwerth_dungeon", "wilderness_slayer_cave");
	}

	@Test
	public void karuulmBootsAreOnlyListedWhereTheWikiRequiresThem()
	{
		for (SlayerMonster monster : database.getPages())
		{
			if (visitsKaruulm(monster))
			{
				continue;
			}
			for (String item : monster.getRequiredItems())
			{
				String lower = item.toLowerCase();
				assertFalse(
					monster.getName() + " lists Karuulm boots: " + item,
					lower.contains("boots of stone") || lower.contains("boots of brimstone"));
			}
		}
	}

	@Test
	public void wyrmPagesKeepWeaknessAndNotesOnTopic()
	{
		SlayerMonster wyrms = pageFor("Wyrms");
		assertEquals("Slash.", wyrms.getWeakness());
		assertFalse(wyrms.getNotes().toLowerCase().contains("lava"));
		assertFalse(wyrms.getNotes().toLowerCase().contains("strykewyrm"));
		assertFalse(wyrms.getNotes().toLowerCase().contains("wyrmling"));
		assertFalse(wyrms.getWeakness().toLowerCase().contains("water"));

		SlayerMonster lava = pageFor("Lava Strykewyrm");
		assertFalse(lava.getWeakness().toLowerCase().contains("slash"));
		assertTrue(lava.getWeakness().toLowerCase().contains("water"));
	}

	@Test
	public void caveHorrorsWeaknessIsCombatOnly()
	{
		SlayerMonster caveHorrors = database.findByTaskName("Cave horrors");
		assertEquals("Slash.", caveHorrors.getWeakness());
		String weakness = lower(caveHorrors.getWeakness());
		assertFalse(weakness.contains("protect from"));
		assertFalse(weakness.contains("scream"));
		assertTrue(lower(caveHorrors.getNotes()).contains("scream"));
	}

	@Test
	public void assignmentWeaknessDoesNotMentionProtectionPrayers()
	{
		for (SlayerMonster assignment : database.getMonsters())
		{
			assertFalse(
				assignment.getName() + " weakness mentions a protection prayer: " + assignment.getWeakness(),
				lower(assignment.getWeakness()).contains("protect from"));
		}
	}

	@Test
	public void assignmentWeaknessAndNotesDoNotDescribeAlternatives()
	{
		for (SlayerMonster assignment : database.getMonsters())
		{
			if (assignment.getAlternatives() == null)
			{
				continue;
			}
			String weakness = lower(assignment.getWeakness());
			String notes = lower(assignment.getNotes());
			for (String alternative : assignment.getAlternatives())
			{
				String phrase = topicPhrase(alternative);
				if (phrase.length() < 5)
				{
					continue;
				}
				assertFalse(
					assignment.getName() + " weakness mentions " + alternative,
					weakness.contains(phrase));
				assertFalse(
					assignment.getName() + " notes mention " + alternative,
					notes.contains(phrase));
			}
		}
	}

	private SlayerMonster pageFor(String name)
	{
		SlayerMonster named = database.findNamedPage(name);
		return named != null ? named : database.findByTaskName(name);
	}

	static String[] wikiSlayerMonsterNames()
	{
		return new String[] {
			"Crawling Hand", "Crushing hand",
			"Cave bug",
			"Cave crawler", "Chasm Crawler",
			"Banshee", "Screaming banshee",
			"Twisted Banshee", "Screaming twisted banshee",
			"Cave slime",
			"Rockslug", "Giant rockslug",
			"Desert Lizard",
			"Cockatrice", "Cockathrice",
			"Pyrefiend", "Flaming pyrelord",
			"Pyrelord", "Infernal pyrelord",
			"Mogre", "Mogre (sea)",
			"Harpie Bug Swarm",
			"Wall beast",
			"Killerwatt",
			"Vyrewatch Sentinel",
			"Molanisk",
			"Basilisk", "Monstrous basilisk",
			"Terror dog",
			"Sea Snake Hatchling",
			"Fever spider",
			"Sulphur Lizard", "Grimy Lizard",
			"Infernal Mage", "Malevolent Mage",
			"Brine rat",
			"Sulphur nagua", "Frost nagua", "Amoxliatl", "Earthen nagua",
			"Bloodveld", "Insatiable Bloodveld",
			"Mutated Bloodveld", "Insatiable mutated Bloodveld",
			"Gryphon", "Dire gryphon", "Shellbane gryphon",
			"Jelly", "Vitreous Jelly",
			"Warped Jelly", "Vitreous warped Jelly",
			"Chilled jelly",
			"Juvenile custodian stalker",
			"Turoth", "Spiked Turoth",
			"Warped Terrorbird", "Mutated Terrorbird",
			"Warped Tortoise", "Mutated Tortoise",
			"Mutated zygomite", "Ancient Zygomite",
			"Cave horror", "Cave abomination",
			"Aberrant spectre", "Abhorrent spectre",
			"Deviant spectre", "Repugnant spectre",
			"Basilisk Knight", "Basilisk Sentinel",
			"Wyrm", "Shadow Wyrm", "Wyrmling",
			"Lava strykewyrm", "Magma strykewyrm",
			"Spiritual ranger",
			"Dust devil", "Choke devil",
			"Spitting Wyvern", "Taloned Wyvern", "Long-tailed Wyvern",
			"Mature custodian stalker",
			"Spiritual warrior",
			"Kurask", "King kurask",
			"Skeletal Wyvern",
			"Venator", "Blood-starved venator",
			"Gargoyle", "Marble gargoyle",
			"Grotesque Guardians",
			"Elder custodian stalker", "Ancient Custodian",
			"Brutal black dragon",
			"Aquanite", "Elder aquanite",
			"Nechryael", "Nechryarch",
			"Greater Nechryael",
			"Ancient Wyvern",
			"Spiritual mage",
			"Drake", "Guardian Drake",
			"Abyssal demon", "Greater abyssal demon",
			"Abyssal Sire",
			"Cave kraken", "Kraken",
			"Dark beast", "Night beast",
			"Cerberus",
			"Araxyte", "Dreadborn Araxyte", "Araxxor",
			"Smoke devil", "Nuclear smoke devil",
			"Thermonuclear smoke devil",
			"Hydra", "Colossal Hydra", "Alchemical Hydra"
		};
	}

	private void assertLocations(String name, String... locationIds)
	{
		SlayerMonster monster = pageFor(name);
		assertNotNull("Missing page: " + name, monster);
		assertEquals(name, Arrays.asList(locationIds), monster.getLocationIds());
	}

	private static boolean visitsKaruulm(SlayerMonster monster)
	{
		for (String locationId : monster.getLocationIds())
		{
			if ("karuulm_slayer_dungeon".equals(locationId)
				|| "mount_karuulm".equals(locationId)
				|| "hydra_lair".equals(locationId))
			{
				return true;
			}
		}
		return false;
	}

	private static String topicPhrase(String alternative)
	{
		return alternative.replaceAll("\\s*\\([^)]*\\)", "").trim().toLowerCase();
	}

	private static String lower(String value)
	{
		return value == null ? "" : value.toLowerCase();
	}
}
