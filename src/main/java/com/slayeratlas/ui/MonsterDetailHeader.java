package com.slayeratlas.ui;

import com.slayeratlas.data.SlayerMonster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class MonsterDetailHeader extends JPanel
{
	public MonsterDetailHeader(
		SlayerMonster monster,
		MonsterImageLoader images,
		Runnable onBack)
	{
		setName("detail-header");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(0, 0, 8, 0));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		add(new BackButton(onBack));
		add(Box.createVerticalStrut(8));

		JPanel identity = new JPanel(new BorderLayout(8, 0));
		identity.setOpaque(false);
		identity.setAlignmentX(Component.LEFT_ALIGNMENT);
		identity.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JPanel portraitWrap = new JPanel(new BorderLayout());
		portraitWrap.setOpaque(false);
		portraitWrap.add(new MonsterPortrait(monster, MonsterImageSizes.DETAIL, images), BorderLayout.NORTH);
		identity.add(portraitWrap, BorderLayout.WEST);

		JPanel text = PanelWidgets.vertical();
		text.setOpaque(false);
		JTextArea name = PanelWidgets.wrappingText(MonsterName.display(monster.getName()), Color.WHITE, PanelFonts.heading());
		text.add(name);
		for (String line : MonsterHeaderText.lines(monster))
		{
			text.add(PanelWidgets.wrappingText(line, ColorScheme.LIGHT_GRAY_COLOR, PanelFonts.body()));
		}
		identity.add(text, BorderLayout.CENTER);
		add(identity);
	}
}
