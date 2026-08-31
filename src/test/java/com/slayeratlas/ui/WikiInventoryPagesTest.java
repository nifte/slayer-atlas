package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.RankedGearLoadout;
import com.slayeratlas.data.SlayerMonster;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

public class WikiInventoryPagesTest
{
	@Test
	public void fetchesAMonsterStrategyInventoryWhenThereIsNoGearTable()
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		List<String> requested = new ArrayList<>();
		AtomicReference<List<GearItem>> extra = new AtomicReference<>();
		WikiInventoryPages.load(
			(page, onLoaded) ->
			{
				requested.add(page);
				if (page.toLowerCase().endsWith("/strategies"))
				{
					onLoaded.accept(List.of(GearItem.named("Spade")));
					return;
				}
				onLoaded.accept(List.of());
			},
			birds,
			List.of(),
			(ranked, inventory) -> extra.set(inventory));
		assertTrue(requested.get(0).toLowerCase().endsWith("/strategies"));
		assertEquals(1, extra.get().size());
		assertEquals("Spade", extra.get().get(0).getName());
	}

	@Test
	public void stillAttachesInventoriesToWikiGearPages()
	{
		RankedGearLoadout ranked = new RankedGearLoadout(
			"Kraken/Strategies",
			com.slayeratlas.data.CombatStyle.MAGIC,
			true,
			new java.util.EnumMap<>(com.slayeratlas.data.EquipmentSlot.class),
			List.of());
		AtomicReference<List<RankedGearLoadout>> updated = new AtomicReference<>();
		WikiInventoryPages.load(
			(page, onLoaded) -> onLoaded.accept(List.of(GearItem.named("Fishing explosive"))),
			new MonsterDatabase(new Gson()).findNamedPage("Kraken"),
			List.of(ranked),
			(loadouts, extra) ->
			{
				updated.set(loadouts);
				assertTrue(extra.isEmpty());
			});
		assertEquals("Fishing explosive", updated.get().get(0).getWikiInventory().get(0).getName());
	}
}
