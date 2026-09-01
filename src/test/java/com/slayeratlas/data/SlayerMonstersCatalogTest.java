package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
}
