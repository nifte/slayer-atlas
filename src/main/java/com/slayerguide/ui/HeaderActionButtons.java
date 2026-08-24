package com.slayerguide.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class HeaderActionButtons extends JPanel
{
	public HeaderActionButtons(Runnable onWiki, Runnable onDps)
	{
		setLayout(new GridLayout(1, 2, 8, 0));
		setOpaque(false);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

		JButton wiki = PanelWidgets.button(PanelCopy.OPEN_WIKI);
		wiki.setName("open-wiki");
		wiki.addActionListener(event -> onWiki.run());
		add(wiki);

		JButton dps = PanelWidgets.button(PanelCopy.OPEN_DPS);
		dps.setName("open-dps");
		if (onDps == null)
		{
			dps.setEnabled(false);
			dps.setToolTipText("No DPS calculator link for this monster");
		}
		else
		{
			dps.addActionListener(event -> onDps.run());
		}
		add(dps);
	}
}
