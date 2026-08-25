package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearLoadout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;

public class StyleTabs extends JPanel
{
	public StyleTabs(List<GearLoadout> loadouts, CombatStyle selected, Consumer<CombatStyle> onSelect)
	{
		setLayout(new GridLayout(1, 0, 4, 0));
		setOpaque(false);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setName("gear-tabs");
		setVisible(loadouts.size() > 1);
		for (GearLoadout loadout : loadouts)
		{
			add(tab(loadout.getStyle(), loadout.getStyle() == selected, onSelect));
		}
	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(0, super.getMinimumSize().height);
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
	}

	private static JButton tab(CombatStyle style, boolean selected, Consumer<CombatStyle> onSelect)
	{
		JButton button = new JButton(style.displayName());
		button.setName("style-tab-" + style.name().toLowerCase());
		button.setFocusable(false);
		button.setFont(PanelFonts.bodyBold());
		button.setMargin(new Insets(2, 4, 2, 4));
		button.setBackground(selected ? ColorScheme.BRAND_ORANGE : ColorScheme.DARKER_GRAY_COLOR);
		button.setForeground(selected ? Color.BLACK : Color.WHITE);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addActionListener(event -> onSelect.accept(style));
		return button;
	}
}
