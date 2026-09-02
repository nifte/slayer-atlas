package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class RequiredGearTest
{
	@Test
	public void mapsRequiredItemsToWornSlots()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		assertEquals("Tortugan shield", RequiredGear.cape(database.findByTaskName("Gryphons")).getName());
		assertEquals("Mirror shield", RequiredGear.shield(database.findByTaskName("Basilisks")).getName());
		assertEquals("Lit bug lantern", RequiredGear.shield(database.findByTaskName("Harpie bug swarms")).getName());
		assertEquals("Insulated boots", RequiredGear.feet(database.findByTaskName("Killerwatts")).getName());
		assertEquals("Slayer gloves", RequiredGear.hands(database.findByTaskName("Fever spiders")).getName());
		assertNull(RequiredGear.neck(database.findByTaskName("Cave horrors")));
		assertEquals("Boots of brimstone", RequiredGear.feet(database.findByTaskName("Drakes")).getName());
		assertNull(RequiredGear.feet(database.findNamedPage("Lava Strykewyrm")));
		assertNull(RequiredGear.cape(database.findByTaskName("Dust devils")));
	}

	@Test
	public void wearsWitchwoodIconOnlyWithoutProtectFromMelee()
	{
		SlayerMonster withoutPrayer = new Gson().fromJson(
			"{\"requiredItems\":[\"Witchwood icon\"],\"protectionPrayer\":\"None\"}",
			SlayerMonster.class);
		SlayerMonster withMeleePrayer = new Gson().fromJson(
			"{\"requiredItems\":[\"Witchwood icon\"],\"protectionPrayer\":\"Protect from Melee\"}",
			SlayerMonster.class);
		assertEquals("Witchwood icon", RequiredGear.neck(withoutPrayer).getName());
		assertNull(RequiredGear.neck(withMeleePrayer));
	}

	@Test
	public void replacesATwoHandedWeaponWhenAShieldIsRequired()
	{
		SlayerMonster harpies = new MonsterDatabase(new Gson()).findByTaskName("Harpie bug swarms");
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.WEAPON, GearItem.named("Tumeken's shadow"));
		GearLoadout loadout = RequiredGear.apply(
			new GearLoadout(CombatStyle.MAGIC, true, worn, List.of()),
			harpies);
		assertEquals("Eye of Ayak", loadout.worn(EquipmentSlot.WEAPON).getName());
		assertEquals("Lit bug lantern", loadout.worn(EquipmentSlot.SHIELD).getName());
	}
}
