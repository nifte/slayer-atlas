package com.slayerguide;

import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.MonsterDatabase;
import com.slayerguide.data.MonsterLocation;
import com.slayerguide.data.SlayerMonster;
import com.slayerguide.data.TaskMatcher;
import com.slayerguide.path.ShortestPathService;
import com.slayerguide.ui.CurrentTaskVisibility;
import com.slayerguide.ui.MonsterDetailHeader;
import com.slayerguide.ui.MonsterDetailPanel;
import com.slayerguide.ui.MonsterListItem;
import com.slayerguide.ui.PanelWidgets;
import com.slayerguide.ui.SearchBarVisibility;
import com.slayerguide.ui.SearchFieldSupport;
import com.slayerguide.ui.TaskStatusPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.LinkBrowser;

@Singleton
public class SlayerGuidePanel extends PluginPanel implements MonsterDetailPanel.Actions
{
	private final MonsterDatabase database;
	private final ShortestPathService shortestPathService;
	private final SlayerGuideConfig config;
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
	private boolean showingDetail;
	private boolean ignoreSearchEvents;
	private boolean listRefreshScheduled;
	private String visibleQuery;

	@Inject
	public SlayerGuidePanel(MonsterDatabase database, ShortestPathService shortestPathService, SlayerGuideConfig config)
	{
		super(false);
		this.database = database;
		this.shortestPathService = shortestPathService;
		this.config = config;
		this.taskStatus = new TaskStatusPanel(this::openCurrentTask);

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 36));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.setMinimumSize(new Dimension(0, 36));
		searchBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		SearchFieldSupport.configure(searchBar, "Search Tasks");
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
		top.add(PanelWidgets.heading("Slayer Guide"));

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
		updateChrome();
		if (showingDetail && selected != null)
		{
			showDetail(selected);
		}
	}

	public void selectMonster(SlayerMonster monster)
	{
		if (monster == null)
		{
			showingDetail = false;
			selected = null;
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

	private void onSearchChanged()
	{
		if (ignoreSearchEvents)
		{
			return;
		}
		showingDetail = false;
		selected = null;
		if (listRefreshScheduled)
		{
			return;
		}
		listRefreshScheduled = true;
		SwingUtilities.invokeLater(() ->
		{
			listRefreshScheduled = false;
			showMonsterList();
		});
	}

	private void openCurrentTask()
	{
		if (!currentTask.hasTask())
		{
			return;
		}
		SlayerMonster monster = database.findByTaskName(currentTask.getName());
		if (monster != null)
		{
			selectMonster(monster);
		}
	}

	private void refreshContent()
	{
		if (showingDetail && selected != null && isSearchEmpty())
		{
			showDetail(selected);
			return;
		}
		showMonsterList();
	}

	private void showMonsterList()
	{
		showingDetail = false;
		selected = null;
		String query = searchBar.getText() == null ? "" : searchBar.getText();
		boolean listShowing = content.getComponentCount() == 1 && content.getComponent(0) == listScroll;
		if (!query.equals(visibleQuery) || listPanel.getComponentCount() == 0)
		{
			List<SlayerMonster> matches = database.search(query);
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
			visibleQuery = query;
			listPanel.revalidate();
			listPanel.repaint();
		}
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
			() -> showDetail(monster)));
		item.setCurrentTask(isCurrent(monster));
		return item;
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
		selected = null;
		refreshContent();
	}

	private void showDetail(SlayerMonster monster)
	{
		selected = monster;
		showingDetail = true;
		updateChrome();
		content.removeAll();
		JPanel page = new JPanel(new BorderLayout());
		page.setBackground(ColorScheme.DARK_GRAY_COLOR);
		page.add(new MonsterDetailHeader(monster, this::backToList), BorderLayout.NORTH);
		MonsterDetailPanel detail = new MonsterDetailPanel(
			monster,
			database.locationsFor(monster),
			currentTask,
			this);
		page.add(scrollable(detail), BorderLayout.CENTER);
		content.add(page, BorderLayout.CENTER);
		content.revalidate();
		content.repaint();
	}

	private static JScrollPane scrollable(JPanel view)
	{
		JScrollPane scroll = new JScrollPane(view);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
		shortestPathService.pathTo(new WorldPoint(location.getX(), location.getY(), location.getPlane()));
	}

	@Override
	public void pathToNearest(SlayerMonster monster)
	{
		if (monster == null || !canPath())
		{
			return;
		}
		List<WorldPoint> points = new ArrayList<>();
		String assigned = currentTask != null && TaskMatcher.matchesMonster(currentTask.getName(), monster)
			? currentTask.getLocation()
			: null;
		if (assigned != null && !assigned.isEmpty())
		{
			MonsterLocation preferred = database.preferredLocation(monster, assigned);
			if (preferred != null)
			{
				pathTo(preferred);
				return;
			}
		}
		for (MonsterLocation location : database.locationsFor(monster))
		{
			points.add(new WorldPoint(location.getX(), location.getY(), location.getPlane()));
		}
		shortestPathService.pathTo(points);
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
	public boolean canPath()
	{
		return config.shortestPathEnabled() && shortestPathService != null && shortestPathService.isPluginActive();
	}

	@Override
	public String pathUnavailableReason()
	{
		if (!config.shortestPathEnabled())
		{
			return "Shortest Path integration is disabled in Slayer Guide settings.";
		}
		return "Install and enable the Shortest Path plugin from the Plugin Hub.";
	}

	void rebuildOnEdt()
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			refreshPathButtons();
		}
		else
		{
			SwingUtilities.invokeLater(this::refreshPathButtons);
		}
	}
}
