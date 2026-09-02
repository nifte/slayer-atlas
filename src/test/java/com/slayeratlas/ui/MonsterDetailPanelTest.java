package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.SlayerAtlasConfig;
import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.EquipmentSlot;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.MonsterLocation;
import com.slayeratlas.data.PlayerLoadouts;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.TaskLoadouts;
import com.slayeratlas.data.UnlockedPrayers;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;
import org.junit.Test;

public class MonsterDetailPanelTest
{
	@Test
	public void omitsRemainingCountForTheCurrentTask()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions());

		assertFalse(ComponentLookup.containsText(panel, "remaining"));
	}

	@Test
	public void omitsWikiAndDpsFromTheScrollableBody()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions());

		assertNull(ComponentLookup.named(panel, "open-wiki"));
		assertNull(ComponentLookup.named(panel, "open-dps"));
		assertNull(ComponentLookup.named(panel, "wiki-dps-buttons"));
	}

	@Test
	public void locationCardsStartCollapsed()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions());

		LocationCard card = (LocationCard) ComponentLookup.named(panel, "location-asgarnia_ice_dungeon");
		assertNotNull(card);
		assertNull(ComponentLookup.named(panel, "path-to-nearest"));
		assertFalse(card.isExpanded());
		assertFalse(ComponentLookup.named(card, "location-details").isVisible());
		assertTrue(ComponentLookup.containsText(panel, "Locations"));
		assertFalse(ComponentLookup.containsText(panel, "Locations & Travel"));
		assertEquals(0, ((JLabel) ((JPanel) panel.getComponent(0)).getComponent(0)).getInsets().top);
	}

	@Test
	public void placesShowOnMapNextToPathHere()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		AtomicReference<MonsterLocation> shown = new AtomicReference<>();
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new CanPathActions()
			{
				@Override
				public void showOnMap(MonsterLocation location)
				{
					shown.set(location);
				}
			});

		LocationCard card = (LocationCard) ComponentLookup.named(panel, "location-asgarnia_ice_dungeon");
		click(ComponentLookup.named(card, "location-name"));
		LocationActionButtons actions = (LocationActionButtons) ComponentLookup.named(card, "location-actions");
		JButton path = (JButton) ComponentLookup.named(card, "path-here");
		JButton map = (JButton) ComponentLookup.named(card, "show-on-map");
		assertEquals(2, actions.getComponentCount());
		assertEquals(0, actions.getComponentZOrder(map));
		assertEquals(1, actions.getComponentZOrder(path));
		assertEquals(PanelCopy.PATH_HERE, path.getText());
		assertEquals(PanelCopy.SHOW_ON_MAP, map.getText());
		assertEquals(path.getPreferredSize().height, map.getPreferredSize().height);
		assertEquals(path.getPreferredSize().height, actions.getPreferredSize().height);
		assertTrue(path.isEnabled());
		assertTrue(map.isEnabled());

		map.doClick();
		assertEquals("asgarnia_ice_dungeon", shown.get().getId());
	}

	@Test
	public void hidesPathHereWhenShortestPathIsUnavailable()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions());

		LocationCard card = (LocationCard) ComponentLookup.named(panel, "location-asgarnia_ice_dungeon");
		click(ComponentLookup.named(card, "location-name"));
		LocationActionButtons actions = (LocationActionButtons) ComponentLookup.named(card, "location-actions");
		assertNull(ComponentLookup.named(card, "path-here"));
		assertNotNull(ComponentLookup.named(card, "show-on-map"));
		assertEquals(1, actions.getComponentCount());
	}

	@Test
	public void refreshPathButtonsTogglesPathHereOnTheExistingCards()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		AtomicBoolean canPath = new AtomicBoolean(false);
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions()
			{
				@Override
				public boolean canPath()
				{
					return canPath.get();
				}
			});

		LocationCard card = (LocationCard) ComponentLookup.named(panel, "location-asgarnia_ice_dungeon");
		LocationActionButtons actions = (LocationActionButtons) ComponentLookup.named(card, "location-actions");
		assertNull(ComponentLookup.named(card, "path-here"));
		assertEquals(1, actions.getComponentCount());

		canPath.set(true);
		panel.refreshPathButtons();

		assertSame(card, ComponentLookup.named(panel, "location-asgarnia_ice_dungeon"));
		assertNotNull(ComponentLookup.named(card, "path-here"));
		assertEquals(2, actions.getComponentCount());

		canPath.set(false);
		panel.refreshPathButtons();

		assertSame(card, ComponentLookup.named(panel, "location-asgarnia_ice_dungeon"));
		assertNull(ComponentLookup.named(card, "path-here"));
		assertEquals(1, actions.getComponentCount());
	}

	@Test
	public void foldsRequirementsIntoNotes()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions());

		assertNotNull(ComponentLookup.named(panel, "notes-section"));
		assertTrue(ComponentLookup.containsText(panel, "Requires 70 Defence."));
		assertTrue(ComponentLookup.containsText(panel, "Back of the Asgarnian Ice Dungeon"));
		assertFalse(hasExactText(panel, "Requirements"));
	}

	@Test
	public void showsSkillLevelsInNotesInsteadOfRequiredItems()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster kraken = database.findByTaskName("Cave kraken");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			kraken,
			database.locationsFor(kraken),
			new NoPathActions());

		assertTrue(ComponentLookup.containsText(panel, "Requires 50 Magic."));
		assertFalse(ComponentLookup.containsText(panel, "• 50 Magic"));
		assertFalse(hasExactText(panel, "Required items"));
	}

	@Test
	public void keepsItemsWhenASkillLevelIsMixedIntoRequiredItems()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster cerberus = database.getMonster("cerberus");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			cerberus,
			database.locationsFor(cerberus),
			new NoPathActions());

		assertTrue(ComponentLookup.containsText(panel, "Requires 91 Slayer."));
		assertTrue(ComponentLookup.containsText(panel, "• Three Cerberus crystals or an eternal key"));
		assertFalse(ComponentLookup.containsText(panel, "• 91 Slayer"));
	}

	@Test
	public void showsAlternativeButtonsUnderNotes()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster bandits = database.findByTaskName("Bandits");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			bandits,
			database.locationsFor(bandits),
			new NoPathActions(),
			database);

		Component notes = ComponentLookup.named(panel, "notes-section");
		Component alternatives = ComponentLookup.named(panel, "alternatives-section");
		assertNotNull(notes);
		assertNotNull(alternatives);
		assertTrue(panel.getComponentZOrder(alternatives) > panel.getComponentZOrder(notes));
		assertNotNull(ComponentLookup.named(panel, "alternative-black_heather"));
		assertNotNull(ComponentLookup.named(panel, "alternative-speedy_keith"));
		assertNotNull(ComponentLookup.named(panel, "alternative-donny_the_lad"));
		assertFalse(ComponentLookup.containsText(panel, "• Black Heather"));
	}

	@Test
	public void opensTheMatchedAlternativeInTheDetailPanel()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster spiders = database.findByTaskName("Spiders");
		AtomicReference<SlayerMonster> opened = new AtomicReference<>();
		MonsterDetailPanel panel = new MonsterDetailPanel(
			spiders,
			database.locationsFor(spiders),
			new NoPathActions()
			{
				@Override
				public void openMonster(SlayerMonster monster)
				{
					opened.set(monster);
				}
			},
			database);

		Component araxyte = ComponentLookup.named(panel, "alternative-araxyte");
		assertNotNull(araxyte);
		click(araxyte);

		assertEquals("Araxytes", opened.get().getName());
	}

	@Test
	public void opensAnAlternativeWithoutATaskPageInTheDetailPanel()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster dragons = database.findByTaskName("Black dragons");
		AtomicReference<SlayerMonster> opened = new AtomicReference<>();
		MonsterDetailPanel panel = new MonsterDetailPanel(
			dragons,
			database.locationsFor(dragons),
			new NoPathActions()
			{
				@Override
				public void openMonster(SlayerMonster monster)
				{
					opened.set(monster);
				}

				@Override
				public void openWiki(SlayerMonster monster)
				{
					throw new AssertionError("alternatives must not open the wiki");
				}
			},
			database);

		click(ComponentLookup.named(panel, "alternative-king_black_dragon"));
		assertEquals("King Black Dragon", opened.get().getName());

		click(ComponentLookup.named(panel, "alternative-baby_black_dragon"));
		assertEquals("Baby black dragon", opened.get().getName());
	}

	@Test
	public void opensTheKrakenBossAlternativeWithoutCrashing()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster cave = database.findByTaskName("Cave kraken");
		AtomicReference<SlayerMonster> opened = new AtomicReference<>();
		MonsterDetailPanel cavePanel = new MonsterDetailPanel(
			cave,
			database.locationsFor(cave),
			new NoPathActions()
			{
				@Override
				public void openMonster(SlayerMonster monster)
				{
					opened.set(monster);
				}
			},
			database);
		assertNotNull(ComponentLookup.named(cavePanel, "location-kraken_cove"));
		assertNull(ComponentLookup.named(cavePanel, "location-kraken_boss"));
		click(ComponentLookup.named(cavePanel, "alternative-kraken"));
		SlayerMonster kraken = opened.get();
		assertEquals("Kraken", kraken.getName());

		MonsterDetailPanel boss = new MonsterDetailPanel(
			kraken,
			database.locationsFor(kraken),
			new NoPathActions(),
			database);
		assertNotNull(ComponentLookup.named(boss, "location-kraken_boss"));
		assertNotNull(ComponentLookup.named(boss, "gear-section"));
		assertNotNull(ComponentLookup.named(boss, "inventory-panel"));
	}

	@Test
	public void alternativePagesShowTheirOwnLocations()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster kbd = database.getMonster("king_black_dragon");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			kbd,
			database.locationsFor(kbd),
			new NoPathActions(),
			database);

		assertNotNull(ComponentLookup.named(panel, "location-kbd_lair"));
		assertTrue(ComponentLookup.containsText(panel, "King Black Dragon Lair"));
		assertEquals(Integer.valueOf(276), kbd.getCombatLevelMin());
	}

	@Test
	public void showsPrayIconsInsteadOfAttackStyleBlurb()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions());

		assertNotNull(ComponentLookup.named(panel, "pray-section"));
		assertEquals(
			"Protect from Melee",
			((JLabel) ComponentLookup.named(panel, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Piety",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());
		assertFalse(ComponentLookup.containsText(panel, "Attack Style"));
		assertFalse(ComponentLookup.containsText(panel, "They attack with"));
		assertFalse(ComponentLookup.containsText(panel, "Assigned By"));
		assertFalse(ComponentLookup.containsText(panel, "Recommended Equipment"));
		assertNotNull(ComponentLookup.named(panel, "gear-section"));
		assertNotNull(ComponentLookup.named(panel, "equipment-panel"));
		assertNotNull(ComponentLookup.named(panel, "inventory-panel"));
		assertEquals(
			PanelCopy.SAVE_CURRENT_LOADOUT,
			((JButton) ComponentLookup.named(panel, "save-current-loadout")).getText());
		assertEquals(
			"Slayer helmet (i)",
			((JLabel) ComponentLookup.named(panel, "item-Slayer helmet (i)")).getToolTipText());
		assertTrue(ComponentLookup.named(panel, "gear-tabs").isVisible());
		assertTrue(hasExactText(panel, "Weaknesses"));
		assertTrue(hasExactText(panel, "Recommended Prayers"));
		assertTrue(hasExactText(panel, "Recommended Gear"));
		assertFalse(hasExactText(panel, "Weakness"));
		assertFalse(hasExactText(panel, "Pray"));
		assertFalse(hasExactText(panel, "Gear"));
	}

	@Test
	public void showsCombatPrayerWhenNoProtectionIsNeeded()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster birds = database.findByTaskName("Birds");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			birds,
			database.locationsFor(birds),
			new NoPathActions());

		assertNotNull(ComponentLookup.named(panel, "pray-section"));
		assertNull(ComponentLookup.named(panel, "pray-icon-0"));
		assertEquals(
			"Piety",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());
	}

	@Test
	public void updatesCombatPrayerWhenGearStyleChanges()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions());

		assertEquals(
			"Piety",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());
		((JButton) ComponentLookup.named(panel, "style-tab-ranged")).doClick();
		assertEquals(
			"Rigour",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());
		assertEquals(
			"Protect from Melee",
			((JLabel) ComponentLookup.named(panel, "pray-icon-0")).getToolTipText());
	}

	@Test
	public void showsAuguryForMagicGear()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster kraken = database.findByTaskName("Cave kraken");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			kraken,
			database.locationsFor(kraken),
			new NoPathActions());

		assertEquals(
			"Protect from Magic",
			((JLabel) ComponentLookup.named(panel, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Augury",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());
	}

	@Test
	public void showsMultiplePrayIconsForAlternatingAttacks()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster hydras = database.findByTaskName("Hydras");
		MonsterDetailPanel panel = new MonsterDetailPanel(
			hydras,
			database.locationsFor(hydras),
			new NoPathActions());

		assertEquals(
			"Protect from Missiles",
			((JLabel) ComponentLookup.named(panel, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Protect from Magic",
			((JLabel) ComponentLookup.named(panel, "pray-icon-1")).getToolTipText());
		assertEquals(
			"Piety",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());
	}

	@Test
	public void opensATaskOnTheSavedLoadoutTab()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster birds = database.findByTaskName("Birds");
		TaskLoadouts loadouts = TaskLoadouts.memory(savedLoadout());
		loadouts.save(birds.getId(), savedLoadout());
		MonsterDetailPanel panel = new MonsterDetailPanel(
			birds,
			database.locationsFor(birds),
			new NoPathActions(),
			null,
			MonsterImageLoader.none(),
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			null,
			database,
			loadouts);

		JButton saved = (JButton) ComponentLookup.named(panel, "style-tab-saved");
		assertNotNull(saved);
		assertEquals(ColorScheme.BRAND_ORANGE, saved.getBackground());
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, ComponentLookup.named(panel, "style-tab-melee").getBackground());
		assertNotNull(ComponentLookup.named(panel, "item-Bronze sword"));
	}

	@Test
	public void showsProtectionAndWeaponPrayersOnTheSavedLoadoutTab()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		GearLoadout saved = PlayerLoadouts.named(
			CombatStyle.RANGED,
			Map.of(EquipmentSlot.WEAPON, "Twisted bow"),
			List.of("Trout"));
		TaskLoadouts loadouts = TaskLoadouts.memory(saved);
		loadouts.save(wyverns.getId(), saved);
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions(),
			null,
			MonsterImageLoader.none(),
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			null,
			database,
			loadouts);

		assertEquals(
			"Protect from Melee",
			((JLabel) ComponentLookup.named(panel, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Rigour",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());

		((JButton) ComponentLookup.named(panel, "style-tab-melee")).doClick();
		assertEquals(
			"Protect from Melee",
			((JLabel) ComponentLookup.named(panel, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Piety",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());

		((JButton) ComponentLookup.named(panel, "style-tab-saved")).doClick();
		assertEquals(
			"Protect from Melee",
			((JLabel) ComponentLookup.named(panel, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Rigour",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());
	}

	@Test
	public void filtersSavedLoadoutCombatPrayerWhenOnlyUnlockedIsOn()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		GearLoadout saved = PlayerLoadouts.named(
			CombatStyle.RANGED,
			Map.of(EquipmentSlot.WEAPON, "Twisted bow"),
			List.of("Trout"));
		TaskLoadouts loadouts = TaskLoadouts.memory(saved);
		loadouts.save(wyverns.getId(), saved);
		GearRecommendationService service = new GearRecommendationService(new SlayerAtlasConfig()
		{
			@Override
			public boolean onlyRecommendUnlockedPrayers()
			{
				return true;
			}
		});
		service.setUnlockedPrayers(UnlockedPrayers.known(70, 70, true, false, false, false, false));
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			new NoPathActions(),
			null,
			MonsterImageLoader.none(),
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			service,
			database,
			loadouts);

		assertEquals(
			"Protect from Melee",
			((JLabel) ComponentLookup.named(panel, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Eagle Eye",
			((JLabel) ComponentLookup.named(panel, "combat-pray-icon")).getToolTipText());
	}

	@Test
	public void sectionHeadingsAreTitleCaseAndBold()
	{
		JPanel section = PanelWidgets.section("Recommended Prayers");
		JLabel heading = (JLabel) section.getComponent(0);
		assertEquals("Recommended Prayers", heading.getText());
		assertEquals(PanelFonts.bodyBold(), heading.getFont());
		assertFalse("PRAY".equals(heading.getText()));
		assertTrue(heading.getForeground().equals(ColorScheme.BRAND_ORANGE));
		assertEquals(16, heading.getInsets().top);
	}

	private static boolean hasExactText(Component root, String text)
	{
		if (root instanceof JLabel && text.equals(((JLabel) root).getText()))
		{
			return true;
		}
		if (root instanceof Container)
		{
			for (Component child : ((Container) root).getComponents())
			{
				if (hasExactText(child, text))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static void click(Component component)
	{
		component.setSize(Math.max(component.getPreferredSize().width, 8), Math.max(component.getPreferredSize().height, 8));
		component.dispatchEvent(new MouseEvent(
			component,
			MouseEvent.MOUSE_RELEASED,
			System.currentTimeMillis(),
			0,
			1,
			1,
			1,
			false,
			MouseEvent.BUTTON1));
	}

	private static GearLoadout savedLoadout()
	{
		Map<EquipmentSlot, String> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.WEAPON, "Bronze sword");
		List<String> inventory = new ArrayList<>();
		inventory.add("Trout");
		return PlayerLoadouts.named(CombatStyle.MELEE, worn, inventory);
	}

	private static class NoPathActions implements MonsterDetailPanel.Actions
	{
		@Override
		public void pathTo(MonsterLocation location)
		{
		}

		@Override
		public void showOnMap(MonsterLocation location)
		{
		}

		@Override
		public void openWiki(SlayerMonster monster)
		{
		}

		@Override
		public void openDps(SlayerMonster monster)
		{
		}

		@Override
		public void openMonster(SlayerMonster monster)
		{
		}

		@Override
		public boolean canPath()
		{
			return false;
		}
	}

	private static class CanPathActions extends NoPathActions
	{
		@Override
		public boolean canPath()
		{
			return true;
		}
	}
}
