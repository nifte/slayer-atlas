package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class DefaultLoadoutAttributesTest
{
	private final MonsterDatabase database = new MonsterDatabase(new Gson());

	@Test
	public void recommendsSalveOnUndeadMelee()
	{
		SlayerMonster spectres = database.findByTaskName("Aberrant spectres");
		assertTrue(UndeadGear.applies(spectres));
		GearLoadout melee = GearLoadouts.forMonster(spectres, List.of()).get(0);
		assertEquals("Salve amulet (ei)", melee.worn(EquipmentSlot.NECK).getName());
		assertEquals("Slayer helmet (i)", melee.worn(EquipmentSlot.HEAD).getName());
		assertEquals("Ghrazi rapier", melee.worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void recommendsGraniteHammerOnGargoyles()
	{
		assertGraniteHammerWithoutInventoryRockHammer(database.findByTaskName("Gargoyles"));
		assertGraniteHammerWithoutInventoryRockHammer(database.findNamedPage("Marble gargoyle"));
	}

	@Test
	public void ownedMaceStillPutsRockHammerInGargoyleInventory()
	{
		SlayerMonster gargoyles = database.findByTaskName("Gargoyles");
		GearLoadout melee = GearLoadouts.forMonster(
			gargoyles,
			List.of(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Inquisitor's mace",
				"Rock hammer",
				"Avernic defender"))))
			.get(0);
		assertEquals("Inquisitor's mace", melee.worn(EquipmentSlot.WEAPON).getName());
		assertTrue(inventoryHas(melee, "Rock hammer"));
	}

	@Test
	public void recommendsLeafBladedWeaponsOnTuroth()
	{
		SlayerMonster turoth = database.findByTaskName("Turoth");
		assertTrue(LeafBladedGear.applies(turoth));
		GearLoadout melee = loadoutFor(turoth, CombatStyle.MELEE);
		assertEquals("Leaf-bladed battleaxe", melee.worn(EquipmentSlot.WEAPON).getName());
		GearLoadout ranged = loadoutFor(turoth, CombatStyle.RANGED);
		assertEquals("Broad bolts", ranged.worn(EquipmentSlot.AMMO).getName());
		GearLoadout magic = loadoutFor(turoth, CombatStyle.MAGIC);
		assertEquals("Slayer's staff (e)", magic.worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void recommendsKerisOnKalphites()
	{
		SlayerMonster kalphites = database.findByTaskName("Kalphites");
		assertTrue(KalphiteGear.applies(kalphites));
		assertEquals(
			"Keris partisan of the sun",
			GearLoadouts.forMonster(kalphites, List.of()).get(0).worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void recommendsHallowedFlailOnVampyres()
	{
		SlayerMonster vampyres = database.findByTaskName("Vampyres");
		SlayerMonster sentinels = database.findNamedPage("Vyrewatch Sentinel");
		assertTrue(VampyreGear.applies(vampyres));
		assertEquals(
			"Hallowed flail",
			GearLoadouts.forMonster(vampyres, List.of()).get(0).worn(EquipmentSlot.WEAPON).getName());
		assertEquals(
			"Hallowed flail",
			GearLoadouts.forMonster(sentinels, List.of()).get(0).worn(EquipmentSlot.WEAPON).getName());
		assertEquals(
			"Hallowed flail",
			GearLoadouts.forMonster(database.findByTaskName("Venators"), List.of())
				.get(0)
				.worn(EquipmentSlot.WEAPON)
				.getName());
		assertEquals(
			"Hallowed flail",
			GearLoadouts.forMonster(database.findNamedPage("Blood-starved venator"), List.of())
				.get(0)
				.worn(EquipmentSlot.WEAPON)
				.getName());
	}

	@Test
	public void ownedOnlyNeverRecommendsAnUnownedWeapon()
	{
		SlayerMonster birds = database.findByTaskName("Birds");
		GearLoadout melee = GearLoadouts.forMonster(
			birds,
			List.of(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of("Abyssal whip", "Dragon defender"))))
			.get(0);
		assertEquals("Abyssal whip", melee.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Dragon defender", melee.worn(EquipmentSlot.SHIELD).getName());
		assertEquals(null, melee.worn(EquipmentSlot.BODY));
	}

	@Test
	public void prefersOwnedSalveOverOwnedTortureOnUndead()
	{
		SlayerMonster spectres = database.findByTaskName("Aberrant spectres");
		GearLoadout melee = GearLoadouts.forMonster(
			spectres,
			List.of(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Salve amulet(ei)",
				"Amulet of torture"))))
			.get(0);
		assertEquals("Salve amulet (ei)", melee.worn(EquipmentSlot.NECK).getName());
	}

	private static void assertGraniteHammerWithoutInventoryRockHammer(SlayerMonster monster)
	{
		assertTrue(CrushWeapons.applies(monster));
		GearLoadout melee = GearLoadouts.forMonster(monster, List.of()).get(0);
		assertEquals("Granite hammer", melee.worn(EquipmentSlot.WEAPON).getName());
		assertFalse(monster.getName() + " inventory still has Rock hammer", inventoryHas(melee, "Rock hammer"));
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

	private static GearLoadout loadoutFor(SlayerMonster monster, CombatStyle style)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			if (loadout.getStyle() == style)
			{
				return loadout;
			}
		}
		throw new AssertionError("No " + style + " loadout");
	}
}
