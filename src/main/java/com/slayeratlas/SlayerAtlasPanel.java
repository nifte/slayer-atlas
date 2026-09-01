package com.slayeratlas;

import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.FavoriteTasks;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.LoadoutSelection;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.MonsterLocation;
import com.slayeratlas.data.OwnedItems;
import com.slayeratlas.data.SelectedMonster;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.TaskLoadouts;
import com.slayeratlas.data.TaskMatcher;
import com.slayeratlas.data.UnlockedPrayers;
import com.slayeratlas.map.LocationMapPins;
import com.slayeratlas.path.LocationPath;
import com.slayeratlas.path.ShortestPathService;
import com.slayeratlas.ui.CurrentTaskVisibility;
import com.slayeratlas.ui.DpsCalculatorUrl;
import com.slayeratlas.ui.MonsterDetailHeader;
import com.slayeratlas.ui.MonsterDetailPanel;
import com.slayeratlas.ui.MonsterImageLoader;
import com.slayeratlas.ui.MonsterListItem;
import com.slayeratlas.ui.PanelCopy;
import com.slayeratlas.ui.PanelWidgets;
import com.slayeratlas.ui.ScrollReset;
import com.slayeratlas.ui.SearchBarVisibility;
import com.slayeratlas.ui.SearchFieldSupport;
import com.slayeratlas.ui.TaskStatusPanel;
import com.slayeratlas.ui.WikiInventoryClient;
import com.slayeratlas.ui.WikiLoadoutClient;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.LinkBrowser;

@Singleton
public class SlayerAtlasPanel extends PluginPanel implements MonsterDetailPanel.Actions
{
	private final MonsterDatabase database;
	private final ShortestPathService shortestPathService;
	private final SlayerAtlasConfig config;
	private final MonsterImageLoader images;
	private final SpriteManager sprites;
	private final WikiLoadoutClient loadouts;
	private final WikiInventoryClient inventory;
	private final GearRecommendationService recommendations;
	private final TaskLoadouts taskLoadouts;
	private final FavoriteTasks favorites;
	private final LoadoutSelection loadoutSelection;
	private final SelectedMonster selectedMonster;
	private final IconTextField searchBar = new IconTextField();
	private final TaskStatusPanel taskStatus;
	private final JPanel top = PanelWidgets.vertical();
	private final JPanel searchSlot = new JPanel(new BorderLayout());
	private final JPanel taskSlot = new JPanel(new BorderLayout());
	private final JPanel content = new JPanel(new BorderLayout());
	private final JPanel listPanel = PanelWidgets.vertical();
	private final JScrollPane listScroll;
	private final Map<String, MonsterListItem> listItems = new LinkedHashMap<>();
	private CurrentSlayerTask currentTask = new CurrentSlayerTask(null, null, 0, 0);
	private SlayerMonster selected;
	private MonsterDetailHeader detailHeader;
	private MonsterDetailPanel detailPanel;
	private boolean showingDetail;
	private boolean ignoreSearchEvents;
	private boolean listRefreshScheduled;
	private String visibleQuery;
	private List<SlayerMonster> visibleMatches = Collections.emptyList();
	private int searchPreviewIndex;
	private LocationMapPins mapPins;

	public SlayerAtlasPanel(MonsterDatabase database, ShortestPathService shortestPathService, SlayerAtlasConfig config)
	{
		this(database, shortestPathService, config, FavoriteTasks.none());
	}

	public SlayerAtlasPanel(
		MonsterDatabase database,
		ShortestPathService shortestPathService,
		SlayerAtlasConfig config,
		FavoriteTasks favorites)
	{
		this(
			database,
			shortestPathService,
			config,
			MonsterImageLoader.none(),
			null,
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			null,
			null,
			TaskLoadouts.none(),
			favorites,
			LoadoutSelection.none(),
			SelectedMonster.none());
	}

	public SlayerAtlasPanel(
		MonsterDatabase database,
		ShortestPathService shortestPathService,
		SlayerAtlasConfig config,
		MonsterImageLoader images,
		SpriteManager sprites,
		WikiLoadoutClient loadouts)
	{
		this(
			database,
			shortestPathService,
			config,
			images,
			sprites,
			loadouts,
			WikiInventoryClient.none(),
			null,
			null,
			TaskLoadouts.none(),
			FavoriteTasks.none(),
			LoadoutSelection.none(),
			SelectedMonster.none());
	}

	@Inject
	public SlayerAtlasPanel(
		MonsterDatabase database,
		ShortestPathService shortestPathService,
		SlayerAtlasConfig config,
		MonsterImageLoader images,
		SpriteManager sprites,
		WikiLoadoutClient loadouts,
		WikiInventoryClient inventory,
		GearRecommendationService recommendations,
		LocationMapPins mapPins,
		TaskLoadouts taskLoadouts,
		FavoriteTasks favorites,
		LoadoutSelection loadoutSelection,
		SelectedMonster selectedMonster)
	{
		super(false);
		this.database = database;
		this.shortestPathService = shortestPathService;
		this.config = config;
		this.mapPins = mapPins;
		this.images = images;
		this.sprites = sprites;
		this.loadouts = loadouts;
		this.inventory = inventory == null ? WikiInventoryClient.none() : inventory;
		this.recommendations = recommendations;
		this.taskLoadouts = taskLoadouts == null ? TaskLoadouts.none() : taskLoadouts;
		this.favorites = favorites == null ? FavoriteTasks.none() : favorites;
		this.loadoutSelection = loadoutSelection == null ? LoadoutSelection.none() : loadoutSelection;
		this.selectedMonster = selectedMonster == null ? SelectedMonster.none() : selectedMonster;
		this.taskStatus = new TaskStatusPanel(this::openCurrentTask, images);
		images.prefetch(database.getMonsters());
		images.prefetch(database.getPages());

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		searchBar.setName("search-bar");
		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 36));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.setMinimumSize(new Dimension(0, 36));
		searchBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		SearchFieldSupport.configure(searchBar, "Search Tasks");
		SearchFieldSupport.bindArrows(searchBar, () -> moveSearchPreview(-1), () -> moveSearchPreview(1));
		searchBar.addActionListener(event -> openHighlightedSearchResult());
		searchBar.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				onSearchChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				onSearchChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				onSearchChanged();
			}
		});

		top.setBorder(new EmptyBorder(0, 0, 8, 0));
		JLabel title = PanelWidgets.heading(PanelCopy.TITLE);
		title.setName("panel-title");
		top.add(title);

		searchSlot.setName("search-slot");
		searchSlot.setOpaque(false);
		searchSlot.setBackground(ColorScheme.DARK_GRAY_COLOR);
		searchSlot.setBorder(new EmptyBorder(8, 0, 0, 0));
		searchSlot.setAlignmentX(Component.LEFT_ALIGNMENT);
		searchSlot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
		searchSlot.add(searchBar, BorderLayout.CENTER);
		top.add(searchSlot);

		taskSlot.setName("task-slot");
		taskSlot.setOpaque(false);
		taskSlot.setBackground(ColorScheme.DARK_GRAY_COLOR);
		taskSlot.setBorder(new EmptyBorder(8, 0, 0, 0));
		taskSlot.setAlignmentX(Component.LEFT_ALIGNMENT);
		taskSlot.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		taskSlot.add(taskStatus, BorderLayout.CENTER);
		top.add(taskSlot);

		listPanel.setName("monster-list");
		listPanel.setBorder(new EmptyBorder(4, 0, 0, 0));
		listScroll = scrollable(listPanel);

		content.setBackground(ColorScheme.DARK_GRAY_COLOR);
		content.setPreferredSize(new Dimension(0, 0));
		content.setMinimumSize(new Dimension(0, 0));

		add(top, BorderLayout.NORTH);
		add(content, BorderLayout.CENTER);
		refreshContent();
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(PANEL_WIDTH + SCROLLBAR_WIDTH, 0);
	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(PANEL_WIDTH + SCROLLBAR_WIDTH, 0);
	}

	public void setCurrentTask(CurrentSlayerTask task)
	{
		this.currentTask = task == null ? new CurrentSlayerTask(null, null, 0, 0) : task;
		taskStatus.update(currentTask, database.findByTaskName(currentTask.getName()));
		updateCurrentTaskHighlights();
		if (showingDetail && detailHeader != null)
		{
			detailHeader.setTask(currentTask);
		}
		updateChrome();
	}

	public void selectMonster(SlayerMonster monster)
	{
		if (monster == null)
		{
			showingDetail = false;
			setSelected(null);
			refreshContent();
			return;
		}
		if (!isSearchEmpty())
		{
			ignoreSearchEvents = true;
			searchBar.setText("");
			ignoreSearchEvents = false;
		}
		showDetail(monster);
	}

	public SlayerMonster getSelected()
	{
		return selected;
	}

	public void refreshPathButtons()
	{
		if (showingDetail && selected != null)
		{
			showDetail(selected);
		}
	}

	public void setOwnedItems(OwnedItems owned)
	{
		setOwnedItems(owned, OwnedItems.none());
	}

	public void setOwnedItems(OwnedItems owned, OwnedItems carried)
	{
		OwnedItems nextOwned = owned == null ? OwnedItems.none() : owned;
		OwnedItems nextCarried = carried == null ? OwnedItems.none() : carried;
		if (recommendations != null)
		{
			boolean unchanged = nextOwned.equals(recommendations.owned())
				&& nextCarried.equals(recommendations.carried());
			recommendations.setOwnedItems(nextOwned);
			recommendations.setCarriedItems(nextCarried);
			if (unchanged)
			{
				return;
			}
		}
		refreshGear();
	}

	public void setUnlockedPrayers(UnlockedPrayers prayers)
	{
		if (recommendations == null)
		{
			return;
		}
		UnlockedPrayers next = prayers == null ? UnlockedPrayers.unknown() : prayers;
		if (next.equals(recommendations.unlockedPrayers()))
		{
			return;
		}
		recommendations.setUnlockedPrayers(next);
		refreshPrayers();
	}

	public void refreshGear()
	{
		if (detailPanel != null && showingDetail)
		{
			detailPanel.refreshGear();
		}
	}

	public void refreshPrayers()
	{
		if (detailPanel != null && showingDetail)
		{
			detailPanel.refreshPrayers();
		}
	}

	private void onSearchChanged()
	{
		if (ignoreSearchEvents)
		{
			return;
		}
		showingDetail = false;
		setSelected(null);
		if (listRefreshScheduled)
		{
			return;
		}
		listRefreshScheduled = true;
		SwingUtilities.invokeLater(() ->
		{
			listRefreshScheduled = false;
			if (showingDetail)
			{
				return;
			}
			refreshMonsterList(true);
		});
	}

	private void openHighlightedSearchResult()
	{
		refreshMonsterList(false);
		if (visibleMatches.isEmpty())
		{
			return;
		}
		showDetail(visibleMatches.get(clampedPreviewIndex()));
	}

	private void moveSearchPreview(int delta)
	{
		refreshMonsterList(false);
		if (visibleMatches.isEmpty())
		{
			return;
		}
		searchPreviewIndex = Math.max(0, Math.min(visibleMatches.size() - 1, clampedPreviewIndex() + delta));
		previewSearchResult();
		scrollPreviewIntoView();
	}

	public void openCurrentTask()
	{
		if (!currentTask.hasTask())
		{
			return;
		}
		SlayerMonster monster = database.findByTaskName(currentTask.getName());
		if (monster == null)
		{
			return;
		}
		if (showingDetail && selected != null && selected.getId().equals(monster.getId()))
		{
			return;
		}
		selectMonster(monster);
	}

	public void openFromBankTab()
	{
		if (selected == null)
		{
			openCurrentTask();
		}
	}

	private void refreshContent()
	{
		if (showingDetail && selected != null && isSearchEmpty())
		{
			showDetail(selected);
			return;
		}
		refreshMonsterList(true);
	}

	private void refreshMonsterList(boolean jumpToTop)
	{
		refreshMonsterList(jumpToTop, false);
	}

	private void refreshMonsterList(boolean jumpToTop, boolean forceRebuild)
	{
		showingDetail = false;
		setSelected(null);
		String query = searchBar.getText() == null ? "" : searchBar.getText();
		List<SlayerMonster> matches = FavoriteTasks.pinToTop(database.search(query), favorites);
		boolean listShowing = content.getComponentCount() == 1 && content.getComponent(0) == listScroll;
		boolean rebuild = forceRebuild || !query.equals(visibleQuery) || listPanel.getComponentCount() == 0;
		String keepPreviewId = null;
		if (rebuild)
		{
			if (!jumpToTop)
			{
				keepPreviewId = previewedMonsterId();
			}
			listPanel.removeAll();
			if (matches.isEmpty())
			{
				listPanel.add(PanelWidgets.muted("No monsters match that search."));
			}
			else
			{
				for (SlayerMonster monster : matches)
				{
					listPanel.add(itemFor(monster));
					listPanel.add(Box.createVerticalStrut(4));
				}
			}
			for (MonsterListItem item : listItems.values())
			{
				item.clearPointerHover();
			}
			visibleQuery = query;
			searchPreviewIndex = 0;
			listPanel.revalidate();
			listPanel.repaint();
			if (jumpToTop)
			{
				ScrollReset.toTop(listScroll);
				SwingUtilities.invokeLater(() -> ScrollReset.toTop(listScroll));
			}
		}
		visibleMatches = isSearchEmpty() || matches.isEmpty()
			? Collections.emptyList()
			: List.copyOf(matches);
		if (keepPreviewId != null)
		{
			searchPreviewIndex = indexOfVisible(keepPreviewId);
		}
		previewSearchResult();
		if (!listShowing)
		{
			content.removeAll();
			content.add(listScroll, BorderLayout.CENTER);
			content.revalidate();
			content.repaint();
		}
		updateChrome();
	}

	private MonsterListItem itemFor(SlayerMonster monster)
	{
		MonsterListItem item = listItems.computeIfAbsent(monster.getId(), id -> new MonsterListItem(
			monster,
			isCurrent(monster),
			favorites.contains(id),
			images,
			favorite -> toggleFavorite(id, favorite),
			() -> showDetail(monster)));
		item.setCurrentTask(isCurrent(monster));
		item.setFavorite(favorites.contains(monster.getId()));
		return item;
	}

	private void toggleFavorite(String monsterId, boolean favorite)
	{
		favorites.set(monsterId, favorite);
		refreshMonsterList(false, true);
	}

	private void previewSearchResult()
	{
		MonsterListItem previewed = previewedItem();
		for (MonsterListItem item : listItems.values())
		{
			item.setPreviewed(item == previewed);
		}
	}

	private MonsterListItem previewedItem()
	{
		if (visibleMatches.isEmpty())
		{
			return null;
		}
		return listItems.get(visibleMatches.get(clampedPreviewIndex()).getId());
	}

	private String previewedMonsterId()
	{
		MonsterListItem item = previewedItem();
		return item == null ? null : item.getMonsterId();
	}

	private int indexOfVisible(String monsterId)
	{
		for (int index = 0; index < visibleMatches.size(); index++)
		{
			if (monsterId.equals(visibleMatches.get(index).getId()))
			{
				return index;
			}
		}
		return 0;
	}

	private int clampedPreviewIndex()
	{
		if (visibleMatches.isEmpty())
		{
			return 0;
		}
		return Math.max(0, Math.min(searchPreviewIndex, visibleMatches.size() - 1));
	}

	private void scrollPreviewIntoView()
	{
		MonsterListItem item = previewedItem();
		if (item == null)
		{
			return;
		}
		item.scrollRectToVisible(new Rectangle(0, 0, item.getWidth(), item.getHeight()));
	}

	private void updateCurrentTaskHighlights()
	{
		for (SlayerMonster monster : database.getMonsters())
		{
			MonsterListItem item = listItems.get(monster.getId());
			if (item != null)
			{
				item.setCurrentTask(isCurrent(monster));
			}
		}
	}

	private boolean isCurrent(SlayerMonster monster)
	{
		return currentTask.hasTask() && TaskMatcher.matchesMonster(currentTask.getName(), monster);
	}

	private boolean isSearchEmpty()
	{
		return searchBar.getText() == null || searchBar.getText().trim().isEmpty();
	}

	private void updateChrome()
	{
		searchSlot.setVisible(SearchBarVisibility.visible(showingDetail));
		taskSlot.setVisible(CurrentTaskVisibility.visible(!showingDetail, isSearchEmpty(), currentTask.hasTask()));
		top.revalidate();
		top.repaint();
	}

	private void backToList()
	{
		showingDetail = false;
		setSelected(null);
		refreshContent();
	}

	private void setSelected(SlayerMonster monster)
	{
		selected = monster;
		selectedMonster.set(monster);
	}

	private void showDetail(SlayerMonster monster)
	{
		setSelected(monster);
		showingDetail = true;
		JPanel page = new JPanel(new BorderLayout());
		page.setBackground(ColorScheme.DARK_GRAY_COLOR);
		detailHeader = new MonsterDetailHeader(
			monster,
			images,
			this::backToList,
			() -> openWiki(monster),
			() -> openDps(monster),
			currentTask);
		page.add(detailHeader, BorderLayout.NORTH);
		MonsterDetailPanel detail = new MonsterDetailPanel(
			monster,
			database.locationsFor(monster),
			this,
			sprites,
			images,
			loadouts,
			inventory,
			recommendations,
			database,
			taskLoadouts,
			loadoutSelection);
		JScrollPane body = scrollable(detail);
		body.setName("detail-scroll");
		page.add(body, BorderLayout.CENTER);

		detailPanel = detail;
		updateChrome();
		content.removeAll();
		content.add(page, BorderLayout.CENTER);
		content.revalidate();
		content.repaint();
		ScrollReset.toTop(body);
		SwingUtilities.invokeLater(() -> ScrollReset.toTop(body));
	}

	private static JScrollPane scrollable(JPanel view)
	{
		JScrollPane scroll = new JScrollPane(view);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setBackground(ColorScheme.DARK_GRAY_COLOR);
		scroll.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scroll.setPreferredSize(new Dimension(0, 0));
		scroll.setMinimumSize(new Dimension(0, 0));
		return scroll;
	}

	@Override
	public void pathTo(MonsterLocation location)
	{
		if (location == null || !canPath())
		{
			return;
		}
		shortestPathService.pathTo(LocationPath.target(location));
	}

	void useMapPins(LocationMapPins mapPins)
	{
		this.mapPins = mapPins;
	}

	@Override
	public void showOnMap(MonsterLocation location)
	{
		if (location == null || mapPins == null)
		{
			return;
		}
		mapPins.show(location);
	}

	@Override
	public void openWiki(SlayerMonster monster)
	{
		if (monster != null && monster.getWiki() != null)
		{
			LinkBrowser.browse(monster.getWiki());
		}
	}

	@Override
	public void openMonster(SlayerMonster monster)
	{
		if (monster != null)
		{
			selectMonster(monster);
		}
	}

	@Override
	public void openDps(SlayerMonster monster)
	{
		String url = DpsCalculatorUrl.fromMonster(monster);
		if (!url.isEmpty())
		{
			LinkBrowser.browse(url);
		}
	}

	@Override
	public boolean canPath()
	{
		return config.shortestPathEnabled() && shortestPathService != null && shortestPathService.isPluginActive();
	}

	void rebuildOnEdt()
	{
		Runnable task = () ->
		{
			refreshPathButtons();
			refreshGear();
			refreshPrayers();
		};
		if (SwingUtilities.isEventDispatchThread())
		{
			task.run();
		}
		else
		{
			SwingUtilities.invokeLater(task);
		}
	}

	void refreshFavorites()
	{
		Runnable task = () ->
		{
			if (!showingDetail)
			{
				refreshMonsterList(false, true);
			}
		};
		if (SwingUtilities.isEventDispatchThread())
		{
			task.run();
		}
		else
		{
			SwingUtilities.invokeLater(task);
		}
	}
}
