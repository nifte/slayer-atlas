package com.slayeratlas;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(SlayerAtlasConfig.GROUP)
public interface SlayerAtlasConfig extends Config
{
	String GROUP = "slayeratlas";

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

	@ConfigItem(
		keyName = "openPanelOnTask",
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
		keyName = "shortestPathEnabled",
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
		keyName = "autoPathOnNewTask",
		name = "Path on new task",
		description = "When a new task is assigned, start a Shortest Path route to that monster. Requires Shortest Path plugin.",
		section = pathSection,
		position = 1
	)
	default boolean autoPathOnNewTask()
	{
		return false;
	}

	@ConfigItem(
		keyName = "onlyRecommendOwnedEquipment",
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
		keyName = "onlyRecommendUnlockedPrayers",
		name = "Only recommend unlocked prayers",
		description = "Recommend the best combat prayers you have unlocked.",
		section = recommendationsSection,
		position = 1
	)
	default boolean onlyRecommendUnlockedPrayers()
	{
		return true;
	}
}
