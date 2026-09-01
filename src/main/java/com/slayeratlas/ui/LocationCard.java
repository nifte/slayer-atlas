package com.slayeratlas.ui;

import com.slayeratlas.data.MonsterLocation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class LocationCard extends ViewportWidthPanel
{
	private final JButton header;
	private final JLabel chevron;
	private final JPanel details;
	private boolean expanded;

	public LocationCard(MonsterLocation location, JComponent actions)
	{
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setName("location-" + location.getId());

		header = new HeaderButton();
		header.setName("location-header");
		header.addActionListener(event -> toggle());

		chevron = new JLabel(ChevronIcon.collapsed());
		chevron.setName("location-chevron");
		chevron.setVerticalAlignment(SwingConstants.CENTER);
		chevron.setBorder(new EmptyBorder(0, 6, 0, 0));

		JTextArea name = PanelWidgets.wrappingText(location.getName(), Color.WHITE, PanelFonts.heading());
		name.setName("location-name");

		header.add(name, BorderLayout.CENTER);
		header.add(chevron, BorderLayout.EAST);
		relay(name);
		relay(chevron);
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
		details.add(actions);
		add(details);
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

	private void relay(Component child)
	{
		child.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				header.getModel().setRollover(true);
				header.repaint();
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				Point point = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), header);
				if (!header.contains(point))
				{
					header.getModel().setRollover(false);
					header.repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (SwingUtilities.isLeftMouseButton(event))
				{
					header.doClick();
				}
			}
		});
	}

	private static final class HeaderButton extends JButton
	{
		private HeaderButton()
		{
			PanelWidgets.styleButton(this);
			setLayout(new BorderLayout());
			setAlignmentX(Component.LEFT_ALIGNMENT);
			setMargin(new Insets(4, 8, 4, 8));
		}

		@Override
		public Dimension getPreferredSize()
		{
			return getLayout().preferredLayoutSize(this);
		}

		@Override
		public Dimension getMinimumSize()
		{
			return getLayout().minimumLayoutSize(this);
		}

		@Override
		public Dimension getMaximumSize()
		{
			return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
		}
	}
}
