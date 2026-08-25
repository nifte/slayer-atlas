package com.slayeratlas.ui;

import com.slayeratlas.data.AlternativeMonsters;
import com.slayeratlas.data.SlayerMonster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class AlternativeItem extends JPanel
{
	public AlternativeItem(String alternative, SlayerMonster monster, MonsterImageLoader images, Runnable onSelect)
	{
		setName("alternative-" + AlternativeMonsters.slug(alternative));
		setLayout(new BorderLayout(8, 0));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

		add(portrait(alternative, monster, images), BorderLayout.WEST);

		JLabel name = new JLabel(MonsterName.display(alternative));
		name.setName("alternative-name");
		name.setForeground(Color.WHITE);
		name.setFont(PanelFonts.body());
		add(name, BorderLayout.CENTER);

		PanelWidgets.makeHoverable(this, onSelect);
	}

	private static MonsterPortrait portrait(String alternative, SlayerMonster monster, MonsterImageLoader images)
	{
		if (monster != null)
		{
			return new MonsterPortrait(monster, MonsterImageSizes.LIST, images);
		}
		return new MonsterPortrait(AlternativeMonsters.imageFile(alternative), MonsterImageSizes.LIST, images);
	}
}
