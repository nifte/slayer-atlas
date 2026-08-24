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
import net.runelite.client.ui.FontManager;

public class TaskStatusPanel extends JPanel
{
	private final JTextArea label = PanelWidgets.wrappingText(
		CurrentTaskText.label(null, null),
		Color.WHITE,
		FontManager.getRunescapeBoldFont());
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
			label.setForeground(Color.WHITE);
			setToolTipText("Open this task in the guide");
		}
		else
		{
			label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
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
