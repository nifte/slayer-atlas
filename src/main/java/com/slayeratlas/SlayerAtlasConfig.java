package com.slayeratlas;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(SlayerAtlasConfig.GROUP)
public interface SlayerAtlasConfig extends Config
{
	String GROUP = "slayeratlas";
	String KEY_OPEN_PANEL_ON_TASK = "openPanelOnTask";
	String KEY_SHORTEST_PATH_ENABLED = "shortestPathEnabled";
	String KEY_AUTO_PATH_ON_NEW_TASK = "autoPathOnNewTask";
	String KEY_ONLY_RECOMMEND_OWNED_EQUIPMENT = "onlyRecommendOwnedEquipment";
	String KEY_ONLY_RECOMMEND_UNLOCKED_PRAYERS = "onlyRecommendUnlockedPrayers";
	String KEY_USE_GOADING_POTIONS = "useGoadingPotions";
	String KEY_SHOW_BANK_TAB_BUTTON = "showBankTabButton";
	String KEY_PREVENT_TAG_TAB_DRAGS = "preventTagTabDrags";
	String KEY_USE_BANK_TAB_LAYOUTS = "useBankTabLayouts";

	@ConfigSection(
		name = "Task tracking",
		description = "How the side panel follows your Slayer assignment.",
		position = 0
	)
	String taskSection = "taskSection";

	@ConfigSection(
		name = "Shortest Path",
		description = "Optional routing through the Shortest Path plugin.",
		position = 1
	)
	String pathSection = "pathSection";

	@ConfigSection(
		name = "Recommendations",
		description = "How recommended gear and prayers are chosen from the wiki and what this account can use.",
		position = 2
	)
	String recommendationsSection = "recommendationsSection";

	@ConfigSection(
		name = "Bank",
		description = "The Slayer Atlas button on the bank interface.",
		position = 3
	)
	String bankSection = "bankSection";

	@ConfigItem(
		keyName = KEY_OPEN_PANEL_ON_TASK,
		name = "Open panel on new task",
		description = "When a slayer master assigns you a new task, open that monster in the Slayer Atlas side panel.",
		section = taskSection,
		position = 0
	)
	default boolean openPanelOnTask()
	{
		return true;
	}

	@ConfigItem(
		keyName = KEY_SHORTEST_PATH_ENABLED,
		name = "Use Shortest Path plugin",
		description = "If the Shortest Path plugin is installed and enabled, show Path buttons and send destinations to it.",
		section = pathSection,
		position = 0
	)
	default boolean shortestPathEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = KEY_AUTO_PATH_ON_NEW_TASK,
		name = "Auto path on new task",
		description = "When a new task is assigned, start a Shortest Path route to Konar's assigned location or the nearest listed location. Requires Shortest Path plugin.",
		section = pathSection,
		position = 1
	)
	default boolean autoPathOnNewTask()
	{
		return false;
	}

	@ConfigItem(
		keyName = KEY_ONLY_RECOMMEND_OWNED_EQUIPMENT,
		name = "Only recommend owned items",
		description = "Recommend the best item you own in each slot. Recommendations may be inaccurate until you have opened a bank at least once.",
		section = recommendationsSection,
		position = 0
	)
	default boolean onlyRecommendOwnedEquipment()
	{
		return true;
	}

	@ConfigItem(
		keyName = KEY_ONLY_RECOMMEND_UNLOCKED_PRAYERS,
		name = "Only recommend unlocked prayers",
		description = "Recommend the best protection/combat prayers you have unlocked.",
		section = recommendationsSection,
		position = 1
	)
	default boolean onlyRecommendUnlockedPrayers()
	{
		return true;
	}

	@ConfigItem(
		keyName = KEY_USE_GOADING_POTIONS,
		name = "Use goading potions",
		description = "On burst/barrage tasks, include a few goading potions instead of the usual monster stacking items such as a venator bow or darts.",
		section = recommendationsSection,
		position = 2
	)
	default boolean useGoadingPotions()
	{
		return false;
	}

	@ConfigItem(
		keyName = KEY_SHOW_BANK_TAB_BUTTON,
		name = "Show bank tab button",
		description = "Show a Slayer Atlas button on the bank that filters to the selected loadout for the monster open in the side panel.",
		section = bankSection,
		position = 0
	)
	default boolean showBankTabButton()
	{
		return true;
	}

	@ConfigItem(
		keyName = KEY_PREVENT_TAG_TAB_DRAGS,
		name = "Prevent bank tab item dragging",
		description = "Ignore dragged items in the bank tab to prevent unwanted item reordering.",
		section = bankSection,
		position = 1
	)
	default boolean preventTagTabDrags()
	{
		return true;
	}

	@ConfigItem(
		keyName = KEY_USE_BANK_TAB_LAYOUTS,
		name = "Use bank tab layouts",
		description = "Arrange the filtered bank tab with equipment on the left and inventory on the right.",
		section = bankSection,
		position = 2
	)
	default boolean useBankTabLayouts()
	{
		return true;
	}
}
