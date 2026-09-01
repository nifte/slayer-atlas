package com.slayeratlas.ui;

import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class HeaderLinkButton extends JButton
{
	public HeaderLinkButton(String name, String label, Runnable onClick)
	{
		super(label, ExternalLinkIcon.icon());
		setName(name);
		PanelWidgets.styleButton(this);
		setHorizontalAlignment(SwingConstants.CENTER);
		setHorizontalTextPosition(SwingConstants.LEFT);
		setIconTextGap(4);
		Insets margin = getMargin();
		setMargin(new Insets(margin == null ? 2 : margin.top, 2, margin == null ? 2 : margin.bottom, 2));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		if (onClick != null)
		{
			addActionListener(event -> onClick.run());
		}
	}
}
