package com.slayeratlas.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.SlayerAtlasConfig;
import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.EquipmentSlot;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.OwnedItems;
import com.slayeratlas.data.RankedGearLoadout;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.WikiInventoryText;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JComponent;
import org.junit.Test;

public class GearSectionHintTest
{
	@Test
	public void showsTheOpenBankHintUntilASnapshotExists()
	{
		GearRecommendationService service = new GearRecommendationService(ownedConfig(true));
		GearSection section = section(service);
		JComponent hint = (JComponent) ComponentLookup.named(section, "bank-hint");
		assertNotNull(hint);
		assertNull(hint.getBorder());
		assertTrue(ComponentLookup.containsText(section, PanelCopy.OPEN_BANK_HINT));

		service.setOwnedItems(OwnedItems.withBank(Set.of("Shark")));
		section.refreshRecommendations();
		assertNull(ComponentLookup.named(section, "bank-hint"));
		assertFalse(ComponentLookup.containsText(section, PanelCopy.OPEN_BANK_HINT));
	}

	@Test
	public void hidesTheHintWhenOwnedFilteringIsOff()
	{
		GearRecommendationService service = new GearRecommendationService(ownedConfig(false));
		GearSection section = section(service);
		assertNull(ComponentLookup.named(section, "bank-hint"));
	}

	@Test
	public void appliesAWikiInventoryGridWithAnEmptySlotWithoutCrashing()
	{
		SlayerMonster kraken = new MonsterDatabase(new Gson()).findNamedPage("Kraken");
		List<GearItem> wiki = WikiInventoryText.parse(
			"{{Inventory|align=right|Fishing explosive||Prayer potion"
				+ "|Shark|Shark|Shark|Shark|Shark|Shark|Shark|Shark"
				+ "|Shark|Shark|Shark|Shark|Shark|Shark|Shark|Divine rune pouch}}");
		Map<EquipmentSlot, List<GearItem>> ranks = new EnumMap<>(EquipmentSlot.class);
		ranks.put(EquipmentSlot.WEAPON, List.of(GearItem.named("Trident of the swamp")));
		RankedGearLoadout ranked = new RankedGearLoadout(
			"Kraken/Strategies",
			CombatStyle.MAGIC,
			true,
			ranks,
			List.of());
		GearSection section = new GearSection(
			kraken,
			MonsterImageLoader.none(),
			rankedLoadouts(ranked),
			(page, onLoaded) -> onLoaded.accept(wiki),
			null,
			null);
		assertNotNull(ComponentLookup.named(section, "inventory-panel"));
		assertNotNull(ComponentLookup.named(section, "item-Fishing explosive"));
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

	private static WikiLoadoutClient rankedLoadouts(RankedGearLoadout ranked)
	{
		return new WikiLoadoutClient()
		{
			@Override
			public void load(SlayerMonster monster, Consumer<List<GearLoadout>> onLoaded)
			{
				onLoaded.accept(List.of());
			}

			@Override
			public void loadRanked(SlayerMonster monster, Consumer<List<RankedGearLoadout>> onLoaded)
			{
				onLoaded.accept(List.of(ranked));
			}
		};
	}

	private static SlayerAtlasConfig ownedConfig(boolean onlyOwned)
	{
		return new SlayerAtlasConfig()
		{
			@Override
			public boolean onlyRecommendOwnedEquipment()
			{
				return onlyOwned;
			}
		};
	}
}
