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

	@ConfigItem(
		keyName = "autoSelectTask",
		name = "Auto-select current task",
		description = "When you receive a new Slayer task (or log in with one), automatically select that monster in the side panel.",
		section = taskSection,
		position = 0
	)
	default boolean autoSelectTask()
	{
		return true;
	}

	@ConfigItem(
		keyName = "openPanelOnTask",
		name = "Open panel on new task",
		description = "Open the Slayer Atlas side panel when a new task is selected automatically.",
		section = taskSection,
		position = 1
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
		description = "When a new task is auto-selected, also start a Shortest Path route to that monster. Requires Shortest Path.",
		section = pathSection,
		position = 1
	)
	default boolean autoPathOnNewTask()
	{
		return false;
	}
}
