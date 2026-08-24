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
import net.runelite.client.ui.FontManager;

public class MonsterListItem extends JPanel
{
	public MonsterListItem(SlayerMonster monster, boolean currentTask, Runnable onSelect)
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(6, 8, 6, 8));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

		JLabel name = new JLabel(monster.getName());
		name.setForeground(Color.WHITE);
		name.setFont(FontManager.getRunescapeSmallFont());

		String meta = "Lvl " + monster.getSlayerLevel();
		if (monster.getAttribute() != null && !monster.getAttribute().isEmpty())
		{
			meta += " · " + monster.getAttribute();
		}
		if (currentTask)
		{
			meta += " · current task";
			name.setForeground(ColorScheme.BRAND_ORANGE);
		}
		JLabel info = new JLabel(meta);
		info.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		info.setFont(FontManager.getRunescapeSmallFont());

		JPanel text = PanelWidgets.vertical();
		text.setOpaque(false);
		text.add(name);
		text.add(info);
		add(text, BorderLayout.CENTER);

		PanelWidgets.makeHoverable(this, onSelect);
	}
}
