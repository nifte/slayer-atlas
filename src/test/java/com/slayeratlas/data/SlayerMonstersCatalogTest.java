package com.slayeratlas.data;

import static org.junit.Assert.assertNotNull;

import com.google.gson.Gson;
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
		for (String name : wikiSlayerMonsters())
		{
			assertNotNull("Missing slayer monster or variant: " + name, pageFor(name));
		}
	}

	private SlayerMonster pageFor(String name)
	{
		SlayerMonster named = database.findNamedPage(name);
		return named != null ? named : database.findByTaskName(name);
	}

	private static String[] wikiSlayerMonsters()
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
}
