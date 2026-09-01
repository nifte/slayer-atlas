package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.EquipmentSlot;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.LoadoutSelection;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.PlayerLoadouts;
import com.slayeratlas.data.RankedGearLoadout;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.TaskLoadouts;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import net.runelite.client.ui.ColorScheme;
import org.junit.Test;

public class GearSectionLoadoutTest
{
	@Test
	public void showsSaveUnderInventoryUntilALoadoutIsSaved()
	{
		GearSection section = section(TaskLoadouts.memory(current()));
		assertNotNull(ComponentLookup.named(section, "inventory-panel"));
		JButton save = (JButton) ComponentLookup.named(section, "save-current-loadout");
		assertNotNull(save);
		assertEquals(PanelCopy.SAVE_CURRENT_LOADOUT, save.getText());
		assertNull(ComponentLookup.named(section, "clear-saved-loadout"));
		assertNull(ComponentLookup.named(section, "item-Bronze sword"));
	}

	@Test
	public void saveReplacesTheButtonAndShowsTheCapturedLoadout()
	{
		GearSection section = section(TaskLoadouts.memory(current()));
		((JButton) ComponentLookup.named(section, "save-current-loadout")).doClick();
		assertNull(ComponentLookup.named(section, "save-current-loadout"));
		JButton clear = (JButton) ComponentLookup.named(section, "clear-saved-loadout");
		assertNotNull(clear);
		assertEquals(PanelCopy.CLEAR_SAVED_LOADOUT, clear.getText());
		assertNotNull(ComponentLookup.named(section, "item-Bronze sword"));
		assertNotNull(ComponentLookup.named(section, "item-Trout"));
		JLabel hole = (JLabel) ((java.awt.Container) ComponentLookup.named(section, "inventory-panel"))
			.getComponent(1);
		assertNull(hole.getToolTipText());
	}

	@Test
	public void addsASavedTabNextToStyleTabs()
	{
		GearSection section = section(TaskLoadouts.memory(current()));
		assertNull(ComponentLookup.named(section, "style-tab-saved"));
		((JButton) ComponentLookup.named(section, "save-current-loadout")).doClick();
		JButton saved = (JButton) ComponentLookup.named(section, "style-tab-saved");
		assertNotNull(saved);
		assertEquals(PanelCopy.SAVED_LOADOUT, saved.getText());
		assertNotNull(ComponentLookup.named(section, "style-tab-melee"));
		assertNotNull(ComponentLookup.named(section, "style-tab-ranged"));
		assertNotNull(ComponentLookup.named(section, "style-tab-magic"));
		((JButton) ComponentLookup.named(section, "style-tab-melee")).doClick();
		assertNull(ComponentLookup.named(section, "item-Bronze sword"));
		assertNotNull(ComponentLookup.named(section, "clear-saved-loadout"));
		((JButton) ComponentLookup.named(section, "style-tab-saved")).doClick();
		assertNotNull(ComponentLookup.named(section, "item-Bronze sword"));
	}

	@Test
	public void wrapsFourGearTabsOntoTwoRows()
	{
		GearSection section = section(TaskLoadouts.memory(current()));
		((JButton) ComponentLookup.named(section, "save-current-loadout")).doClick();
		java.awt.Container tabs = (java.awt.Container) ComponentLookup.named(section, "gear-tabs");
		tabs.setSize(220, 80);
		tabs.doLayout();
		JButton melee = (JButton) ComponentLookup.named(tabs, "style-tab-melee");
		JButton ranged = (JButton) ComponentLookup.named(tabs, "style-tab-ranged");
		JButton magic = (JButton) ComponentLookup.named(tabs, "style-tab-magic");
		JButton saved = (JButton) ComponentLookup.named(tabs, "style-tab-saved");
		assertEquals(melee.getY(), ranged.getY());
		assertEquals(magic.getY(), saved.getY());
		assertTrue(magic.getY() > melee.getY());
		assertEquals(melee.getX(), magic.getX());
		assertEquals(ranged.getX(), saved.getX());
	}

	@Test
	public void clearRestoresRecommendationsAndTheSaveButton()
	{
		TaskLoadouts loadouts = TaskLoadouts.memory(current());
		GearSection section = section(loadouts);
		((JButton) ComponentLookup.named(section, "save-current-loadout")).doClick();
		((JButton) ComponentLookup.named(section, "clear-saved-loadout")).doClick();
		assertNotNull(ComponentLookup.named(section, "save-current-loadout"));
		assertNull(ComponentLookup.named(section, "clear-saved-loadout"));
		assertNull(ComponentLookup.named(section, "item-Bronze sword"));
		assertTrue(ComponentLookup.containsText(section, PanelCopy.SAVE_CURRENT_LOADOUT));
	}

	@Test
	public void remembersTheSelectedStyleWhenTheSectionIsReopened()
	{
		LoadoutSelection selection = new LoadoutSelection();
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		GearSection first = new GearSection(
			birds,
			MonsterImageLoader.none(),
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			null,
			(style, prayers) ->
			{
			},
			TaskLoadouts.none(),
			selection);
		((JButton) ComponentLookup.named(first, "style-tab-ranged")).doClick();
		assertEquals(CombatStyle.RANGED, selection.style(birds.getId()));
		assertFalse(selection.saved(birds.getId()));
		assertNotNull(selection.loadout(birds.getId()));
		GearSection reopened = new GearSection(
			birds,
			MonsterImageLoader.none(),
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			null,
			(style, prayers) ->
			{
			},
			TaskLoadouts.none(),
			selection);
		assertSelected(reopened, "style-tab-ranged");
	}

	@Test
	public void keepsAPreviouslySavedLoadoutWhenTheSectionIsReopened()
	{
		TaskLoadouts loadouts = TaskLoadouts.memory(current());
		GearSection first = section(loadouts);
		((JButton) ComponentLookup.named(first, "save-current-loadout")).doClick();
		GearSection reopened = section(loadouts);
		assertNotNull(ComponentLookup.named(reopened, "clear-saved-loadout"));
		assertNotNull(ComponentLookup.named(reopened, "item-Bronze sword"));
		assertNull(ComponentLookup.named(reopened, "save-current-loadout"));
		assertSelected(reopened, "style-tab-saved");
	}

	@Test
	public void keepsTheSavedTabSelectedAfterWikiRecommendationsArrive()
	{
		TaskLoadouts loadouts = TaskLoadouts.memory(current());
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		loadouts.save(birds.getId(), current());
		AtomicReference<Consumer<List<RankedGearLoadout>>> pending = new AtomicReference<>();
		GearSection section = new GearSection(
			birds,
			MonsterImageLoader.none(),
			delayedRanked(pending),
			WikiInventoryClient.none(),
			null,
			(style, prayers) ->
			{
			},
			loadouts);
		assertSelected(section, "style-tab-saved");
		pending.get().accept(List.of(
			new RankedGearLoadout(
				"Slayer task/Birds",
				CombatStyle.RANGED,
				true,
				new EnumMap<>(EquipmentSlot.class),
				List.of())));
		assertSelected(section, "style-tab-saved");
		assertNotNull(ComponentLookup.named(section, "item-Bronze sword"));
	}

	private static void assertSelected(GearSection section, String tabName)
	{
		JButton tab = (JButton) ComponentLookup.named(section, tabName);
		assertNotNull(tab);
		assertEquals(ColorScheme.BRAND_ORANGE, tab.getBackground());
	}

	private static WikiLoadoutClient delayedRanked(AtomicReference<Consumer<List<RankedGearLoadout>>> pending)
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
				pending.set(onLoaded);
			}
		};
	}

	private static GearSection section(TaskLoadouts loadouts)
	{
		SlayerMonster birds = new MonsterDatabase(new Gson()).findByTaskName("Birds");
		return new GearSection(
			birds,
			MonsterImageLoader.none(),
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			null,
			(style, prayers) ->
			{
			},
			loadouts);
	}

	private static GearLoadout current()
	{
		Map<EquipmentSlot, String> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.WEAPON, "Bronze sword");
		List<String> inventory = new ArrayList<>();
		inventory.add("Trout");
		inventory.add(null);
		inventory.add("Trout");
		return PlayerLoadouts.named(CombatStyle.MELEE, worn, inventory);
	}
}
