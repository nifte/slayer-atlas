package com.slayerguide;

import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.MonsterDatabase;
import com.slayerguide.data.MonsterLocation;
import com.slayerguide.data.SlayerMonster;
import com.slayerguide.data.TaskMatcher;
import com.slayerguide.path.ShortestPathService;
import com.slayerguide.ui.MonsterDetailPanel;
import com.slayerguide.ui.MonsterListItem;
import com.slayerguide.ui.PanelWidgets;
import com.slayerguide.ui.TaskStatusPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
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
	private final JPanel content = new JPanel(new BorderLayout());
	private CurrentSlayerTask currentTask = new CurrentSlayerTask(null, null, 0, 0);
	private SlayerMonster selected;
	private boolean showingDetail;
	private boolean ignoreSearchEvents;

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
		searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.setMinimumSize(new Dimension(0, 30));
		searchBar.setToolTipText("Search by monster name, alias, or style");
		searchBar.addActionListener(e -> onSearchChanged());
		searchBar.addClearListener(this::onSearchChanged);
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

		JPanel top = PanelWidgets.vertical();
		top.setBorder(new EmptyBorder(0, 0, 8, 0));
		JLabel heading = PanelWidgets.heading("Slayer Guide");
		top.add(heading);
		top.add(PanelWidgets.wrapped("Search any assignment, or follow your current task."));
		top.add(Box.createVerticalStrut(8));
		top.add(searchBar);

		content.setBackground(ColorScheme.DARK_GRAY_COLOR);
		content.setPreferredSize(new Dimension(0, 0));
		content.setMinimumSize(new Dimension(0, 0));
		JPanel footer = new JPanel(new BorderLayout());
		footer.setBackground(ColorScheme.DARK_GRAY_COLOR);
		footer.setBorder(new EmptyBorder(8, 0, 0, 0));
		footer.add(taskStatus, BorderLayout.CENTER);

		add(top, BorderLayout.NORTH);
		add(content, BorderLayout.CENTER);
		add(footer, BorderLayout.SOUTH);
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
		if (showingDetail && selected != null)
		{
			showDetail(selected);
		}
		else
		{
			refreshContent();
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
		ignoreSearchEvents = true;
		searchBar.setText("");
		ignoreSearchEvents = false;
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
		refreshContent();
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
		if (showingDetail && selected != null && (searchBar.getText() == null || searchBar.getText().trim().isEmpty()))
		{
			showDetail(selected);
			return;
		}
		showingDetail = false;
		content.removeAll();
		JPanel list = PanelWidgets.vertical();
		list.setBorder(new EmptyBorder(4, 0, 0, 0));

		String query = searchBar.getText();
		List<SlayerMonster> matches = database.search(query);
		if (matches.isEmpty())
		{
			list.add(PanelWidgets.muted("No monsters match that search."));
		}
		else
		{
			for (SlayerMonster monster : matches)
			{
				boolean current = currentTask.hasTask() && TaskMatcher.matchesMonster(currentTask.getName(), monster);
				list.add(new MonsterListItem(monster, current, () -> showDetail(monster)));
				list.add(Box.createVerticalStrut(4));
			}
		}

		content.add(scrollable(list), BorderLayout.CENTER);
		content.revalidate();
		content.repaint();
	}

	private void showDetail(SlayerMonster monster)
	{
		selected = monster;
		showingDetail = true;
		content.removeAll();
		MonsterDetailPanel detail = new MonsterDetailPanel(
			monster,
			database.locationsFor(monster),
			currentTask,
			this,
			() ->
			{
				showingDetail = false;
				selected = null;
				refreshContent();
			});
		content.add(scrollable(detail), BorderLayout.CENTER);
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
		return config.shortestPathEnabled() && shortestPathService.isPluginActive();
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
