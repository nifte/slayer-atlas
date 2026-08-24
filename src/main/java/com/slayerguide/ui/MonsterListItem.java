package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class MonsterListItem extends JPanel
{
	private final JLabel name;

	public MonsterListItem(SlayerMonster monster, boolean currentTask, Runnable onSelect)
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		name = new JLabel(monster.getName());
		name.setFont(PanelFonts.body());
		add(name, BorderLayout.CENTER);
		setCurrentTask(currentTask);

		PanelWidgets.makeHoverable(this, onSelect);
	}

	public void setCurrentTask(boolean currentTask)
	{
		name.setForeground(currentTask ? ColorScheme.BRAND_ORANGE : Color.WHITE);
	}
}
