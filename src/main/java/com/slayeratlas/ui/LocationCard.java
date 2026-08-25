package com.slayeratlas.ui;

import com.slayeratlas.data.MonsterLocation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class LocationCard extends ViewportWidthPanel
{
	private final JLabel chevron;
	private final JPanel details;
	private boolean expanded;

	public LocationCard(MonsterLocation location, JButton pathHere)
	{
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(restBorder());
		setName("location-" + location.getId());

		chevron = new JLabel(ChevronIcon.collapsed());
		chevron.setName("location-chevron");
		chevron.setVerticalAlignment(SwingConstants.CENTER);
		chevron.setBorder(new EmptyBorder(0, 6, 0, 0));

		JTextArea name = PanelWidgets.wrappingText(location.getName(), Color.WHITE, PanelFonts.heading());
		name.setName("location-name");

		JPanel header = new JPanel(new BorderLayout())
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		header.setOpaque(false);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.add(name, BorderLayout.CENTER);
		header.add(chevron, BorderLayout.EAST);
		add(header);
		PanelWidgets.makeHoverable(header, this::toggle, this::setHovered);

		details = PanelWidgets.vertical();
		details.setName("location-details");
		details.setOpaque(false);
		details.setVisible(false);
		details.add(Box.createVerticalStrut(4));
		PanelWidgets.addBullets(details, location.getTravel());
		details.add(Box.createVerticalStrut(4));
		details.add(pathHere);
		add(details);
	}

	public boolean isExpanded()
	{
		return expanded;
	}

	private void toggle()
	{
		expanded = !expanded;
		details.setVisible(expanded);
		chevron.setIcon(expanded ? ChevronIcon.expanded() : ChevronIcon.collapsed());
		revalidate();
		repaint();
	}

	private void setHovered(boolean hovered)
	{
		setBorder(hovered ? hoverBorder() : restBorder());
	}

	private static Border restBorder()
	{
		return new EmptyBorder(8, 8, 8, 8);
	}

	private static Border hoverBorder()
	{
		return BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR),
			new EmptyBorder(7, 7, 7, 7));
	}
}
