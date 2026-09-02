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

	private final JButton showOnMap;
	private final JButton pathHere;

	public LocationActionButtons(JButton showOnMap, JButton pathHere)
	{
		this(showOnMap, pathHere, pathHere != null);
	}

	public LocationActionButtons(JButton showOnMap, JButton pathHere, boolean pathVisible)
	{
		this.showOnMap = showOnMap;
		this.pathHere = pathHere;
		setLayout(new GridLayout(1, 1, BUTTON_GAP, 0));
		setOpaque(false);
		setName("location-actions");
		setAlignmentX(Component.LEFT_ALIGNMENT);
		tightenHorizontal(showOnMap);
		add(showOnMap);
		if (pathHere != null)
		{
			tightenHorizontal(pathHere);
		}
		setPathVisible(pathVisible);
	}

	public void setPathVisible(boolean visible)
	{
		boolean showing = pathHere != null && visible;
		if (pathHere != null)
		{
			if (showing && pathHere.getParent() != this)
			{
				add(pathHere);
			}
			if (!showing && pathHere.getParent() == this)
			{
				remove(pathHere);
			}
		}
		((GridLayout) getLayout()).setColumns(showing ? 2 : 1);
		int height = showOnMap.getPreferredSize().height;
		if (showing)
		{
			height = Math.max(height, pathHere.getPreferredSize().height);
		}
		setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		setMinimumSize(new Dimension(0, height));
		revalidate();
		repaint();
	}

	private static void tightenHorizontal(JButton button)
	{
		Insets margin = button.getMargin();
		int top = margin == null ? 2 : margin.top;
		int bottom = margin == null ? 2 : margin.bottom;
		button.setMargin(new Insets(top, HORIZONTAL_MARGIN, bottom, HORIZONTAL_MARGIN));
	}
}
