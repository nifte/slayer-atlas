package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import org.junit.Test;

public class DragonfireSuppliesTest
{
	@Test
	public void needsAntifireWhenADragonUsesAnOffensiveOffhand()
	{
		SlayerMonster frost = new MonsterDatabase(new Gson()).findByTaskName("Frost dragons");
		assertTrue(DragonfireSupplies.needsPotion(frost, OffhandGear.MELEE));
		assertTrue(DragonfireSupplies.needsPotion(frost, OffhandGear.RANGED));
		assertFalse(DragonfireSupplies.needsPotion(frost, OffhandGear.DRAGONFIRE_SHIELD));
		assertFalse(DragonfireSupplies.needsPotion(frost, OffhandGear.DRAGONFIRE_WARD));
	}

	@Test
	public void doesNotForceAntifireOnWyvernsOrNonDragons()
	{
		Gson gson = new Gson();
		SlayerMonster wyverns = new MonsterDatabase(gson).findByTaskName("Skeletal Wyverns");
		SlayerMonster birds = new MonsterDatabase(gson).findByTaskName("Birds");
		assertFalse(DragonfireSupplies.needsPotion(wyverns, OffhandGear.MELEE));
		assertFalse(DragonfireSupplies.needsPotion(birds, OffhandGear.MELEE));
	}
}
