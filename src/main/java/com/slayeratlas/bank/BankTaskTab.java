package com.slayeratlas.bank;

import com.slayeratlas.SlayerAtlasConfig;
import com.slayeratlas.data.BankTabTitle;
import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.LoadoutBankMatcher;
import com.slayeratlas.data.LoadoutSelection;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.PotionStorageItems;
import com.slayeratlas.data.PotionStorageSlot;
import com.slayeratlas.data.SelectedMonster;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.TaskBankLoadout;
import com.slayeratlas.data.TaskLoadouts;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ScriptID;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetDrag;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;

@Singleton
public class BankTaskTab
{
	private final Client client;
	private final ClientThread clientThread;
	private final ItemManager itemManager;
	private final BankTaskTabInterface tabInterface;
	private final MonsterDatabase database;
	private final LoadoutSelection selection;
	private final SelectedMonster selectedMonster;
	private final TaskLoadouts taskLoadouts;
	private final GearRecommendationService recommendations;
	private final SlayerAtlasConfig config;

	private CurrentSlayerTask task = new CurrentSlayerTask(null, null, 0, 0);
	private LoadoutBankMatcher matcher = LoadoutBankMatcher.of((GearLoadout) null);
	private GearLoadout cachedLoadout;
	private final Map<Integer, Boolean> matchCache = new HashMap<>();
	private List<PotionStorageSlot> potionSlots = List.of();
	private Map<Integer, Integer> copiedClicks = Map.of();
	private boolean potionsDirty = true;
	private Runnable openPanel = () ->
	{
	};

	@Inject
	public BankTaskTab(
		Client client,
		ClientThread clientThread,
		ItemManager itemManager,
		BankTaskTabInterface tabInterface,
		MonsterDatabase database,
		LoadoutSelection selection,
		SelectedMonster selectedMonster,
		TaskLoadouts taskLoadouts,
		GearRecommendationService recommendations,
		SlayerAtlasConfig config)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.itemManager = itemManager;
		this.tabInterface = tabInterface;
		this.database = database;
		this.selection = selection;
		this.selectedMonster = selectedMonster == null ? SelectedMonster.none() : selectedMonster;
		this.taskLoadouts = taskLoadouts;
		this.recommendations = recommendations;
		this.config = config;
		this.tabInterface.setOnActivated(this::prepareFilter);
		this.tabInterface.setOnClicked(this::openCurrentTask);
		this.selection.setOnChange(() -> clientThread.invoke(this::refreshIfActive));
		this.selectedMonster.setOnChange(() -> clientThread.invoke(this::refreshIfActive));
		if (this.recommendations != null)
		{
			this.recommendations.setOnChange(() -> clientThread.invoke(this::refreshIfLoadoutChanged));
		}
	}

	public void setOpenPanel(Runnable openPanel)
	{
		this.openPanel = openPanel == null ? () ->
		{
		} : openPanel;
	}

	public void startUp()
	{
		clientThread.invokeLater(this::syncButton);
	}

	public void shutDown()
	{
		tabInterface.destroy();
		matcher = LoadoutBankMatcher.of((GearLoadout) null);
		cachedLoadout = null;
		matchCache.clear();
		potionSlots = List.of();
		copiedClicks = Map.of();
		potionsDirty = true;
	}

	public void setTask(CurrentSlayerTask task)
	{
		this.task = task == null ? new CurrentSlayerTask(null, null, 0, 0) : task;
		clientThread.invokeLater(this::syncButton);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANKMAIN && shouldShowButton())
		{
			potionsDirty = true;
			tabInterface.init();
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (BankTaskTabClicks.isBankUnload(event.getGroupId(), event.isUnload()))
		{
			tabInterface.unload();
			potionSlots = List.of();
			potionsDirty = true;
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_SEARCH_TOGGLE)
		{
			tabInterface.handleSearch();
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_INIT && shouldShowButton())
		{
			tabInterface.init();
			return;
		}
		if (event.getScriptId() == ScriptID.POTIONSTORE_BUILD)
		{
			potionsDirty = true;
			tabInterface.handleCurrentTab(client.getVarbitValue(VarbitID.BANK_CURRENTTAB));
			return;
		}
		if (event.getScriptId() == ScriptID.POTIONSTORE_DOSE_CHANGE)
		{
			potionsDirty = true;
			return;
		}
		if (event.getScriptId() == ScriptID.BANKMAIN_SEARCHING && tabInterface.isLoadoutTabActive())
		{
			client.getIntStack()[client.getIntStackSize() - 1] = 1;
			return;
		}
		if (event.getScriptId() == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			if (shouldShowButton())
			{
				tabInterface.init();
			}
			if (!tabInterface.isLoadoutTabActive())
			{
				return;
			}
			Widget bankTitle = client.getWidget(InterfaceID.Bankmain.TITLE);
			if (bankTitle == null)
			{
				return;
			}
			bankTitle.setText("Tab <col=ff0000>" + tabTitle() + " ");
			showFilteredBank();
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (!potionsDirty || !tabInterface.isLoadoutTabActive())
		{
			return;
		}
		if (syncPotionSlots())
		{
			showFilteredBank();
		}
	}

	@Subscribe(priority = -1)
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!tabInterface.isLoadoutTabActive())
		{
			return;
		}
		String eventName = event.getEventName();
		int[] intStack = client.getIntStack();
		int intStackSize = client.getIntStackSize();
		if ("getSearchingTagTab".equals(eventName) || "bankBuildTab".equals(eventName))
		{
			intStack[intStackSize - 1] = 1;
			return;
		}
		if (!"bankSearchFilter".equals(eventName))
		{
			return;
		}
		int itemId = intStack[intStackSize - 1];
		intStack[intStackSize - 2] = matches(itemId) ? 1 : 0;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == VarbitID.BANK_CURRENTTAB)
		{
			tabInterface.handleCurrentTab(event.getValue());
		}
		if (PotionStorageItems.tracksVarp(event.getVarpId()))
		{
			potionsDirty = true;
		}
	}

	@Subscribe(priority = -1)
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		tabInterface.handleClick(event);
		if (tabInterface.isLoadoutTabActive())
		{
			BankTaskPotionItems.remapClick(client, event, potionSlots);
			BankCopiedClicks.remap(event, copiedClicks);
		}
	}

	@Subscribe
	public void onWidgetDrag(WidgetDrag event)
	{
		Widget dragged = client.getDraggedWidget();
		if (!BankTaskTabDrags.blocksReorder(
			tabInterface.isLoadoutTabActive(),
			dragged == null ? -1 : dragged.getId(),
			config == null || config.preventTagTabDrags()))
		{
			return;
		}
		client.setDraggedOnWidget(null);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!SlayerAtlasConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}
		if (SlayerAtlasConfig.KEY_SHOW_BANK_TAB_BUTTON.equals(event.getKey()))
		{
			clientThread.invokeLater(this::syncButton);
		}
		if (SlayerAtlasConfig.KEY_USE_BANK_TAB_LAYOUTS.equals(event.getKey()))
		{
			clientThread.invokeLater(this::refreshIfActive);
		}
	}

	private void syncButton()
	{
		boolean show = shouldShowButton();
		tabInterface.setShowButton(show);
		if (!show)
		{
			tabInterface.destroy();
			return;
		}
		tabInterface.init();
	}

	private boolean shouldShowButton()
	{
		return BankTaskButtonLayout.showButton(task.hasTask(), config == null || config.showBankTabButton());
	}

	private void showFilteredBank()
	{
		BankTaskPotionItems.show(client, itemManager, matcher, potionSlots);
		if (config == null || config.useBankTabLayouts())
		{
			copiedClicks = BankTaskTabLayout.apply(client, itemManager, currentLoadout());
			return;
		}
		copiedClicks = Map.of();
	}

	private void prepareFilter()
	{
		cachedLoadout = null;
		matchCache.clear();
		matcher = LoadoutBankMatcher.of(currentLoadout());
		if (potionsDirty)
		{
			syncPotionSlots();
		}
	}

	private void refreshIfActive()
	{
		if (!tabInterface.isLoadoutTabActive())
		{
			return;
		}
		prepareFilter();
		tabInterface.refreshTab();
	}

	private void refreshIfLoadoutChanged()
	{
		if (!tabInterface.isLoadoutTabActive())
		{
			return;
		}
		cachedLoadout = null;
		LoadoutBankMatcher next = LoadoutBankMatcher.of(currentLoadout());
		if (next.sameItems(matcher))
		{
			return;
		}
		matcher = next;
		matchCache.clear();
		tabInterface.refreshTab();
	}

	private boolean syncPotionSlots()
	{
		if (!PotionStorageItems.bankOpen(client))
		{
			return false;
		}
		List<PotionStorageSlot> next = PotionStorageItems.slots(client);
		potionsDirty = false;
		if (next == null)
		{
			return false;
		}
		next = List.copyOf(next);
		boolean changed = !potionSlots.equals(next);
		potionSlots = next;
		return changed;
	}

	private void openCurrentTask()
	{
		openPanel.run();
	}

	private String tabTitle()
	{
		return BankTabTitle.of(
			currentMonster(),
			selection,
			taskLoadouts,
			recommendations == null ? null : recommendations.recommendation());
	}

	private GearLoadout currentLoadout()
	{
		if (cachedLoadout == null)
		{
			cachedLoadout = TaskBankLoadout.resolve(
				currentMonster(),
				selection,
				taskLoadouts,
				recommendations == null ? null : recommendations.recommendation());
		}
		return cachedLoadout;
	}

	private SlayerMonster currentMonster()
	{
		return selectedMonster.or(database.findByTaskName(task.getName()));
	}

	private boolean matches(int itemId)
	{
		if (itemId <= 0)
		{
			return false;
		}
		Boolean cached = matchCache.get(itemId);
		if (cached != null)
		{
			return cached;
		}
		int canonical = itemManager.canonicalize(itemId);
		ItemComposition composition = itemManager.getItemComposition(canonical);
		boolean result = composition != null && matcher.matches(composition.getName());
		matchCache.put(itemId, result);
		if (canonical != itemId)
		{
			matchCache.putIfAbsent(canonical, result);
		}
		return result;
	}
}
