package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.SlayerAtlasConfig;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.InventoryLoadouts;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.OwnedItems;
import com.slayeratlas.data.SlayerMonster;
import java.util.Set;
import org.junit.Test;

public class GearSectionHeldTest
{
	@Test
	public void greensCarriedRecommendationsAndIgnoresBankOnlyItems()
	{
		GearRecommendationService service = new GearRecommendationService(new SlayerAtlasConfig()
		{
			@Override
			public boolean onlyRecommendOwnedEquipment()
			{
				return false;
			}
		});
		service.setOwnedItems(OwnedItems.withBank(Set.of("Ghrazi rapier", InventoryLoadouts.FOOD)));
		GearSection section = section(service);
		assertEquals(
			ItemSlot.EMPTY_BACKGROUND,
			ComponentLookup.named(section, "item-Ghrazi rapier").getBackground());
		assertEquals(
			ItemSlot.EMPTY_BACKGROUND,
			ComponentLookup.named(section, "item-" + InventoryLoadouts.FOOD).getBackground());

		service.setCarriedItems(OwnedItems.withoutBank(Set.of("Ghrazi rapier", InventoryLoadouts.FOOD)));
		section.refreshRecommendations();
		assertEquals(
			ItemSlot.HELD_BACKGROUND,
			ComponentLookup.named(section, "item-Ghrazi rapier").getBackground());
		assertEquals(
			ItemSlot.HELD_BACKGROUND,
			ComponentLookup.named(section, "item-" + InventoryLoadouts.FOOD).getBackground());
		assertEquals(
			ItemSlot.EMPTY_BACKGROUND,
			ComponentLookup.named(section, "item-Torva platebody").getBackground());
	}

	private static GearSection section(GearRecommendationService service)
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		return new GearSection(
			birds,
			MonsterImageLoader.none(),
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			service,
			null);
	}
}
