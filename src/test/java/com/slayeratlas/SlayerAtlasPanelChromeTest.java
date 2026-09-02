package com.slayeratlas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.MonsterLocation;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.map.LocationMapPins;
import com.slayeratlas.ui.MonsterImageLoader;
import com.slayeratlas.ui.PanelCopy;
import com.slayeratlas.ui.SearchFieldSupport;
import com.slayeratlas.ui.WikiLoadoutClient;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import org.junit.Before;
import org.junit.Test;

public class SlayerAtlasPanelChromeTest
{
	private MonsterDatabase database;
	private SlayerAtlasPanel panel;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
		panel = new SlayerAtlasPanel(database, null, new SlayerAtlasConfig()
		{
		});
	}

	@Test
	public void hidesSearchOnMonsterDetail()
	{
		Component searchSlot = ComponentLookup.named(panel, "search-slot");
		assertNotNull(searchSlot);
		assertTrue(searchSlot.isVisible());

		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));

		assertFalse(searchSlot.isVisible());
	}

	@Test
	public void showsSearchAgainAfterLeavingDetail()
	{
		Component searchSlot = ComponentLookup.named(panel, "search-slot");
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		assertFalse(searchSlot.isVisible());

		panel.selectMonster(null);

		assertTrue(searchSlot.isVisible());
	}

	@Test
	public void hidesCurrentTaskUntilAnAssignmentExists()
	{
		Component taskSlot = ComponentLookup.named(panel, "task-slot");
		assertNotNull(taskSlot);
		assertFalse(taskSlot.isVisible());

		panel.setCurrentTask(new CurrentSlayerTask("Skeletal Wyverns", null, 31, 31));
		assertTrue(taskSlot.isVisible());
		assertEquals("Skeletal Wyverns (Current)", ((JTextArea) ComponentLookup.named(panel, "current-task-label")).getText());

		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		assertFalse(taskSlot.isVisible());
		assertEquals("Skeletal Wyverns (0/31)", ((JTextArea) ComponentLookup.named(panel, "detail-header-name")).getText());
	}

	@Test
	public void monsterDetailOpensScrolledToTheTop()
	{
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		JScrollPane detailScroll = (JScrollPane) ComponentLookup.named(panel, "detail-scroll");
		assertNotNull(detailScroll);
		assertEquals(0, detailScroll.getVerticalScrollBar().getValue());
		assertEquals(0, detailScroll.getViewport().getViewPosition().y);
	}

	@Test
	public void keepsGearAndScrollWhenTheRemainingCountChanges() throws Exception
	{
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		flushEdt();

		((JButton) ComponentLookup.named(panel, "style-tab-ranged")).doClick();
		JButton ranged = (JButton) ComponentLookup.named(panel, "style-tab-ranged");
		JButton melee = (JButton) ComponentLookup.named(panel, "style-tab-melee");
		assertEquals(ColorScheme.BRAND_ORANGE, ranged.getBackground());
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, melee.getBackground());

		JScrollPane detailScroll = (JScrollPane) ComponentLookup.named(panel, "detail-scroll");
		panel.setSize(PluginPanel.PANEL_WIDTH + PluginPanel.SCROLLBAR_WIDTH, 400);
		panel.doLayout();
		detailScroll.setSize(panel.getWidth(), 300);
		detailScroll.doLayout();
		detailScroll.getViewport().setViewSize(detailScroll.getViewport().getView().getPreferredSize());
		detailScroll.getVerticalScrollBar().setValue(120);
		int scrolled = detailScroll.getVerticalScrollBar().getValue();
		assertTrue(scrolled > 0);

		panel.setCurrentTask(new CurrentSlayerTask("Skeletal Wyverns", null, 30, 31));
		flushEdt();

		assertEquals("Skeletal Wyverns (1/31)", ((JTextArea) ComponentLookup.named(panel, "detail-header-name")).getText());
		assertSame(detailScroll, ComponentLookup.named(panel, "detail-scroll"));
		assertSame(ranged, ComponentLookup.named(panel, "style-tab-ranged"));
		assertEquals(ColorScheme.BRAND_ORANGE, ranged.getBackground());
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, melee.getBackground());
		assertEquals(scrolled, detailScroll.getVerticalScrollBar().getValue());
	}

	@Test
	public void showOnMapPinsTheSelectedLocation()
	{
		AtomicReference<MonsterLocation> shown = new AtomicReference<>();
		panel.useMapPins(new LocationMapPins(null, null, null)
		{
			@Override
			public void show(MonsterLocation location)
			{
				shown.set(location);
			}
		});
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		Container card = (Container) ComponentLookup.named(panel, "location-asgarnia_ice_dungeon");
		click(ComponentLookup.named(card, "location-name"));
		JButton map = (JButton) ComponentLookup.named(card, "show-on-map");
		map.doClick();
		assertEquals("asgarnia_ice_dungeon", shown.get().getId());
	}

	@Test
	public void configChangesDoNotRebuildTheOpenMonsterPage()
	{
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		Component page = ComponentLookup.named(panel, "detail-scroll");
		Component card = ComponentLookup.named(panel, "location-asgarnia_ice_dungeon");
		assertNotNull(page);
		assertNotNull(card);

		panel.applyConfigChange(SlayerAtlasConfig.KEY_SHOW_BANK_TAB_BUTTON);
		panel.applyConfigChange(SlayerAtlasConfig.KEY_OPEN_PANEL_ON_TASK);
		panel.applyConfigChange(SlayerAtlasConfig.KEY_PREVENT_TAG_TAB_DRAGS);
		panel.applyConfigChange(SlayerAtlasConfig.KEY_ONLY_RECOMMEND_OWNED_EQUIPMENT);

		assertSame(page, ComponentLookup.named(panel, "detail-scroll"));
		assertSame(card, ComponentLookup.named(panel, "location-asgarnia_ice_dungeon"));
	}

	@Test
	public void listTitleIsSlayerAtlas()
	{
		Component title = ComponentLookup.named(panel, "panel-title");
		assertNotNull(title);
		assertTrue(ComponentLookup.containsText(title, "Slayer Atlas"));
	}

	@Test
	public void settingsCogSitsInTheTopRightAndOpensConfiguration()
	{
		AtomicBoolean opened = new AtomicBoolean();
		panel.useConfigOpener(() -> opened.set(true));

		JButton settings = (JButton) ComponentLookup.named(panel, "open-settings");
		assertNotNull(settings);
		assertEquals(PanelCopy.OPEN_SETTINGS, settings.getToolTipText());
		assertSame(
			settings,
			((BorderLayout) ((Container) ComponentLookup.named(panel, "panel-header")).getLayout())
				.getLayoutComponent(BorderLayout.EAST));

		settings.doClick();
		assertTrue(opened.get());
	}

	@Test
	public void settingsCogStaysVisibleOnMonsterDetail()
	{
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		Component settings = ComponentLookup.named(panel, "open-settings");
		assertNotNull(settings);
		assertTrue(settings.isVisible());
	}

	@Test
	public void prefetchesAssignmentIconsBeforeAlternatives()
	{
		RecordingImages images = new RecordingImages();
		new SlayerAtlasPanel(
			database,
			null,
			new SlayerAtlasConfig()
			{
			},
			images,
			null,
			WikiLoadoutClient.none());

		int assignments = database.getMonsters().size();
		assertEquals(database.getMonsters(), images.prefetched.subList(0, assignments));
		assertEquals(database.getPages(), images.prefetched.subList(assignments, images.prefetched.size()));
		assertEquals(database.getMonsters(), images.loaded);
		assertFalse(images.loadedUrgent.contains(true));
	}

	@Test
	public void detailKeepsBackAndWikiInTheHeader()
	{
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		assertTrue(ComponentLookup.containsText(panel, "Back to list"));
		assertTrue(ComponentLookup.containsText(panel, PanelCopy.OPEN_WIKI));
		assertTrue(ComponentLookup.containsText(panel, "Skeletal Wyverns"));
		Container header = (Container) ComponentLookup.named(panel, "detail-header");
		assertNotNull(header);
		assertNotNull(ComponentLookup.named(header, "open-wiki"));
		assertNotNull(ComponentLookup.named(header, "open-dps"));
		assertNull(ComponentLookup.named((Container) ComponentLookup.named(panel, "detail-scroll"), "open-wiki"));
	}

	@Test
	public void enterOpensFirstSearchResult() throws Exception
	{
		JTextField input = searchInput();
		input.setText("dust");
		input.postActionEvent();
		flushEdt();

		assertEquals("Dust devils", panel.getSelected().getName());
		assertNotNull(ComponentLookup.named(panel, "detail-scroll"));
		assertFalse(ComponentLookup.named(panel, "search-slot").isVisible());
	}

	@Test
	public void enterDoesNothingWhenSearchIsEmpty() throws Exception
	{
		searchInput().postActionEvent();
		flushEdt();

		assertNull(panel.getSelected());
		assertNull(ComponentLookup.named(panel, "detail-scroll"));
	}

	@Test
	public void enterDoesNothingWhenSearchHasNoResults() throws Exception
	{
		JTextField input = searchInput();
		input.setText("zzzznotamonster");
		input.postActionEvent();
		flushEdt();

		assertNull(panel.getSelected());
		assertNull(ComponentLookup.named(panel, "detail-scroll"));
	}

	@Test
	public void highlightsTheFirstResultWhileSearching() throws Exception
	{
		List<SlayerMonster> matches = database.search("dragon");
		assertTrue(matches.size() > 1);

		searchInput().setText("dragon");
		flushEdt();

		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(matches.get(0).getId()).getBackground());
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, monsterRow(matches.get(1).getId()).getBackground());
	}

	@Test
	public void highlightsTheFirstResultOnEverySearch() throws Exception
	{
		SlayerMonster dust = database.search("dust").get(0);
		searchInput().setText("dust");
		flushEdt();
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(dust.getId()).getBackground());

		SlayerMonster abyssal = database.search("abyssal").get(0);
		searchInput().setText("abyssal");
		flushEdt();

		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(abyssal.getId()).getBackground());
	}

	@Test
	public void keepsHighlightingTheFirstResultAsTheQueryChanges() throws Exception
	{
		JTextField input = searchInput();
		input.setText("d");
		flushEdt();
		SlayerMonster firstD = database.search("d").get(0);
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(firstD.getId()).getBackground());

		input.setText("du");
		flushEdt();
		SlayerMonster firstDu = database.search("du").get(0);
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(firstDu.getId()).getBackground());
		Component previous = ComponentLookup.named(panel, "monster-" + firstD.getId());
		if (previous != null && !firstD.getId().equals(firstDu.getId()))
		{
			assertEquals(ColorScheme.DARKER_GRAY_COLOR, previous.getBackground());
		}

		input.setText("dust");
		flushEdt();
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(database.search("dust").get(0).getId()).getBackground());
	}

	@Test
	public void doesNotHighlightAResultWhenSearchIsEmpty()
	{
		SlayerMonster first = database.getMonsters().get(0);
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, monsterRow(first.getId()).getBackground());
	}

	@Test
	public void downArrowMovesHighlightAndEnterOpensThatResult() throws Exception
	{
		List<SlayerMonster> matches = database.search("dragon");
		assertTrue(matches.size() > 1);
		searchInput().setText("dragon");
		flushEdt();

		pressSearch(SearchFieldSupport.DOWN_ACTION);

		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(matches.get(1).getId()).getBackground());
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, monsterRow(matches.get(0).getId()).getBackground());

		searchInput().postActionEvent();
		flushEdt();

		assertEquals(matches.get(1).getName(), panel.getSelected().getName());
	}

	@Test
	public void upArrowKeepsTheFirstResultHighlighted() throws Exception
	{
		List<SlayerMonster> matches = database.search("dragon");
		searchInput().setText("dragon");
		flushEdt();

		pressSearch(SearchFieldSupport.UP_ACTION);

		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(matches.get(0).getId()).getBackground());
	}

	@Test
	public void downArrowStopsAtTheLastResult() throws Exception
	{
		List<SlayerMonster> matches = database.search("dragon");
		assertTrue(matches.size() > 1);
		searchInput().setText("dragon");
		flushEdt();

		for (int i = 0; i < matches.size() + 2; i++)
		{
			pressSearch(SearchFieldSupport.DOWN_ACTION);
		}

		assertEquals(
			ColorScheme.DARKER_GRAY_HOVER_COLOR,
			monsterRow(matches.get(matches.size() - 1).getId()).getBackground());
	}

	@Test
	public void typingResetsTheHighlightToTheFirstResult() throws Exception
	{
		searchInput().setText("d");
		flushEdt();
		pressSearch(SearchFieldSupport.DOWN_ACTION);

		searchInput().setText("dr");
		flushEdt();

		SlayerMonster first = database.search("dr").get(0);
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(first.getId()).getBackground());
	}

	@Test
	public void arrowsDoNothingWhenSearchIsEmpty()
	{
		SlayerMonster first = database.getMonsters().get(0);
		pressSearch(SearchFieldSupport.DOWN_ACTION);
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, monsterRow(first.getId()).getBackground());
		assertNull(panel.getSelected());
	}

	@Test
	public void escapeClearsSearchAndRemovesTheHighlight() throws Exception
	{
		JTextField input = searchInput();
		input.setText("dust");
		flushEdt();
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, monsterRow(database.search("dust").get(0).getId()).getBackground());

		pressSearch(SearchFieldSupport.ESCAPE_ACTION);
		flushEdt();

		assertEquals("", input.getText());
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, monsterRow(database.getMonsters().get(0).getId()).getBackground());
	}

	@Test
	public void alternativeReplacesTheSelectedMonster()
	{
		panel.selectMonster(database.findByTaskName("Black dragons"));
		assertEquals("Black dragons", panel.getSelected().getName());

		click(ComponentLookup.named(panel, "alternative-king_black_dragon"));

		assertEquals("King Black Dragon", panel.getSelected().getName());
		assertTrue(ComponentLookup.containsText(panel, "King Black Dragon"));
		assertNotNull(ComponentLookup.named(panel, "detail-scroll"));
	}

	private Component monsterRow(String id)
	{
		Component row = ComponentLookup.named(panel, "monster-" + id);
		assertNotNull(row);
		return row;
	}

	private JTextField searchInput()
	{
		return SearchFieldSupport.findTextField((Container) ComponentLookup.named(panel, "search-bar"));
	}

	private void pressSearch(String action)
	{
		JTextField input = searchInput();
		Action bound = input.getActionMap().get(action);
		assertNotNull(bound);
		bound.actionPerformed(new ActionEvent(input, ActionEvent.ACTION_PERFORMED, action));
	}

	private static void click(Component component)
	{
		assertNotNull(component);
		component.setSize(
			Math.max(component.getPreferredSize().width, 8),
			Math.max(component.getPreferredSize().height, 8));
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

	private static void flushEdt() throws InterruptedException, InvocationTargetException
	{
		SwingUtilities.invokeAndWait(() ->
		{
		});
	}

	private static final class RecordingImages implements MonsterImageLoader
	{
		private final List<SlayerMonster> prefetched = new ArrayList<>();
		private final List<SlayerMonster> loaded = new ArrayList<>();
		private final List<Boolean> loadedUrgent = new ArrayList<>();

		@Override
		public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded)
		{
			load(monster, size, onLoaded, true);
		}

		@Override
		public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded, boolean urgent)
		{
			loaded.add(monster);
			loadedUrgent.add(urgent);
		}

		@Override
		public void prefetch(Iterable<SlayerMonster> monsters)
		{
			for (SlayerMonster monster : monsters)
			{
				prefetched.add(monster);
			}
		}
	}
}
