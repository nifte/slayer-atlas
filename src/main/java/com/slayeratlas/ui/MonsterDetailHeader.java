package com.slayeratlas.ui;

import com.slayeratlas.data.CurrentSlayerTask;
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
	private final SlayerMonster monster;
	private final JTextArea name;

	public MonsterDetailHeader(
		SlayerMonster monster,
		MonsterImageLoader images,
		Runnable onBack,
		Runnable onWiki,
		Runnable onDps)
	{
		this(monster, images, onBack, onWiki, onDps, null);
	}

	public MonsterDetailHeader(
		SlayerMonster monster,
		MonsterImageLoader images,
		Runnable onBack,
		Runnable onWiki,
		Runnable onDps,
		CurrentSlayerTask task)
	{
		this.monster = monster;
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
		text.setName("detail-header-text");
		text.setOpaque(false);
		name = PanelWidgets.wrappingText(MonsterTitleText.display(monster, task), Color.WHITE, PanelFonts.heading());
		name.setName("detail-header-name");
		text.add(name);
		for (String line : MonsterHeaderText.lines(monster))
		{
			text.add(PanelWidgets.wrappingText(line, ColorScheme.LIGHT_GRAY_COLOR, PanelFonts.body()));
		}
		String dpsUrl = DpsCalculatorUrl.fromMonster(monster);
		text.add(Box.createVerticalStrut(6));
		text.add(new HeaderActionButtons(onWiki, dpsUrl.isEmpty() ? null : onDps));
		identity.add(text, BorderLayout.CENTER);
		add(identity);
	}

	public void setTask(CurrentSlayerTask task)
	{
		name.setText(MonsterTitleText.display(monster, task));
		revalidate();
		repaint();
	}
}
