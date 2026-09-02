package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.util.Set;
import org.junit.Test;

public class GoadingSuppliesTest
{
	@Test
	public void isViableOnBurstMagicTasks()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		assertTrue(GoadingSupplies.viable(CombatStyle.MAGIC, dust));
		assertFalse(GoadingSupplies.viable(CombatStyle.MELEE, dust));
		assertFalse(GoadingSupplies.viable(CombatStyle.RANGED, dust));
	}

	@Test
	public void isNotViableOnNonBurstTasks()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		assertFalse(GoadingSupplies.viable(CombatStyle.MAGIC, birds));
		assertFalse(GoadingSupplies.include(CombatStyle.MAGIC, birds, withGoading()));
	}

	@Test
	public void includeRequiresTheSettingAndOwnershipWhenFiltering()
	{
		SlayerMonster dust = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		assertFalse(GoadingSupplies.include(CombatStyle.MAGIC, dust, GearRecommendation.specialized()));
		assertTrue(GoadingSupplies.include(CombatStyle.MAGIC, dust, withGoading()));
		assertFalse(GoadingSupplies.include(
			CombatStyle.MAGIC,
			dust,
			GearRecommendation.of(true, true, OwnedItems.withBank(Set.of("Venator bow")))));
		assertTrue(GoadingSupplies.include(
			CombatStyle.MAGIC,
			dust,
			GearRecommendation.of(true, true, OwnedItems.withBank(Set.of("Goading potion(4)")))));
	}

	@Test
	public void treatsVenatorBowsDartsAndChinsAsStackingItems()
	{
		assertTrue(GoadingSupplies.isStackingItem("Venator bow"));
		assertTrue(GoadingSupplies.isStackingItem("Echo venator bow"));
		assertTrue(GoadingSupplies.isStackingItem("Dragon dart"));
		assertTrue(GoadingSupplies.isStackingItem("Red chinchompa"));
		assertFalse(GoadingSupplies.isStackingItem("Venator ring"));
		assertFalse(GoadingSupplies.isStackingItem("Toxic blowpipe"));
		assertFalse(GoadingSupplies.isStackingItem("Prayer potion(4)"));
	}

	private static GearRecommendation withGoading()
	{
		return GearRecommendation.of(false, true, OwnedItems.none());
	}
}
