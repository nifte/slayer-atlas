package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.Test;

public class DragonfireSuppliesTest
{
	private final Gson gson = new Gson();
	private final MonsterDatabase database = new MonsterDatabase(gson);

	@Test
	public void needsAntifireWhenADragonUsesAnOffensiveOffhand()
	{
		SlayerMonster frost = database.findByTaskName("Frost dragons");
		assertTrue(DragonfireSupplies.needsPotion(frost, OffhandGear.MELEE));
		assertTrue(DragonfireSupplies.needsPotion(frost, OffhandGear.RANGED));
		assertFalse(DragonfireSupplies.needsPotion(frost, OffhandGear.DRAGONFIRE_SHIELD));
		assertFalse(DragonfireSupplies.needsPotion(frost, OffhandGear.DRAGONFIRE_WARD));
	}

	@Test
	public void doesNotForceAntifireOnWyvernsOrNonDragons()
	{
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		SlayerMonster birds = database.findByTaskName("Birds");
		assertFalse(DragonfireSupplies.needsPotion(wyverns, OffhandGear.MELEE));
		assertFalse(DragonfireSupplies.needsPotion(birds, OffhandGear.MELEE));
	}

	@Test
	public void superAntifireFullyProtectsRegularAndMetalDragons()
	{
		GearItem superAntifire = GearItem.named("Extended super antifire");
		assertTrue(DragonfireSupplies.potionFullyProtects(database.findByTaskName("Blue dragons"), superAntifire));
		assertTrue(DragonfireSupplies.potionFullyProtects(database.findByTaskName("Metal dragons"), superAntifire));
		assertTrue(DragonfireSupplies.potionFullyProtects(database.findByTaskName("Frost dragons"), superAntifire));
		assertFalse(DragonfireSupplies.potionFullyProtects(
			database.findByTaskName("Blue dragons"),
			GearItem.named("Extended antifire")));
	}

	@Test
	public void superAntifireDoesNotFullyProtectVorkathOrKingBlackDragon()
	{
		GearItem superAntifire = GearItem.named("Extended super antifire");
		assertFalse(DragonfireSupplies.potionFullyProtects(database.findNamedPage("Vorkath"), superAntifire));
		assertFalse(DragonfireSupplies.potionFullyProtects(
			database.findNamedPage("King Black Dragon"),
			superAntifire));
	}

	@Test
	public void doesNotWearADragonfireShieldWhenThePotionFullyProtects()
	{
		for (SlayerMonster monster : database.getPages())
		{
			for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
			{
				GearItem potion = antifire(loadout.getInventory());
				if (potion == null || !DragonfireSupplies.potionFullyProtects(monster, potion))
				{
					continue;
				}
				GearItem shield = loadout.worn(EquipmentSlot.SHIELD);
				assertFalse(
					monster.getName() + " " + loadout.getStyle()
						+ " wears " + name(shield) + " with " + name(potion),
					OffhandGear.isDragonfireOffhand(shield));
			}
		}
	}

	@Test
	public void keepsADragonfireShieldOnVorkathAndKingBlackDragon()
	{
		assertEquals(
			"Dragonfire shield",
			melee(database.findNamedPage("Vorkath")).worn(EquipmentSlot.SHIELD).getName());
		assertEquals(
			"Dragonfire shield",
			melee(database.findNamedPage("King Black Dragon")).worn(EquipmentSlot.SHIELD).getName());
		assertTrue(hasAntifire(melee(database.findNamedPage("Vorkath")).getInventory()));
		assertTrue(hasAntifire(melee(database.findNamedPage("King Black Dragon")).getInventory()));
	}

	@Test
	public void ownedOnlyDropsTheShieldWhenSuperAntifireIsOwned()
	{
		SlayerMonster blues = database.findByTaskName("Blue dragons");
		GearLoadout melee = GearLoadouts.forMonster(
			blues,
			List.of(),
			GearRecommendation.of(true, OwnedItems.withBank(Set.of(
				"Extended super antifire",
				"Avernic defender",
				"Dragonfire shield"))))
			.get(0);
		assertEquals("Avernic defender", melee.worn(EquipmentSlot.SHIELD).getName());
		assertTrue(hasAntifire(melee.getInventory()));
	}

	private static GearLoadout melee(SlayerMonster monster)
	{
		for (GearLoadout loadout : GearLoadouts.forMonster(monster, List.of()))
		{
			if (loadout.getStyle() == CombatStyle.MELEE)
			{
				return loadout;
			}
		}
		throw new AssertionError("No melee loadout for " + monster.getName());
	}

	private static GearItem antifire(List<GearItem> items)
	{
		for (GearItem item : items)
		{
			if (item != null && item.getName() != null
				&& item.getName().toLowerCase(Locale.ROOT).contains("antifire"))
			{
				return item;
			}
		}
		return null;
	}

	private static boolean hasAntifire(List<GearItem> items)
	{
		return antifire(items) != null;
	}

	private static String name(GearItem item)
	{
		return item == null || item.getName() == null ? "none" : item.getName();
	}
}
