package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.MonsterLocation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import org.junit.Test;

public class LocationCardTest
{
	@Test
	public void startsCollapsedWithNameOnly()
	{
		MonsterLocation location = iceDungeon();
		LocationCard card = new LocationCard(location, new JButton("Path here"));

		assertEquals("Asgarnian Ice Dungeon", ((JTextArea) ComponentLookup.named(card, "location-name")).getText());
		assertEquals(ChevronIcon.COLLAPSED, chevronDescription(card));
		JLabel chevron = (JLabel) ComponentLookup.named(card, "location-chevron");
		assertEquals(BorderLayout.EAST, ((BorderLayout) chevron.getParent().getLayout()).getConstraints(chevron));
		assertEquals(SwingConstants.CENTER, chevron.getVerticalAlignment());
		assertFalse(card.isExpanded());
		assertFalse(ComponentLookup.named(card, "location-details").isVisible());
		assertFalse(hasExactText(card, location.getRegion()));
	}

	@Test
	public void outlinesTheCardWhenTheNameIsHovered()
	{
		LocationCard card = new LocationCard(iceDungeon(), new JButton("Path here"));
		Component name = ComponentLookup.named(card, "location-name");
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, card.getBackground());
		assertEquals(new Insets(4, 8, 4, 8), card.getInsets());
		assertNull(lineColor(card));

		name.dispatchEvent(enter(name));
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, card.getBackground());
		assertEquals(new Insets(4, 8, 4, 8), card.getInsets());
		assertEquals(ColorScheme.MEDIUM_GRAY_COLOR, lineColor(card));

		name.dispatchEvent(exit(name));
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, card.getBackground());
		assertNull(lineColor(card));
	}

	@Test
	public void expandsWhenTheNameIsClicked()
	{
		MonsterLocation location = iceDungeon();
		LocationCard card = new LocationCard(location, new JButton("Path here"));
		Component name = ComponentLookup.named(card, "location-name");
		assertNotNull(name);

		click(name);

		assertTrue(card.isExpanded());
		assertEquals(ChevronIcon.EXPANDED, chevronDescription(card));
		assertTrue(ComponentLookup.named(card, "location-details").isVisible());
		assertEquals(
			new Insets(8, 4, 8, 4),
			((Container) ComponentLookup.named(card, "location-travel")).getInsets());
		assertTrue(ComponentLookup.containsText(card, "Path here"));
		assertTrue(ComponentLookup.containsText(card, location.getTravel().get(0)));

		click(name);

		assertFalse(card.isExpanded());
		assertEquals(ChevronIcon.COLLAPSED, chevronDescription(card));
		assertFalse(ComponentLookup.named(card, "location-details").isVisible());
	}

	@Test
	public void expandsWhenTheChevronIsClicked()
	{
		LocationCard card = new LocationCard(iceDungeon(), new JButton("Path here"));
		click(ComponentLookup.named(card, "location-chevron"));

		assertTrue(card.isExpanded());
		assertEquals(ChevronIcon.EXPANDED, chevronDescription(card));
	}

	@Test
	public void wrapsLongNamesAndKeepsTheChevronCentered()
	{
		MonsterLocation location = new MonsterDatabase(new Gson()).getLocation("black_dragon_taverley");
		LocationCard card = new LocationCard(location, new JButton("Path here"));
		JTextArea name = (JTextArea) ComponentLookup.named(card, "location-name");
		JLabel chevron = (JLabel) ComponentLookup.named(card, "location-chevron");

		assertEquals("Taverley Dungeon (black dragons)", name.getText());
		assertTrue(name.getLineWrap());
		assertTrue(name.getWrapStyleWord());
		assertEquals(SwingConstants.CENTER, chevron.getVerticalAlignment());

		layoutToWidth(card, PluginPanel.PANEL_WIDTH - 20);

		FontMetrics metrics = name.getFontMetrics(name.getFont());
		assertTrue(metrics.stringWidth(name.getText()) > name.getWidth());
		assertTrue(name.getHeight() > metrics.getHeight());
		assertEquals(name.getY() + name.getHeight() / 2, chevron.getY() + chevron.getHeight() / 2);
	}

	@Test
	public void collapsedHeaderFillsTheButton()
	{
		LocationCard card = new LocationCard(iceDungeon(), new JButton("Path here"));
		layoutToWidth(card, PluginPanel.PANEL_WIDTH - 20);

		Component header = ComponentLookup.named(card, "location-header");
		Insets insets = card.getInsets();
		assertEquals(card.getHeight() - insets.top - insets.bottom, header.getHeight());
		assertEquals(card.getWidth() - insets.left - insets.right, header.getWidth());
	}

	@Test
	public void expandsWhenCollapsedPaddingIsClicked()
	{
		LocationCard card = new LocationCard(iceDungeon(), new JButton("Path here"));
		layoutToWidth(card, PluginPanel.PANEL_WIDTH - 20);

		clickAt(card, card.getWidth() / 2, 1);
		assertTrue(card.isExpanded());

		click(ComponentLookup.named(card, "location-name"));
		layoutToWidth(card, PluginPanel.PANEL_WIDTH - 20);
		assertFalse(card.isExpanded());

		clickAt(card, card.getWidth() / 2, card.getHeight() - 1);
		assertTrue(card.isExpanded());
	}

	@Test
	public void outlinesTheCardWhenPaddingIsHovered()
	{
		LocationCard card = new LocationCard(iceDungeon(), new JButton("Path here"));
		layoutToWidth(card, PluginPanel.PANEL_WIDTH - 20);

		card.dispatchEvent(enter(card));
		assertEquals(ColorScheme.MEDIUM_GRAY_COLOR, lineColor(card));
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, card.getBackground());

		card.dispatchEvent(exit(card));
		assertNull(lineColor(card));
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, card.getBackground());
	}

	@Test
	public void clickingTravelCollapsesTheExpandedCard()
	{
		LocationCard card = new LocationCard(iceDungeon(), new JButton("Path here"));
		click(ComponentLookup.named(card, "location-name"));
		assertTrue(card.isExpanded());

		click(((Container) ComponentLookup.named(card, "location-travel")).getComponent(0));
		assertFalse(card.isExpanded());
	}

	@Test
	public void clickingAnActionButtonDoesNotCollapseTheCard()
	{
		JButton map = PanelWidgets.button(PanelCopy.SHOW_ON_MAP);
		map.setName("show-on-map");
		LocationCard card = new LocationCard(iceDungeon(), new LocationActionButtons(map, null));
		click(ComponentLookup.named(card, "location-name"));
		assertTrue(card.isExpanded());

		click(map);
		assertTrue(card.isExpanded());
		map.doClick();
		assertTrue(card.isExpanded());
	}

	@Test
	public void hoveringAnActionButtonClearsTheCardOutline()
	{
		JButton map = PanelWidgets.button(PanelCopy.SHOW_ON_MAP);
		map.setName("show-on-map");
		LocationCard card = new LocationCard(iceDungeon(), new LocationActionButtons(map, null));
		click(ComponentLookup.named(card, "location-name"));
		Component name = ComponentLookup.named(card, "location-name");

		name.dispatchEvent(enter(name));
		assertEquals(ColorScheme.MEDIUM_GRAY_COLOR, lineColor(card));

		map.dispatchEvent(enter(map));
		assertNull(lineColor(card));
	}

	private static void layoutToWidth(LocationCard card, int width)
	{
		card.setSize(width, Math.max(card.getPreferredSize().height, 8));
		layoutTree(card);
		card.setSize(width, card.getPreferredSize().height);
		layoutTree(card);
	}

	private static void layoutTree(Container container)
	{
		container.doLayout();
		for (Component child : container.getComponents())
		{
			if (child instanceof Container)
			{
				layoutTree((Container) child);
			}
		}
	}

	private static MonsterLocation iceDungeon()
	{
		return new MonsterDatabase(new Gson()).getLocation("asgarnia_ice_dungeon");
	}

	private static String chevronDescription(LocationCard card)
	{
		JLabel chevron = (JLabel) ComponentLookup.named(card, "location-chevron");
		assertNotNull(chevron);
		assertTrue(chevron.getIcon() instanceof ImageIcon);
		return ((ImageIcon) chevron.getIcon()).getDescription();
	}

	private static void click(Component component)
	{
		Container parent = component.getParent();
		if (parent != null)
		{
			parent.setSize(
				Math.max(parent.getPreferredSize().width, 8),
				Math.max(parent.getPreferredSize().height, 8));
			parent.doLayout();
		}
		clickAt(component, 1, 1);
	}

	private static void clickAt(Component component, int x, int y)
	{
		component.dispatchEvent(new MouseEvent(
			component,
			MouseEvent.MOUSE_RELEASED,
			System.currentTimeMillis(),
			0,
			x,
			y,
			1,
			false,
			MouseEvent.BUTTON1));
	}

	private static Color lineColor(LocationCard card)
	{
		if (card.getBorder() instanceof CompoundBorder)
		{
			CompoundBorder border = (CompoundBorder) card.getBorder();
			if (border.getOutsideBorder() instanceof LineBorder)
			{
				return ((LineBorder) border.getOutsideBorder()).getLineColor();
			}
		}
		return null;
	}

	private static MouseEvent enter(Component component)
	{
		return new MouseEvent(
			component,
			MouseEvent.MOUSE_ENTERED,
			System.currentTimeMillis(),
			0,
			1,
			1,
			0,
			false);
	}

	private static MouseEvent exit(Component component)
	{
		return new MouseEvent(
			component,
			MouseEvent.MOUSE_EXITED,
			System.currentTimeMillis(),
			0,
			400,
			400,
			0,
			false);
	}

	private static boolean hasExactText(Component root, String text)
	{
		if (root instanceof JLabel && text.equals(((JLabel) root).getText()))
		{
			return true;
		}
		if (root instanceof Container)
		{
			for (Component child : ((Container) root).getComponents())
			{
				if (hasExactText(child, text))
				{
					return true;
				}
			}
		}
		return false;
	}
}
