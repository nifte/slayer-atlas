package com.slayeratlas.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelHeader extends JPanel
{
	public PanelHeader(Runnable onSettings)
	{
		setName("panel-header");
		setLayout(new BorderLayout());
		setOpaque(false);
		setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel title = PanelWidgets.heading(PanelCopy.TITLE);
		title.setName("panel-title");
		add(title, BorderLayout.WEST);
		add(new SettingsButton(onSettings), BorderLayout.EAST);
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(0, getPreferredSize().height);
	}
}
