package com.slayeratlas.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JPanel;

public class LocationActionButtons extends JPanel
{
	private static final int HORIZONTAL_MARGIN = 0;
	private static final int BUTTON_GAP = 4;

	public LocationActionButtons(JButton showOnMap, JButton pathHere)
	{
		setLayout(new GridLayout(1, pathHere == null ? 1 : 2, BUTTON_GAP, 0));
		setOpaque(false);
		setName("location-actions");
		setAlignmentX(Component.LEFT_ALIGNMENT);
		tightenHorizontal(showOnMap);
		add(showOnMap);
		int height = showOnMap.getPreferredSize().height;
		if (pathHere != null)
		{
			tightenHorizontal(pathHere);
			add(pathHere);
			height = Math.max(height, pathHere.getPreferredSize().height);
		}
		setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		setMinimumSize(new Dimension(0, height));
	}

	private static void tightenHorizontal(JButton button)
	{
		Insets margin = button.getMargin();
		int top = margin == null ? 2 : margin.top;
		int bottom = margin == null ? 2 : margin.bottom;
		button.setMargin(new Insets(top, HORIZONTAL_MARGIN, bottom, HORIZONTAL_MARGIN));
	}
}
