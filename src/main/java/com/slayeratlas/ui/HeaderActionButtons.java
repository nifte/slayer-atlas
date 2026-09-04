package com.slayeratlas.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;

public class HeaderActionButtons extends JPanel
{
	public HeaderActionButtons(Runnable onWiki, Runnable onDps)
	{
		setLayout(new GridLayout(1, 2, 8, 0));
		setOpaque(false);
		setName("wiki-dps-buttons");
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

		HeaderLinkButton wiki = new HeaderLinkButton("open-wiki", PanelCopy.OPEN_WIKI, onWiki);
		wiki.setToolTipText(PanelCopy.OPEN_WIKI_TOOLTIP);
		add(wiki);

		HeaderLinkButton dps = new HeaderLinkButton("open-dps", PanelCopy.OPEN_DPS, onDps);
		if (onDps == null)
		{
			dps.setEnabled(false);
			dps.setToolTipText(PanelCopy.NO_DPS_LINK);
		}
		else
		{
			dps.setToolTipText(PanelCopy.OPEN_DPS_TOOLTIP);
		}
		add(dps);
	}
}
