package com.slayeratlas.bank;

import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.LoadoutBankMatcher;
import com.slayeratlas.data.LoadoutSelection;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.TaskBankLoadout;
import com.slayeratlas.data.TaskLoadouts;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ScriptID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
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
	private final TaskLoadouts taskLoadouts;
	private final GearRecommendationService recommendations;

	private CurrentSlayerTask task = new CurrentSlayerTask(null, null, 0, 0);
	private LoadoutBankMatcher matcher = LoadoutBankMatcher.of((GearLoadout) null);
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
		TaskLoadouts taskLoadouts,
		GearRecommendationService recommendations)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.itemManager = itemManager;
		this.tabInterface = tabInterface;
		this.database = database;
		this.selection = selection;
		this.taskLoadouts = taskLoadouts;
		this.recommendations = recommendations;
		this.tabInterface.setOnActivated(this::prepareFilter);
		this.tabInterface.setOnClicked(this::openCurrentTask);
		this.selection.setOnChange(() -> clientThread.invoke(this::refreshIfActive));
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
	}

	public void setTask(CurrentSlayerTask task)
	{
		this.task = task == null ? new CurrentSlayerTask(null, null, 0, 0) : task;
		clientThread.invokeLater(this::syncButton);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANKMAIN && task.hasTask())
		{
			tabInterface.init();
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (BankTaskTabClicks.isBankUnload(event.getGroupId(), event.isUnload()))
		{
			tabInterface.unload();
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
		if (event.getScriptId() == ScriptID.BANKMAIN_INIT && task.hasTask())
		{
			tabInterface.init();
			return;
		}
		if (event.getScriptId() == ScriptID.POTIONSTORE_BUILD)
		{
			tabInterface.handleCurrentTab(BankTaskTabClicks.POTION_STORE_TAB);
			return;
		}
		if (event.getScriptId() == ScriptID.BANKMAIN_SEARCHING && tabInterface.isLoadoutTabActive())
		{
			client.getIntStack()[client.getIntStackSize() - 1] = 1;
			return;
		}
		if (event.getScriptId() == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			if (task.hasTask())
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
			SlayerMonster monster = currentMonster();
			String name = monster == null ? "Slayer Atlas" : monster.getName();
			bankTitle.setText("Tab <col=ff0000>" + name + " ");
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
	}

	@Subscribe(priority = -1)
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		tabInterface.handleClick(event);
	}

	private void syncButton()
	{
		tabInterface.setShowButton(task.hasTask());
		if (!task.hasTask())
		{
			tabInterface.destroy();
			return;
		}
		tabInterface.init();
	}

	private void prepareFilter()
	{
		matcher = LoadoutBankMatcher.of(currentLoadout());
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

	private void openCurrentTask()
	{
		openPanel.run();
	}

	private GearLoadout currentLoadout()
	{
		return TaskBankLoadout.resolve(
			currentMonster(),
			selection,
			taskLoadouts,
			recommendations == null ? null : recommendations.recommendation());
	}

	private SlayerMonster currentMonster()
	{
		return database.findByTaskName(task.getName());
	}

	private boolean matches(int itemId)
	{
		if (itemId <= 0)
		{
			return false;
		}
		int canonical = itemManager.canonicalize(itemId);
		ItemComposition composition = itemManager.getItemComposition(canonical);
		return composition != null && matcher.matches(composition.getName());
	}
}
