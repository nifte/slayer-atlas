package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;

public class RequiredItemsLoadoutTest
{
	private MonsterDatabase database;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
	}

	@Test
	public void wearsTortuganShieldOnGryphonTasks()
	{
		assertWorn(pageFor("Gryphons"), EquipmentSlot.CAPE, "Tortugan shield");
		assertWorn(pageFor("Dire gryphon"), EquipmentSlot.CAPE, "Tortugan shield");
		assertWorn(pageFor("Shellbane gryphon"), EquipmentSlot.CAPE, "Tortugan shield");
		assertEquals("Tortugan shield", pageFor("Gryphons").getRequiredItems().get(0));
		assertEquals("Tortugan shield", pageFor("Shellbane gryphon").getRequiredItems().get(0));
	}

	@Test
	public void doesNotOfferRangedLoadoutsOnGryphonTasks()
	{
		GearLoadout wikiRanged = new GearLoadout(
			CombatStyle.RANGED,
			true,
			java.util.Map.of(EquipmentSlot.WEAPON, GearItem.named("Bow of faerdhinen")),
			List.of());
		for (String name : List.of("Gryphons", "Dire gryphon", "Shellbane gryphon"))
		{
			for (GearLoadout loadout : GearLoadouts.forMonster(pageFor(name), List.of(wikiRanged)))
			{
				assertFalse(name, loadout.getStyle() == CombatStyle.RANGED);
				assertEquals(name, CombatStyle.MELEE, loadout.getStyle());
				assertEquals("Tortugan shield", loadout.worn(EquipmentSlot.CAPE).getName());
			}
		}
	}

	@Test
	public void wearsCombatRequirementsOnTheWornGrid()
	{
		assertWorn(pageFor("Cockatrice"), EquipmentSlot.SHIELD, "Mirror shield");
		assertWorn(pageFor("Basilisks"), EquipmentSlot.SHIELD, "Mirror shield");
		assertWorn(pageFor("Basilisk Knights"), EquipmentSlot.SHIELD, "Mirror shield");
		assertWorn(pageFor("Harpie bug swarms"), EquipmentSlot.SHIELD, "Lit bug lantern");
		assertWorn(pageFor("Killerwatts"), EquipmentSlot.FEET, "Insulated boots");
		assertWorn(pageFor("Fever spiders"), EquipmentSlot.HANDS, "Slayer gloves");
		assertFalse(wornNames(pageFor("Cave horrors")).contains("Witchwood icon"));
		assertWorn(pageFor("Drakes"), EquipmentSlot.FEET, "Boots of brimstone");
	}

	@Test
	public void doesNotPutKaruulmBootsOnNonKaruulmWyrms()
	{
		assertFalse(mentionsKaruulmBoots(pageFor("Lava Strykewyrm")));
		assertFalse(mentionsKaruulmBoots(pageFor("Wyrmling")));
		assertFalse(wornNames(pageFor("Lava Strykewyrm")).contains("Boots of brimstone"));
		assertFalse(wornNames(pageFor("Wyrmling")).contains("Boots of brimstone"));
		assertFalse(wornNames(pageFor("Hellhounds")).contains("Boots of brimstone"));
		assertTrue(wornNames(pageFor("Wyrms")).contains("Boots of brimstone"));
	}

	@Test
	public void putsWikiRequiredToolsInInventory()
	{
		assertInventory(pageFor("Lizards"), "Ice cooler");
		assertInventory(pageFor("Rockslugs"), "Bag of salt");
		assertGargoyleFinisherCoverage(pageFor("Gargoyles"));
		assertInventory(pageFor("Mogres"), "Fishing explosive");
		assertInventory(pageFor("Zygomites"), "Fungicide spray");
		assertInventory(pageFor("Molanisks"), "Slayer bell");
		assertInventory(pageFor("Warped creatures"), "Crystal chime");
		assertInventory(pageFor("Warped terrorbird"), "Crystal chime");
		assertInventory(pageFor("Cave horrors"), "Bullseye lantern");
		assertInventory(pageFor("Araxytes"), "Anti-venom+(4)");
		assertFalse(inventoryNames(pageFor("Sea mogre")).contains("Fishing explosive"));
	}

	@Test
	public void wikiRequiredItemsAppearInGeneratedLoadouts()
	{
		for (String name : SlayerMonstersCatalogTest.wikiSlayerMonsterNames())
		{
			SlayerMonster monster = pageFor(name);
			assertNotNull("Missing slayer monster or variant: " + name, monster);
			assertRequiredItemsRepresented(monster);
		}
	}

	private void assertRequiredItemsRepresented(SlayerMonster monster)
	{
		String required = join(monster.getRequiredItems()).toLowerCase(Locale.ROOT);
		if (required.isEmpty())
		{
			return;
		}
		if (required.contains("tortugan"))
		{
			assertWorn(monster, EquipmentSlot.CAPE, "Tortugan shield");
		}
		if (required.contains("mirror shield") || required.contains("v's shield"))
		{
			assertWorn(monster, EquipmentSlot.SHIELD, "Mirror shield");
		}
		if (required.contains("bug lantern"))
		{
			assertWorn(monster, EquipmentSlot.SHIELD, "Lit bug lantern");
		}
		if (required.contains("insulated boots"))
		{
			assertWorn(monster, EquipmentSlot.FEET, "Insulated boots");
		}
		if (required.contains("slayer gloves"))
		{
			assertWorn(monster, EquipmentSlot.HANDS, "Slayer gloves");
		}
		if (required.contains("witchwood") && !recommendsProtectFromMelee(monster))
		{
			assertWorn(monster, EquipmentSlot.NECK, "Witchwood icon");
		}
		if (required.contains("boots of stone") || required.contains("boots of brimstone"))
		{
			assertWorn(monster, EquipmentSlot.FEET, "Boots of brimstone");
		}
		if (required.contains("ice cooler"))
		{
			assertInventory(monster, "Ice cooler");
		}
		if (required.contains("bag of salt"))
		{
			assertInventory(monster, "Bag of salt");
		}
		if (required.contains("rock hammer") || required.contains("rock thrown"))
		{
			assertGargoyleFinisherCoverage(monster);
		}
		if (required.contains("fishing explosive"))
		{
			assertInventory(monster, "Fishing explosive");
		}
		if (required.contains("fungicide"))
		{
			assertInventory(monster, "Fungicide spray");
		}
		if (required.contains("slayer bell"))
		{
			assertInventory(monster, "Slayer bell");
		}
		if (required.contains("crystal chime"))
		{
			assertInventory(monster, "Crystal chime");
		}
		if (required.contains("light source") || required.contains("bullseye lantern"))
		{
			assertInventory(monster, "Bullseye lantern");
		}
		if (required.contains("spade"))
		{
			assertInventory(monster, "Spade");
		}
		if (required.contains("lockpick"))
		{
			assertInventory(monster, "Lockpick");
		}
		if (required.contains("antipoison") || required.contains("anti-venom") || required.contains("antidote"))
		{
			assertTrue(monster.getName() + " missing antipoison", hasAntipoison(monster));
		}
		if (required.contains("leaf-blad") || required.contains("broad") || required.contains("magic dart"))
		{
			assertTrue(monster.getName() + " missing leaf-bladed setup", hasLeafBladed(monster));
		}
		if (required.contains("blisterwood") || required.contains("ivandis") || required.contains("sunspear")
			|| required.contains("hallowed flail"))
		{
			assertTrue(monster.getName() + " missing vampyre weapon", hasVampyreWeapon(monster));
		}
		if (required.contains("earmuffs") || required.contains("nose peg") || required.contains("face mask")
			|| required.contains("facemask") || required.contains("spiny helmet"))
		{
			assertWorn(monster, EquipmentSlot.HEAD, "Slayer helmet (i)");
		}
		if (required.contains("wyvern shield") || required.contains("elemental") || required.contains("mind shield"))
		{
			assertTrue(monster.getName() + " missing wyvern shield", hasBreathShield(monster));
		}
		if (required.contains("anti-dragon") || required.contains("antifire") || required.contains("dragonfire"))
		{
			assertTrue(monster.getName() + " missing dragonfire protection", hasDragonfire(monster));
		}
	}

	private void assertWorn(SlayerMonster monster, EquipmentSlot slot, String name)
	{
		boolean found = false;
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			GearItem item = loadout.worn(slot);
			if (item != null && name.equals(item.getName()))
			{
				found = true;
				break;
			}
		}
		assertTrue(monster.getName() + " missing worn " + name + " in " + slot, found);
	}

	private void assertInventory(SlayerMonster monster, String name)
	{
		assertTrue(monster.getName() + " missing inventory " + name, inventoryNames(monster).contains(name));
	}

	private void assertGargoyleFinisherCoverage(SlayerMonster monster)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			boolean finisher = CrushWeapons.isGargoyleFinisher(loadout.worn(EquipmentSlot.WEAPON))
				|| CrushWeapons.hasGargoyleFinisher(loadout.getInventory());
			boolean rockHammer = inventoryHas(loadout, "Rock hammer");
			if (finisher)
			{
				assertFalse(
					monster.getName() + " " + loadout.getStyle() + " still has Rock hammer with a gargoyle finisher",
					rockHammer);
			}
			else
			{
				assertTrue(monster.getName() + " " + loadout.getStyle() + " missing inventory Rock hammer", rockHammer);
			}
		}
	}

	private static boolean inventoryHas(GearLoadout loadout, String name)
	{
		for (GearItem item : loadout.getInventory())
		{
			if (item != null && name.equals(item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasAntipoison(SlayerMonster monster)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			for (GearItem item : loadout.getInventory())
			{
				if (item != null && PoisonSupplies.mentionsCure(item.getName()))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasLeafBladed(SlayerMonster monster)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			if (named(loadout.worn(EquipmentSlot.WEAPON), "leaf-blad")
				|| named(loadout.worn(EquipmentSlot.AMMO), "broad")
				|| named(loadout.worn(EquipmentSlot.WEAPON), "slayer's staff"))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasVampyreWeapon(SlayerMonster monster)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			if (named(loadout.worn(EquipmentSlot.WEAPON), "blisterwood")
				|| named(loadout.worn(EquipmentSlot.WEAPON), "ivandis")
				|| named(loadout.worn(EquipmentSlot.WEAPON), "sunspear")
				|| named(loadout.worn(EquipmentSlot.WEAPON), "hallowed"))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasBreathShield(SlayerMonster monster)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			GearItem shield = loadout.worn(EquipmentSlot.SHIELD);
			if (shield != null && OffhandGear.isDragonfireOffhand(shield))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasDragonfire(SlayerMonster monster)
	{
		if (hasBreathShield(monster))
		{
			return true;
		}
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			for (GearItem item : loadout.getInventory())
			{
				if (item != null && item.getName() != null
					&& item.getName().toLowerCase(Locale.ROOT).contains("antifire"))
				{
					return true;
				}
			}
		}
		return false;
	}

	private String wornNames(SlayerMonster monster)
	{
		StringBuilder names = new StringBuilder();
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			for (EquipmentSlot slot : EquipmentSlot.values())
			{
				if (!slot.onWornGrid())
				{
					continue;
				}
				GearItem item = loadout.worn(slot);
				if (item != null && item.getName() != null)
				{
					names.append(item.getName()).append(',');
				}
			}
		}
		return names.toString();
	}

	private String inventoryNames(SlayerMonster monster)
	{
		StringBuilder names = new StringBuilder();
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			for (GearItem item : loadout.getInventory())
			{
				if (item != null && item.getName() != null)
				{
					names.append(item.getName()).append(',');
				}
			}
		}
		return names.toString();
	}

	private static boolean named(GearItem item, String needle)
	{
		return item != null && item.getName() != null
			&& item.getName().toLowerCase(Locale.ROOT).contains(needle);
	}

	private static boolean mentionsKaruulmBoots(SlayerMonster monster)
	{
		for (String item : monster.getRequiredItems())
		{
			String lower = item.toLowerCase(Locale.ROOT);
			if (lower.contains("boots of stone") || lower.contains("boots of brimstone"))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean recommendsProtectFromMelee(SlayerMonster monster)
	{
		return monster.getProtectionPrayer() != null
			&& monster.getProtectionPrayer().toLowerCase(Locale.ROOT).contains("protect from melee");
	}

	private static String join(List<String> values)
	{
		return values == null ? "" : String.join(" ", values);
	}

	private SlayerMonster pageFor(String name)
	{
		SlayerMonster named = database.findNamedPage(name);
		return named != null ? named : database.findByTaskName(name);
	}
}
