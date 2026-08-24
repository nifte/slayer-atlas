package com.slayerguide.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

public final class PanelWidgets
{
	private PanelWidgets()
	{
	}

	public static JPanel vertical()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return panel;
	}

	public static JLabel title(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(Color.WHITE);
		label.setFont(FontManager.getRunescapeBoldFont());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	public static JLabel subtitle(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(ColorScheme.BRAND_ORANGE);
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	public static JLabel muted(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	public static JTextArea wrapped(String text)
	{
		JTextArea area = new JTextArea(text);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setOpaque(false);
		area.setBorder(BorderFactory.createEmptyBorder());
		area.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		area.setFont(FontManager.getRunescapeSmallFont());
		area.setAlignmentX(Component.LEFT_ALIGNMENT);
		area.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH, Integer.MAX_VALUE));
		return area;
	}

	public static JPanel section(String heading)
	{
		JPanel panel = vertical();
		JLabel label = subtitle(heading.toUpperCase(Locale.ROOT));
		label.setBorder(new EmptyBorder(8, 0, 4, 0));
		panel.add(label);
		return panel;
	}

	public static void addBullets(JPanel parent, List<String> items)
	{
		if (items == null || items.isEmpty())
		{
			parent.add(muted("None"));
			return;
		}
		for (String item : items)
		{
			parent.add(wrapped("• " + item));
			parent.add(Box.createVerticalStrut(2));
		}
	}

	public static JButton button(String text)
	{
		JButton button = new JButton(text);
		button.setFocusable(false);
		button.setForeground(Color.WHITE);
		button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		button.setFont(FontManager.getRunescapeSmallFont().deriveFont(Font.BOLD));
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
		return button;
	}

	public static JPanel card()
	{
		JPanel panel = vertical();
		panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		panel.setBorder(new EmptyBorder(8, 8, 8, 8));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return panel;
	}

	public static void makeHoverable(JPanel panel, Runnable onClick)
	{
		Color base = ColorScheme.DARKER_GRAY_COLOR;
		Color hover = ColorScheme.DARKER_GRAY_HOVER_COLOR;
		panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		MouseAdapter adapter = new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				panel.setBackground(hover);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), panel);
				if (!panel.contains(point))
				{
					panel.setBackground(base);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), panel);
				if (panel.contains(point))
				{
					onClick.run();
				}
			}
		};
		addMouseListenerRecursive(panel, adapter);
	}

	private static void addMouseListenerRecursive(Component component, MouseListener listener)
	{
		component.addMouseListener(listener);
		if (component instanceof Container)
		{
			Container container = (Container) component;
			for (Component child : container.getComponents())
			{
				addMouseListenerRecursive(child, listener);
			}
		}
	}

	public static JLabel heading(String text)
	{
		JLabel label = new JLabel(text, SwingConstants.LEFT);
		label.setForeground(Color.WHITE);
		label.setFont(FontManager.getRunescapeBoldFont());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	public static JPanel northSouth(Component north, Component center)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		panel.add(north, BorderLayout.NORTH);
		panel.add(center, BorderLayout.CENTER);
		return panel;
	}
}
