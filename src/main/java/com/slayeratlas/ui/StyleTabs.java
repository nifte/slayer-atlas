package com.slayeratlas.ui;

import java.awt.Color;
import java.awt.Component;
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
	private static final int GAP = 4;
	private static final int WRAP_AT = 4;
	private static final int WRAP_COLUMNS = 2;

	public StyleTabs(List<GearTab> tabs, GearTab selected, Consumer<GearTab> onSelect)
	{
		List<GearTab> shown = tabs == null ? List.of() : tabs;
		int count = shown.size();
		boolean wrap = wrap(count);
		setLayout(new GridLayout(wrap ? 2 : 1, wrap ? WRAP_COLUMNS : 0, GAP, wrap ? GAP : 0));
		setOpaque(false);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setName("gear-tabs");
		setVisible(count > 1);
		for (GearTab tab : shown)
		{
			add(button(tab, tab.equals(selected), onSelect));
		}
	}

	static boolean wrap(int count)
	{
		return count == WRAP_AT;
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

	private static JButton button(GearTab tab, boolean selected, Consumer<GearTab> onSelect)
	{
		JButton button = new JButton(tab.displayName());
		button.setName(tab.componentName());
		PanelWidgets.styleButton(button);
		button.setMargin(new Insets(2, 4, 2, 4));
		button.setBackground(selected ? ColorScheme.BRAND_ORANGE : ColorScheme.DARKER_GRAY_COLOR);
		button.setForeground(selected ? Color.BLACK : Color.WHITE);
		button.addActionListener(event -> onSelect.accept(tab));
		return button;
	}
}
