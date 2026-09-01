package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class OffhandGearTest
{
	@Test
	public void treatsBowsAndScythesAsTwoHanded()
	{
		assertTrue(OffhandGear.isTwoHanded("Venator bow"));
		assertTrue(OffhandGear.isTwoHanded("Bow of faerdhinen"));
		assertTrue(OffhandGear.isTwoHanded("Scorching bow"));
		assertTrue(OffhandGear.isTwoHanded("Hallowfell"));
		assertTrue(OffhandGear.isTwoHanded("Tumeken's shadow"));
		assertTrue(OffhandGear.isTwoHanded("Sanguinesti staff"));
		assertTrue(OffhandGear.isTwoHanded("Trident of the swamp"));
		assertTrue(OffhandGear.isTwoHanded("Scythe of vitur"));
		assertTrue(OffhandGear.isTwoHanded("Twisted bow"));
		assertTrue(OffhandGear.isTwoHanded("Toxic blowpipe"));
	}

	@Test
	public void treatsRapiersCrossbowsAndWandsAsOneHanded()
	{
		assertFalse(OffhandGear.isTwoHanded("Ghrazi rapier"));
		assertFalse(OffhandGear.isTwoHanded("Osmumten's fang"));
		assertFalse(OffhandGear.isTwoHanded("Emberlight"));
		assertFalse(OffhandGear.isTwoHanded("Purging staff"));
		assertFalse(OffhandGear.isTwoHanded("Dragon hunter lance"));
		assertFalse(OffhandGear.isTwoHanded("Dragon hunter crossbow"));
		assertFalse(OffhandGear.isTwoHanded("Dragon hunter wand"));
		assertFalse(OffhandGear.isTwoHanded("Kodai wand"));
		assertFalse(OffhandGear.isTwoHanded("Eye of Ayak"));
		assertFalse(OffhandGear.isTwoHanded("Zaryte crossbow"));
		assertFalse(OffhandGear.isTwoHanded("Volatile Nightmare staff"));
		assertFalse(OffhandGear.isTwoHanded("Slayer's staff (e)"));
	}

	@Test
	public void usesDragonfireOffhandsOnDraconicTasks()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		assertEquals("Dragonfire shield", OffhandGear.forMonster(CombatStyle.MELEE, dragons).getName());
		assertEquals("Dragonfire ward", OffhandGear.forMonster(CombatStyle.RANGED, dragons).getName());
		assertEquals("Ancient wyvern shield", OffhandGear.forMonster(CombatStyle.MAGIC, dragons).getName());
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		assertEquals("Ancient wyvern shield", OffhandGear.forMonster(CombatStyle.MELEE, wyverns).getName());
	}

	@Test
	public void keepsAWikiDefenderInsteadOfForcingDragonfire()
	{
		SlayerMonster frost = new MonsterDatabase(new Gson()).findByTaskName("Frost dragons");
		GearLoadout wiki = new GearLoadout(
			CombatStyle.MELEE,
			true,
			java.util.Map.of(EquipmentSlot.SHIELD, OffhandGear.MELEE),
			List.of());
		assertEquals("Avernic defender", OffhandGear.apply(wiki, frost).worn(EquipmentSlot.SHIELD).getName());
		assertTrue(OffhandGear.prefersWikiRanks(List.of(OffhandGear.MELEE, OffhandGear.DRAGONFIRE_SHIELD)));
		assertFalse(OffhandGear.prefersWikiRanks(List.of(OffhandGear.DRAGONFIRE_SHIELD, OffhandGear.MELEE)));
	}

	@Test
	public void clearsAWardWhenTheWeaponIsTwoHanded()
	{
		GearLoadout shadow = worn(CombatStyle.MAGIC, "Tumeken's shadow", "Elidinis' ward (f)");
		assertNull(OffhandGear.apply(shadow, null).worn(EquipmentSlot.SHIELD));
		assertEquals("Tumeken's shadow", OffhandGear.apply(shadow, null).worn(EquipmentSlot.WEAPON).getName());
	}

	@Test
	public void clearsAShieldOnBowsAndScythes()
	{
		assertNull(OffhandGear.apply(
			worn(CombatStyle.RANGED, "Bow of faerdhinen", "Twisted buckler"),
			null).worn(EquipmentSlot.SHIELD));
		assertNull(OffhandGear.apply(
			worn(CombatStyle.RANGED, "Twisted bow", "Twisted buckler"),
			null).worn(EquipmentSlot.SHIELD));
		assertNull(OffhandGear.apply(
			worn(CombatStyle.MELEE, "Scythe of vitur", "Avernic defender"),
			null).worn(EquipmentSlot.SHIELD));
	}

	@Test
	public void keepsAWardOrDefenderWithAOneHandedWeapon()
	{
		GearLoadout staff = OffhandGear.apply(
			worn(CombatStyle.MAGIC, "Volatile Nightmare staff", "Elidinis' ward (f)"),
			null);
		assertEquals("Elidinis' ward (f)", staff.worn(EquipmentSlot.SHIELD).getName());
		GearLoadout rapier = OffhandGear.apply(
			worn(CombatStyle.MELEE, "Ghrazi rapier", "Avernic defender"),
			null);
		assertEquals("Avernic defender", rapier.worn(EquipmentSlot.SHIELD).getName());
	}

	@Test
	public void doesNotAddAnOffhandAfterATwoHandedWeapon()
	{
		GearLoadout shadow = worn(CombatStyle.MAGIC, "Tumeken's shadow", null);
		assertNull(OffhandGear.apply(shadow, null).worn(EquipmentSlot.SHIELD));
		assertNull(OffhandGear.withoutOffhandIfTwoHanded(shadow).worn(EquipmentSlot.SHIELD));
	}

	private static GearLoadout worn(CombatStyle style, String weapon, String shield)
	{
		Map<EquipmentSlot, GearItem> slots = new EnumMap<>(EquipmentSlot.class);
		slots.put(EquipmentSlot.WEAPON, GearItem.named(weapon));
		if (shield != null)
		{
			slots.put(EquipmentSlot.SHIELD, GearItem.named(shield));
		}
		return new GearLoadout(style, true, slots, List.of());
	}
}
