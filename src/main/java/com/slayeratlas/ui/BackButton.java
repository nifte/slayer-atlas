package com.slayeratlas.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import net.runelite.client.ui.ColorScheme;

public class BackButton extends JButton
{
	public BackButton(Runnable onBack)
	{
		super(PanelCopy.BACK_TO_LIST, BackArrowIcon.icon());
		setFocusable(false);
		setOpaque(true);
		setForeground(Color.WHITE);
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setFont(PanelFonts.bodyBold());
		setHorizontalAlignment(SwingConstants.CENTER);
		setIconTextGap(8);
		setAlignmentX(LEFT_ALIGNMENT);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		addActionListener(event -> onBack.run());
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}
		});
	}
}
