package com.slayeratlas;

import com.google.inject.Provides;
import com.slayeratlas.bank.BankTaskTab;
import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.OwnedItems;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.UnlockedPrayers;
import com.slayeratlas.map.LocationMapPins;
import com.slayeratlas.path.AutoPathTargets;
import com.slayeratlas.path.ShortestPathService;
import com.slayeratlas.ui.SidebarIcon;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.events.AccountHashChanged;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
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
	tags = {"slayer", "pvm", "atlas", "task", "path", "monster"}
)
@PluginDependency(SlayerPlugin.class)
public class SlayerAtlasPlugin extends Plugin
{
	private static final int LOGIN_SYNC_TICKS = 3;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private SlayerAtlasConfig config;

	@Inject
	private SlayerPluginService slayerPluginService;

	@Inject
	private MonsterDatabase monsterDatabase;

	@Inject
	private ShortestPathService shortestPathService;

	@Inject
	private OwnedItemsTracker ownedItems;

	@Inject
	private UnlockedPrayersTracker unlockedPrayers;

	@Inject
	private GearRecommendationService recommendations;

	@Inject
	private LocationMapPins mapPins;

	@Inject
	private EventBus eventBus;

	@Inject
	private BankTaskTab bankTaskTab;

	private SlayerAtlasPanel panel;
	private NavigationButton navigationButton;
	private String lastTaskName;
	private String lastTaskLocation;
	private int loginSyncTicks;
	private boolean restoringSession;

	@Override
	protected void startUp()
	{
		panel = injector.getInstance(SlayerAtlasPanel.class);
		panel.useConfigOpener(this::openConfig);
		BufferedImage icon = SidebarIcon.enlarge(ImageUtil.loadImageResource(getClass(), "icon.png"));
		navigationButton = NavigationButton.builder()
			.tooltip("Slayer Atlas")
			.icon(icon)
			.priority(5)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navigationButton);
		panel.useMapPins(mapPins);
		bankTaskTab.setOpenPanel(this::openAtlasFromBankTab);
		eventBus.register(bankTaskTab);
		bankTaskTab.startUp();

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			beginSessionRestore();
			clientThread.invokeLater(() ->
			{
				syncOwnedItems();
				syncUnlockedPrayers();
				syncTask(false);
			});
		}
		log.info("Slayer Atlas started with {} monsters.", monsterDatabase.getMonsters().size());
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navigationButton);
		eventBus.unregister(bankTaskTab);
		bankTaskTab.shutDown();
		mapPins.clear();
		lastTaskName = null;
		lastTaskLocation = null;
		restoringSession = false;
		loginSyncTicks = 0;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			beginSessionRestore();
			clientThread.invokeLater(() ->
			{
				syncOwnedItems();
				syncUnlockedPrayers();
				syncTask(false);
			});
		}
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			beginSessionRestore();
			publishTask(new CurrentSlayerTask(null, null, 0, 0));
		}
	}

	@Subscribe
	public void onAccountHashChanged(AccountHashChanged event)
	{
		clientThread.invokeLater(() ->
		{
			syncOwnedItems();
			syncUnlockedPrayers();
		});
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (ownedItems.onItemContainerChanged(event))
		{
			publishOwnedItems();
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
			boolean liveAssignment = !restoringSession;
			clientThread.invokeLater(() -> syncTask(liveAssignment));
		}
		if (UnlockedPrayersTracker.tracksVarbit(varbitId))
		{
			syncUnlockedPrayers();
		}
		if (OwnedItemsTracker.tracksPotionStore(varpId))
		{
			ownedItems.markPotionsDirty();
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (UnlockedPrayersTracker.tracksSkill(event.getSkill()))
		{
			syncUnlockedPrayers();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (loginSyncTicks > 0)
		{
			loginSyncTicks--;
			syncTask(false);
			syncUnlockedPrayers();
			if (loginSyncTicks == 0)
			{
				restoringSession = false;
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick tick)
	{
		mapPins.onClientTick();
		if (ownedItems.onClientTick())
		{
			publishOwnedItems();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		mapPins.onWidgetLoaded(event);
		if (event.getGroupId() == InterfaceID.BANKMAIN)
		{
			clientThread.invokeLater(() ->
			{
				syncOwnedItems();
			});
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		mapPins.onScriptPostFired(event);
		int scriptId = event.getScriptId();
		if (scriptId == ScriptID.POTIONSTORE_BUILD || scriptId == ScriptID.POTIONSTORE_DOSE_CHANGE)
		{
			ownedItems.markPotionsDirty();
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		mapPins.onWidgetClosed(event);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!SlayerAtlasConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}
		if (ConfigChangeRefresh.favorites(event.getKey()))
		{
			panel.refreshFavorites();
			if (event.getKey() != null)
			{
				return;
			}
		}
		panel.applyConfigChange(event.getKey());
	}

	@Subscribe
	public void onPluginChanged(PluginChanged event)
	{
		if (ShortestPathService.PLUGIN_NAME.equals(event.getPlugin().getName()))
		{
			panel.refreshPathingOnEdt();
		}
	}

	@Provides
	SlayerAtlasConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerAtlasConfig.class);
	}

	private void beginSessionRestore()
	{
		restoringSession = true;
		loginSyncTicks = LOGIN_SYNC_TICKS;
	}

	private void syncTask(boolean liveAssignment)
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
		if (!restoringSession || task.hasTask())
		{
			lastTaskName = name;
			lastTaskLocation = location;
		}
		publishTask(task);

		boolean newLiveAssignment = NewTaskFollow.isNewLiveAssignment(
			liveAssignment && !restoringSession,
			assignmentChanged,
			task.hasTask());
		SlayerMonster monster = newLiveAssignment ? monsterDatabase.findByTaskName(name) : null;
		if (NewTaskFollow.shouldFollow(newLiveAssignment, config.openPanelOnTask()))
		{
			SwingUtilities.invokeLater(() ->
			{
				panel.selectMonster(monster);
				if (navigationButton != null)
				{
					clientToolbar.openPanel(navigationButton);
				}
			});
		}

		if (newLiveAssignment && config.autoPathOnNewTask() && config.shortestPathEnabled()
			&& shortestPathService.isPluginActive() && monster != null)
		{
			shortestPathService.pathTo(AutoPathTargets.of(monsterDatabase, monster, location));
		}
	}

	private void publishTask(CurrentSlayerTask task)
	{
		bankTaskTab.setTask(task);
		SwingUtilities.invokeLater(() -> panel.setCurrentTask(task));
	}

	void openConfig()
	{
		eventBus.post(OpenPluginConfig.click(this));
	}

	private void openAtlasFromBankTab()
	{
		SwingUtilities.invokeLater(() ->
		{
			if (panel != null)
			{
				panel.openFromBankTab();
			}
			if (navigationButton != null)
			{
				clientToolbar.openPanel(navigationButton);
			}
		});
	}

	private void syncOwnedItems()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		ownedItems.syncAccount();
		publishOwnedItems();
	}

	private void publishOwnedItems()
	{
		OwnedItems owned = ownedItems.snapshot();
		OwnedItems carried = ownedItems.carried();
		SwingUtilities.invokeLater(() ->
		{
			if (panel != null)
			{
				panel.setOwnedItems(owned, carried);
			}
			else
			{
				recommendations.setOwnedItems(owned);
				recommendations.setCarriedItems(carried);
			}
		});
	}

	private void syncUnlockedPrayers()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		UnlockedPrayers snapshot = unlockedPrayers.snapshot();
		SwingUtilities.invokeLater(() ->
		{
			if (panel != null)
			{
				panel.setUnlockedPrayers(snapshot);
			}
			else
			{
				recommendations.setUnlockedPrayers(snapshot);
			}
		});
	}
}
