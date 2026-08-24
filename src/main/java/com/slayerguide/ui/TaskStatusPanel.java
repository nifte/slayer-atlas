package com.slayerguide.ui;

import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.SlayerMonster;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class TaskStatusPanel extends JPanel
{
	private final JTextArea label = PanelWidgets.wrappingText(
		CurrentTaskText.label(null, null),
		ColorScheme.BRAND_ORANGE,
		PanelFonts.body());
	private final MonsterPortrait portrait;
	private final Runnable onOpenTask;
	private CurrentSlayerTask task;
	private SlayerMonster monster;

	public TaskStatusPanel(Runnable onOpenTask, MonsterImageLoader images)
	{
		this.onOpenTask = onOpenTask;
		this.portrait = new MonsterPortrait(null, MonsterImageSizes.LIST, images);
		portrait.setName("current-task-portrait");
		label.setName("current-task-label");
		setLayout(new BorderLayout(8, 0));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

		add(portrait, BorderLayout.WEST);
		add(label, BorderLayout.CENTER);
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
		label.setText(CurrentTaskText.label(task, monster));
		portrait.setMonster(hasTask() ? monster : null);
		if (hasTask())
		{
			setToolTipText("Open this task in the guide");
		}
		else
		{
			setToolTipText(null);
		}
		revalidate();
		repaint();
	}

	private boolean hasTask()
	{
		return task != null && task.hasTask();
	}

	private void openIfPossible()
	{
		if (hasTask())
		{
			onOpenTask.run();
		}
	}
}
