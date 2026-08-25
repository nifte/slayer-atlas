package com.slayeratlas.ui;

import com.slayeratlas.data.SlayerMonster;
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
	private boolean hovered;
	private boolean previewed;

	public MonsterListItem(
		SlayerMonster monster,
		boolean currentTask,
		MonsterImageLoader images,
		Runnable onSelect)
	{
		setName("monster-" + monster.getId());
		setLayout(new BorderLayout(8, 0));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

		add(new MonsterPortrait(monster, MonsterImageSizes.LIST, images), BorderLayout.WEST);

		name = new JLabel(MonsterName.display(monster.getName()));
		name.setFont(PanelFonts.body());
		add(name, BorderLayout.CENTER);
		setCurrentTask(currentTask);

		PanelWidgets.makeHoverable(this, onSelect, this::setHovered);
	}

	public void setCurrentTask(boolean currentTask)
	{
		name.setForeground(currentTask ? ColorScheme.BRAND_ORANGE : Color.WHITE);
	}

	public void setPreviewed(boolean previewed)
	{
		this.previewed = previewed;
		refreshBackground();
	}

	private void setHovered(boolean hovered)
	{
		this.hovered = hovered;
		refreshBackground();
	}

	private void refreshBackground()
	{
		setBackground(hovered || previewed
			? ColorScheme.DARKER_GRAY_HOVER_COLOR
			: ColorScheme.DARKER_GRAY_COLOR);
		repaint();
	}

	public void clearPointerHover()
	{
		hovered = false;
		refreshBackground();
	}
}
