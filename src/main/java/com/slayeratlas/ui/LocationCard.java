package com.slayeratlas.ui;

import com.slayeratlas.data.MonsterLocation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class LocationCard extends ViewportWidthPanel
{
	private final JLabel chevron;
	private final JPanel details;
	private boolean expanded;

	public LocationCard(MonsterLocation location, JComponent actions)
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

		JPanel header = new JPanel(new BorderLayout());
		header.setName("location-header");
		header.setOpaque(false);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.add(name, BorderLayout.CENTER);
		header.add(chevron, BorderLayout.EAST);
		add(header);

		details = PanelWidgets.vertical();
		details.setName("location-details");
		details.setOpaque(false);
		details.setVisible(false);
		JPanel travel = PanelWidgets.vertical();
		travel.setName("location-travel");
		travel.setOpaque(false);
		travel.setBorder(new EmptyBorder(8, 4, 8, 4));
		PanelWidgets.addBullets(travel, location.getTravel());
		details.add(travel);
		add(details);
		PanelWidgets.makeHoverable(this, this::toggle, this::setHovered);
		details.add(actions);
		exemptActions(actions);
	}

	public boolean isExpanded()
	{
		return expanded;
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
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

	private void exemptActions(JComponent actions)
	{
		MouseAdapter hover = new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				setHovered(false);
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				Point onCard = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), LocationCard.this);
				if (contains(onCard) && !over(actions, onCard))
				{
					setHovered(true);
				}
			}
		};
		listen(actions, hover);
		actions.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (event.getComponent() == actions && SwingUtilities.isLeftMouseButton(event))
				{
					toggle();
				}
			}
		});
	}

	private boolean over(JComponent actions, Point onCard)
	{
		return actions.contains(SwingUtilities.convertPoint(this, onCard, actions));
	}

	private static void listen(Component component, MouseListener listener)
	{
		component.addMouseListener(listener);
		if (component instanceof Container)
		{
			for (Component child : ((Container) component).getComponents())
			{
				listen(child, listener);
			}
		}
	}

	private static Border restBorder()
	{
		return new EmptyBorder(4, 8, 4, 8);
	}

	private static Border hoverBorder()
	{
		return BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR),
			new EmptyBorder(3, 7, 3, 7));
	}
}
