package com.slayerguide.ui;

import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.MonsterLocation;
import com.slayerguide.data.SlayerMonster;
import com.slayerguide.data.TaskMatcher;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class MonsterDetailPanel extends JPanel
{
	public interface Actions
	{
		void pathTo(MonsterLocation location);

		void pathToNearest(SlayerMonster monster);

		void openWiki(SlayerMonster monster);

		boolean canPath();

		String pathUnavailableReason();
	}

	public MonsterDetailPanel(
		SlayerMonster monster,
		List<MonsterLocation> locations,
		CurrentSlayerTask currentTask,
		Actions actions,
		Runnable onBack)
	{
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(0, 0, 8, 0));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		JButton back = PanelWidgets.button("← All monsters");
		back.addActionListener(e -> onBack.run());
		add(back);
		add(Box.createVerticalStrut(8));

		add(PanelWidgets.heading(monster.getName()));
		add(PanelWidgets.muted(metaLine(monster)));
		if (isCurrent(monster, currentTask))
		{
			String taskLine = currentTask.getRemaining() + " remaining";
			if (currentTask.getLocation() != null && !currentTask.getLocation().isEmpty())
			{
				taskLine += " at " + currentTask.getLocation();
			}
			add(PanelWidgets.subtitle(taskLine));
		}
		add(Box.createVerticalStrut(6));

		addLocations(monster, locations, currentTask, actions);
		addSection("Required items", monster.getRequiredItems());
		addTextSection("Weakness", monster.getWeakness());
		addTextSection("Attack style", combatLine(monster));
		addSection("Recommended equipment", monster.getRecommendedEquipment());
		addSection("Potions", monster.getRecommendedPotions());
		addSection("Requirements", monster.getRequirements());
		addSection("Alternatives", monster.getAlternatives());
		addSection("Assigned by", monster.getMasters());
		if (monster.getNotes() != null && !monster.getNotes().isEmpty())
		{
			addTextSection("Notes", monster.getNotes());
		}

		JButton wiki = PanelWidgets.button("Open OSRS Wiki");
		wiki.addActionListener(e -> actions.openWiki(monster));
		add(Box.createVerticalStrut(8));
		add(wiki);
	}

	private void addLocations(
		SlayerMonster monster,
		List<MonsterLocation> locations,
		CurrentSlayerTask currentTask,
		Actions actions)
	{
		JPanel section = PanelWidgets.section("Locations & travel");
		if (locations.isEmpty())
		{
			section.add(PanelWidgets.muted("No locations recorded."));
			add(section);
			return;
		}

		JButton nearest = PanelWidgets.button("Path to nearest location");
		configurePathButton(nearest, actions, () -> actions.pathToNearest(monster));
		section.add(nearest);
		section.add(Box.createVerticalStrut(6));

		for (MonsterLocation location : locations)
		{
			section.add(locationCard(location, currentTask, actions));
			section.add(Box.createVerticalStrut(6));
		}
		add(section);
	}

	private JPanel locationCard(MonsterLocation location, CurrentSlayerTask currentTask, Actions actions)
	{
		JPanel card = PanelWidgets.card();
		JLabel name = PanelWidgets.title(location.getName());
		card.add(name);

		String region = location.getRegion() == null ? "" : location.getRegion();
		if (location.isWilderness())
		{
			region = region.isEmpty() ? "Wilderness" : region + " · Wilderness";
		}
		boolean assigned = currentTask != null && currentTask.hasTask()
			&& TaskMatcher.matchesLocation(currentTask.getLocation(), location);
		if (assigned)
		{
			region = region.isEmpty() ? "Assigned location" : region + " · assigned";
		}
		if (!region.isEmpty())
		{
			card.add(PanelWidgets.muted(region));
		}

		PanelWidgets.addBullets(card, location.getTravel());

		JButton path = PanelWidgets.button("Path here");
		configurePathButton(path, actions, () -> actions.pathTo(location));
		card.add(Box.createVerticalStrut(4));
		card.add(path);
		card.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH, Integer.MAX_VALUE));
		return card;
	}

	private void configurePathButton(JButton button, Actions actions, Runnable onClick)
	{
		if (actions.canPath())
		{
			button.addActionListener(e -> onClick.run());
			return;
		}
		button.setEnabled(false);
		button.setToolTipText(actions.pathUnavailableReason());
	}

	private void addSection(String heading, List<String> items)
	{
		if (items == null || items.isEmpty())
		{
			return;
		}
		JPanel section = PanelWidgets.section(heading);
		PanelWidgets.addBullets(section, items);
		add(section);
	}

	private void addTextSection(String heading, String text)
	{
		if (text == null || text.isEmpty())
		{
			return;
		}
		JPanel section = PanelWidgets.section(heading);
		section.add(PanelWidgets.wrapped(text));
		add(section);
	}

	private static String metaLine(SlayerMonster monster)
	{
		StringBuilder builder = new StringBuilder("Slayer ");
		builder.append(monster.getSlayerLevel());
		if (monster.getCombatRequirement() != null)
		{
			builder.append(" · Combat ").append(monster.getCombatRequirement());
		}
		if (monster.getAttribute() != null && !monster.getAttribute().isEmpty())
		{
			builder.append(" · ").append(monster.getAttribute());
		}
		return builder.toString();
	}

	private static String combatLine(SlayerMonster monster)
	{
		StringBuilder builder = new StringBuilder();
		if (monster.getAttackStyle() != null)
		{
			builder.append("They attack with ").append(monster.getAttackStyle()).append('.');
		}
		if (monster.getProtectionPrayer() != null && !monster.getProtectionPrayer().isEmpty())
		{
			if (builder.length() > 0)
			{
				builder.append(' ');
			}
			builder.append("Typical protection: ").append(monster.getProtectionPrayer()).append('.');
		}
		if (monster.getRecommendedStyle() != null && !monster.getRecommendedStyle().isEmpty())
		{
			if (builder.length() > 0)
			{
				builder.append(' ');
			}
			builder.append("Common kill method: ").append(monster.getRecommendedStyle()).append('.');
		}
		return builder.toString();
	}

	private static boolean isCurrent(SlayerMonster monster, CurrentSlayerTask currentTask)
	{
		return currentTask != null && currentTask.hasTask() && TaskMatcher.matchesMonster(currentTask.getName(), monster);
	}
}
