package com.slayeratlas.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public final class PanelWidgets
{
	private PanelWidgets()
	{
	}

	public static JPanel vertical()
	{
		return new ViewportWidthPanel();
	}

	public static JLabel title(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(Color.WHITE);
		label.setFont(PanelFonts.heading());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	public static JLabel muted(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		label.setFont(PanelFonts.body());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	public static JTextArea wrapped(String text)
	{
		return wrappingText(text, ColorScheme.LIGHT_GRAY_COLOR, PanelFonts.body());
	}

	public static JTextArea wrappingText(String text, Color color, Font font)
	{
		JTextArea area = new JTextArea(text)
		{
			@Override
			public Dimension getPreferredSize()
			{
				int width = wrapWidth(this);
				Insets insets = getInsets();
				int innerWidth = Math.max(1, width - insets.left - insets.right);
				javax.swing.text.View view = getUI().getRootView(this);
				view.setSize(innerWidth, Integer.MAX_VALUE);
				int height = (int) Math.ceil(view.getPreferredSpan(javax.swing.text.View.Y_AXIS))
					+ insets.top
					+ insets.bottom;
				int lineHeight = getFontMetrics(getFont()).getHeight();
				return new Dimension(width, Math.max(height, lineHeight + insets.top + insets.bottom));
			}

			@Override
			public Dimension getMinimumSize()
			{
				return new Dimension(0, getPreferredSize().height);
			}

			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setFocusable(false);
		area.setOpaque(false);
		area.setHighlighter(null);
		area.setBorder(BorderFactory.createEmptyBorder());
		area.setForeground(color);
		area.setFont(font);
		area.setAlignmentX(Component.LEFT_ALIGNMENT);
		DefaultCaret caret = new DefaultCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		area.setCaret(caret);
		area.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent event)
			{
				if (area.getHeight() == area.getPreferredSize().height)
				{
					return;
				}
				SwingUtilities.invokeLater(() ->
				{
					Container parent = area.getParent();
					if (parent != null && area.getHeight() != area.getPreferredSize().height)
					{
						parent.revalidate();
					}
				});
			}
		});
		return area;
	}

	private static int wrapWidth(JTextArea area)
	{
		int fallback = PluginPanel.PANEL_WIDTH - 20;
		if (area.getWidth() >= fallback / 2)
		{
			return area.getWidth();
		}
		Container parent = area.getParent();
		if (parent != null)
		{
			Insets insets = parent.getInsets();
			int inner = parent.getWidth() - insets.left - insets.right - sideColumnWidth(parent);
			if (inner >= fallback / 2)
			{
				return inner;
			}
		}
		return fallback;
	}

	private static int sideColumnWidth(Container parent)
	{
		if (!(parent.getLayout() instanceof BorderLayout))
		{
			return 0;
		}
		BorderLayout layout = (BorderLayout) parent.getLayout();
		return preferredWidth(layout.getLayoutComponent(BorderLayout.EAST))
			+ preferredWidth(layout.getLayoutComponent(BorderLayout.WEST));
	}

	private static int preferredWidth(Component component)
	{
		return component == null ? 0 : component.getPreferredSize().width;
	}

	public static JLabel sectionHeading(String heading)
	{
		return sectionHeading(heading, 16);
	}

	public static JLabel sectionHeading(String heading, int topInset)
	{
		JLabel label = new JLabel(SectionHeading.display(heading));
		label.setForeground(ColorScheme.BRAND_ORANGE);
		label.setFont(PanelFonts.bodyBold());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setBorder(new EmptyBorder(topInset, 0, 4, 0));
		return label;
	}

	public static JPanel section(String heading)
	{
		return section(heading, 16);
	}

	public static JPanel section(String heading, int topInset)
	{
		JPanel panel = vertical();
		panel.add(sectionHeading(heading, topInset));
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
		button.setFont(PanelFonts.bodyBold());
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
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
		makeHoverable(panel, onClick, hovered -> panel.setBackground(hovered ? hover : base));
	}

	public static void makeHoverable(JPanel panel, Runnable onClick, Consumer<Boolean> onHover)
	{
		MouseAdapter adapter = new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (onHover != null)
				{
					onHover.accept(true);
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), panel);
				if (!panel.contains(point) && onHover != null)
				{
					onHover.accept(false);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), panel);
				if (panel.contains(point) || clickWasOnChild(panel, e))
				{
					onClick.run();
				}
			}
		};
		addMouseListenerRecursive(panel, adapter);
	}

	private static boolean clickWasOnChild(JPanel panel, MouseEvent event)
	{
		Component source = event.getComponent();
		return source != panel && SwingUtilities.isDescendingFrom(source, panel);
	}

	private static void addMouseListenerRecursive(Component component, MouseListener listener)
	{
		component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
		label.setFont(PanelFonts.heading());
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

	public static JPanel verticallyCentered(Component child)
	{
		JPanel slot = new JPanel(new GridBagLayout());
		slot.setOpaque(false);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		slot.add(child, constraints);
		return slot;
	}
}
