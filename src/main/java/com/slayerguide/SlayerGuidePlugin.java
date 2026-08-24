package com.slayerguide;

import com.google.inject.Provides;
import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.MonsterDatabase;
import com.slayerguide.data.MonsterLocation;
import com.slayerguide.data.SlayerMonster;
import com.slayerguide.path.ShortestPathService;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PluginChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "Slayer Atlas",
	description = "Side panel with locations, travel, gear, and Shortest Path routing for every Slayer assignment",
	tags = {"slayer", "pvm", "guide", "task", "path", "monster", "atlas"}
)
@PluginDependency(SlayerPlugin.class)
public class SlayerGuidePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private SlayerGuideConfig config;

	@Inject
	private SlayerPluginService slayerPluginService;

	@Inject
	private MonsterDatabase monsterDatabase;

	@Inject
	private ShortestPathService shortestPathService;

	private SlayerGuidePanel panel;
	private NavigationButton navigationButton;
	private String lastTaskName;
	private String lastTaskLocation;
	private int loginSyncTicks;

	@Override
	protected void startUp()
	{
		panel = injector.getInstance(SlayerGuidePanel.class);
		BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		navigationButton = NavigationButton.builder()
			.tooltip("Slayer Atlas")
			.icon(icon)
			.priority(5)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navigationButton);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			loginSyncTicks = 3;
			clientThread.invokeLater(() -> syncTask(false));
		}
		log.info("Slayer Atlas started with {} monsters.", monsterDatabase.getMonsters().size());
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navigationButton);
		lastTaskName = null;
		lastTaskLocation = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			loginSyncTicks = 3;
			clientThread.invokeLater(() -> syncTask(false));
		}
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			lastTaskName = null;
			lastTaskLocation = null;
			publishTask(new CurrentSlayerTask(null, null, 0, 0));
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		int varpId = event.getVarpId();
		int varbitId = event.getVarbitId();
		if (varpId == VarPlayerID.SLAYER_COUNT
			|| varpId == VarPlayerID.SLAYER_AREA
			|| varpId == VarPlayerID.SLAYER_TARGET
			|| varpId == VarPlayerID.SLAYER_COUNT_ORIGINAL
			|| varbitId == VarbitID.SLAYER_TARGET_BOSSID)
		{
			// Run after the core Slayer plugin refreshes SlayerPluginService.
			clientThread.invokeLater(() -> syncTask(true));
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (loginSyncTicks > 0)
		{
			loginSyncTicks--;
			syncTask(false);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!SlayerGuideConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}
		panel.rebuildOnEdt();
		if (config.autoSelectTask())
		{
			clientThread.invokeLater(() -> syncTask(false));
		}
	}

	@Subscribe
	public void onPluginChanged(PluginChanged event)
	{
		if (ShortestPathService.PLUGIN_NAME.equals(event.getPlugin().getName()))
		{
			panel.rebuildOnEdt();
		}
	}

	@Provides
	SlayerGuideConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerGuideConfig.class);
	}

	private void syncTask(boolean allowAutoPath)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		String name = slayerPluginService.getTask();
		String location = slayerPluginService.getTaskLocation();
		int remaining = slayerPluginService.getRemainingAmount();
		int initial = slayerPluginService.getInitialAmount();
		CurrentSlayerTask task = new CurrentSlayerTask(name, location, remaining, initial);
		boolean assignmentChanged = !Objects.equals(name, lastTaskName) || !Objects.equals(location, lastTaskLocation);
		lastTaskName = name;
		lastTaskLocation = location;
		publishTask(task);

		if (!assignmentChanged || !task.hasTask() || !config.autoSelectTask())
		{
			return;
		}

		SlayerMonster monster = monsterDatabase.findByTaskName(name);
		SwingUtilities.invokeLater(() ->
		{
			panel.selectMonster(monster);
			if (config.openPanelOnTask() && navigationButton != null)
			{
				clientToolbar.openPanel(navigationButton);
			}
		});

		if (allowAutoPath && config.autoPathOnNewTask() && config.shortestPathEnabled()
			&& shortestPathService.isPluginActive() && monster != null)
		{
			MonsterLocation target = monsterDatabase.preferredLocation(monster, location);
			if (target != null)
			{
				shortestPathService.pathTo(new WorldPoint(target.getX(), target.getY(), target.getPlane()));
			}
		}
	}

	private void publishTask(CurrentSlayerTask task)
	{
		SwingUtilities.invokeLater(() -> panel.setCurrentTask(task));
	}
}
