package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.ComponentLookup;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import net.runelite.client.ui.PluginPanel;
import org.junit.Test;

public class LocationActionButtonsTest
{
	@Test
	public void placesPathAndMapSideBySide()
	{
		AtomicInteger paths = new AtomicInteger();
		AtomicInteger maps = new AtomicInteger();
		JButton path = PanelWidgets.button(PanelCopy.PATH_HERE);
		path.setName("path-here");
		path.addActionListener(event -> paths.incrementAndGet());
		JButton map = PanelWidgets.button(PanelCopy.SHOW_ON_MAP);
		map.setName("show-on-map");
		map.addActionListener(event -> maps.incrementAndGet());

		LocationActionButtons row = new LocationActionButtons(map, path);

		assertTrue(row.getLayout() instanceof GridLayout);
		assertEquals(2, ((GridLayout) row.getLayout()).getColumns());
		assertEquals(path, ComponentLookup.named(row, "path-here"));
		assertEquals(map, ComponentLookup.named(row, "show-on-map"));
		assertEquals(0, row.getComponentZOrder(map));
		assertEquals(1, row.getComponentZOrder(path));
		assertEquals(path.getPreferredSize().height, row.getPreferredSize().height);
		assertEquals(map.getPreferredSize().height, row.getPreferredSize().height);

		path.doClick();
		map.doClick();
		assertEquals(1, paths.get());
		assertEquals(1, maps.get());
		assertEquals(Cursor.DEFAULT_CURSOR, path.getCursor().getType());
		assertEquals(path.getCursor(), map.getCursor());
		assertTrue(path.isRolloverEnabled());
		assertTrue(map.isRolloverEnabled());
	}

	@Test
	public void stretchesShowOnMapWhenPathHereIsHidden()
	{
		JButton map = PanelWidgets.button(PanelCopy.SHOW_ON_MAP);
		map.setName("show-on-map");
		LocationActionButtons row = new LocationActionButtons(map, null);

		assertTrue(row.getLayout() instanceof GridLayout);
		assertEquals(1, ((GridLayout) row.getLayout()).getColumns());
		assertEquals(1, row.getComponentCount());
		assertEquals(map, ComponentLookup.named(row, "show-on-map"));
		assertEquals(map.getPreferredSize().height, row.getPreferredSize().height);
	}

	@Test
	public void canShowAndHidePathHereWithoutRebuilding()
	{
		JButton path = PanelWidgets.button(PanelCopy.PATH_HERE);
		path.setName("path-here");
		JButton map = PanelWidgets.button(PanelCopy.SHOW_ON_MAP);
		map.setName("show-on-map");
		LocationActionButtons row = new LocationActionButtons(map, path, false);

		assertEquals(1, ((GridLayout) row.getLayout()).getColumns());
		assertEquals(1, row.getComponentCount());
		assertNull(ComponentLookup.named(row, "path-here"));

		row.setPathVisible(true);

		assertEquals(2, ((GridLayout) row.getLayout()).getColumns());
		assertEquals(2, row.getComponentCount());
		assertEquals(path, ComponentLookup.named(row, "path-here"));
		assertEquals(0, row.getComponentZOrder(map));
		assertEquals(1, row.getComponentZOrder(path));

		row.setPathVisible(false);

		assertEquals(1, ((GridLayout) row.getLayout()).getColumns());
		assertEquals(1, row.getComponentCount());
		assertNull(ComponentLookup.named(row, "path-here"));
	}

	@Test
	public void reducesHorizontalPaddingSoShowOnMapFits()
	{
		JButton untouched = PanelWidgets.button(PanelCopy.SHOW_ON_MAP);
		JButton path = PanelWidgets.button(PanelCopy.PATH_HERE);
		JButton map = PanelWidgets.button(PanelCopy.SHOW_ON_MAP);
		LocationActionButtons row = new LocationActionButtons(map, path);

		assertEquals(untouched.getMargin().top, map.getMargin().top);
		assertEquals(untouched.getMargin().bottom, map.getMargin().bottom);
		assertTrue(map.getMargin().left < untouched.getMargin().left);
		assertTrue(map.getMargin().right < untouched.getMargin().right);
		assertEquals(path.getMargin(), map.getMargin());

		row.setSize(PluginPanel.PANEL_WIDTH - 36, row.getPreferredSize().height);
		row.doLayout();

		FontMetrics metrics = map.getFontMetrics(map.getFont());
		Insets insets = map.getInsets();
		assertTrue(metrics.stringWidth(PanelCopy.SHOW_ON_MAP) + insets.left + insets.right <= map.getWidth());
	}
}
