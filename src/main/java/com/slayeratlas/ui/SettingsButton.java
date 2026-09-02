package com.slayeratlas.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class SettingsButton extends JButton
{
	private static final int SIZE = 20;

	public SettingsButton(Runnable onOpen)
	{
		super(CogIcon.idle());
		setName("open-settings");
		setRolloverIcon(CogIcon.hover());
		setRolloverEnabled(true);
		setFocusable(false);
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
		setCursor(Cursor.getDefaultCursor());
		setPreferredSize(new Dimension(SIZE, SIZE));
		setMinimumSize(new Dimension(SIZE, SIZE));
		setMaximumSize(new Dimension(SIZE, Integer.MAX_VALUE));
		setToolTipText(PanelCopy.OPEN_SETTINGS);
		addActionListener(event ->
		{
			if (onOpen != null)
			{
				onOpen.run();
			}
		});
	}
}
