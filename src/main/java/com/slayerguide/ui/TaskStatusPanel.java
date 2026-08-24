package com.slayerguide.ui;

import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.SlayerMonster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public class TaskStatusPanel extends JPanel
{
	private final JLabel title = new JLabel("No Slayer task");
	private final JLabel detail = new JLabel("Get an assignment, or search a monster below.");
	private final Runnable onOpenTask;
	private CurrentSlayerTask task;
	private SlayerMonster monster;

	public TaskStatusPanel(Runnable onOpenTask)
	{
		this.onOpenTask = onOpenTask;
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		title.setForeground(Color.WHITE);
		title.setFont(FontManager.getRunescapeBoldFont());
		detail.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		detail.setFont(FontManager.getRunescapeSmallFont());

		JPanel text = PanelWidgets.vertical();
		text.setOpaque(false);
		text.add(title);
		text.add(detail);
		add(text, BorderLayout.CENTER);

		setPreferredSize(new Dimension(0, 52));
		PanelWidgets.makeHoverable(this, this::openIfPossible);
		refresh();
	}

	public void update(CurrentSlayerTask currentTask, SlayerMonster matched)
	{
		this.task = currentTask;
		this.monster = matched;
		refresh();
	}

	private void refresh()
	{
		if (task != null && task.hasTask())
		{
			title.setText(task.getName());
			String remaining = task.getRemaining() + " remaining";
			if (task.getLocation() != null && !task.getLocation().isEmpty())
			{
				remaining += " · " + task.getLocation();
			}
			if (monster == null)
			{
				remaining += " · unmatched";
			}
			detail.setText(remaining);
			setToolTipText("Open this task in the guide");
		}
		else
		{
			title.setText("No Slayer task");
			detail.setText("Search a monster or visit a Slayer master.");
			setToolTipText(null);
		}
	}

	private void openIfPossible()
	{
		if (task != null && task.hasTask())
		{
			onOpenTask.run();
		}
	}
}
