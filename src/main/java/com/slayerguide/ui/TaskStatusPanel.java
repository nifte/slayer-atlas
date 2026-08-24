package com.slayerguide.ui;

import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.SlayerMonster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class TaskStatusPanel extends JPanel
{
	private final JTextArea label = PanelWidgets.wrappingText(
		CurrentTaskText.label(null, null),
		Color.WHITE,
		PanelFonts.body());
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
		if (task != null && task.hasTask())
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

	private void openIfPossible()
	{
		if (task != null && task.hasTask())
		{
			onOpenTask.run();
		}
	}
}
