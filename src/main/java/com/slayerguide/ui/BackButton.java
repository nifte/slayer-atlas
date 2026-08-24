package com.slayerguide.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import net.runelite.client.ui.ColorScheme;

public class BackButton extends JButton
{
	static final int SIZE = 32;

	public BackButton(Runnable onBack)
	{
		setPreferredSize(new Dimension(SIZE, SIZE));
		setMinimumSize(new Dimension(SIZE, SIZE));
		setMaximumSize(new Dimension(SIZE, SIZE));
		setFocusable(false);
		setOpaque(true);
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_HOVER_COLOR));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setToolTipText("Back to list");
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

	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		int left = centerX - 6;
		int right = centerX + 6;
		g.drawLine(right, centerY, left, centerY);
		g.drawLine(left, centerY, left + 6, centerY - 6);
		g.drawLine(left, centerY, left + 6, centerY + 6);
		g.dispose();
	}
}
