package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class MonsterDetailHeader extends JPanel
{
	public MonsterDetailHeader(SlayerMonster monster, Runnable onBack)
	{
		setLayout(new BorderLayout(8, 0));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(0, 0, 8, 0));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel backWrap = new JPanel(new BorderLayout());
		backWrap.setOpaque(false);
		backWrap.add(new BackButton(onBack), BorderLayout.NORTH);
		add(backWrap, BorderLayout.WEST);

		JPanel text = PanelWidgets.vertical();
		text.setOpaque(false);
		JTextArea name = PanelWidgets.wrappingText(monster.getName(), Color.WHITE, PanelFonts.heading());
		text.add(name);
		String meta = MonsterHeaderText.meta(monster);
		if (!meta.isEmpty())
		{
			JTextArea metaLabel = PanelWidgets.wrappingText(meta, ColorScheme.LIGHT_GRAY_COLOR, PanelFonts.body());
			text.add(metaLabel);
		}
		add(text, BorderLayout.CENTER);
	}
}
