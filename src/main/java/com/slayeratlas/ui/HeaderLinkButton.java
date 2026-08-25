package com.slayeratlas.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import net.runelite.client.ui.ColorScheme;

public class HeaderLinkButton extends JButton
{
	public HeaderLinkButton(String name, String label, Runnable onClick)
	{
		super(label, ExternalLinkIcon.icon());
		setName(name);
		setFocusable(false);
		setForeground(Color.WHITE);
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setFont(PanelFonts.bodyBold());
		setHorizontalAlignment(SwingConstants.CENTER);
		setHorizontalTextPosition(SwingConstants.LEFT);
		setIconTextGap(6);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		if (onClick != null)
		{
			addActionListener(event -> onClick.run());
		}
	}
}
