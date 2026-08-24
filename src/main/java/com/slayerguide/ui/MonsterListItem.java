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

	public MonsterListItem(
		SlayerMonster monster,
		boolean currentTask,
		MonsterImageLoader images,
		Runnable onSelect)
	{
		setLayout(new BorderLayout(8, 0));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

		add(new MonsterPortrait(monster, MonsterImageSizes.LIST, images), BorderLayout.WEST);

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
