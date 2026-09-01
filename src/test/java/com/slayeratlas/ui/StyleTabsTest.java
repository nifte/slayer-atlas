package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.CombatStyle;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import org.junit.Test;

public class StyleTabsTest
{
	@Test
	public void keepsTheLastTabClearOfTheScrollbar()
	{
		StyleTabs tabs = new StyleTabs(
			List.of(
				GearTab.style(CombatStyle.MELEE),
				GearTab.style(CombatStyle.RANGED),
				GearTab.style(CombatStyle.MAGIC)),
			GearTab.style(CombatStyle.MAGIC),
			tab ->
			{
			});
		ViewportWidthPanel view = new ViewportWidthPanel();
		view.add(tabs);
		JScrollPane scroll = new JScrollPane(view);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scroll.setSize(222, 300);
		scroll.doLayout();
		view.setSize(scroll.getViewport().getExtentSize());
		view.doLayout();
		tabs.doLayout();

		JButton magic = (JButton) ComponentLookup.named(tabs, "style-tab-magic");
		Rectangle onView = SwingUtilities.convertRectangle(tabs, magic.getBounds(), view);
		assertTrue(onView.x + onView.width <= view.getWidth());
		assertTrue(onView.x + onView.width <= scroll.getViewport().getWidth());
	}

	@Test
	public void showsATabForEachLoadout()
	{
		StyleTabs tabs = new StyleTabs(
			List.of(GearTab.style(CombatStyle.MELEE), GearTab.style(CombatStyle.RANGED)),
			GearTab.style(CombatStyle.MELEE),
			tab ->
			{
			});
		assertTrue(tabs.isVisible());
		assertNotNull(ComponentLookup.named(tabs, "style-tab-melee"));
		assertNotNull(ComponentLookup.named(tabs, "style-tab-ranged"));
	}

	@Test
	public void addsASavedTabAfterTheStyleTabs()
	{
		StyleTabs tabs = new StyleTabs(
			List.of(
				GearTab.style(CombatStyle.MELEE),
				GearTab.style(CombatStyle.RANGED),
				GearTab.saved()),
			GearTab.saved(),
			tab ->
			{
			});
		assertEquals(3, tabs.getComponentCount());
		assertEquals(2, tabs.getComponentZOrder(ComponentLookup.named(tabs, "style-tab-saved")));
		assertEquals(PanelCopy.SAVED_LOADOUT, ((JButton) ComponentLookup.named(tabs, "style-tab-saved")).getText());
	}

	@Test
	public void wrapsFourTabsOntoTwoRowsOfTwo()
	{
		StyleTabs tabs = new StyleTabs(
			List.of(
				GearTab.style(CombatStyle.MELEE),
				GearTab.style(CombatStyle.RANGED),
				GearTab.style(CombatStyle.MAGIC),
				GearTab.saved()),
			GearTab.saved(),
			tab ->
			{
			});
		assertTrue(StyleTabs.wrap(4));
		assertFalse(StyleTabs.wrap(3));
		assertFalse(StyleTabs.wrap(2));
		tabs.setSize(220, 80);
		tabs.doLayout();

		JButton melee = (JButton) ComponentLookup.named(tabs, "style-tab-melee");
		JButton ranged = (JButton) ComponentLookup.named(tabs, "style-tab-ranged");
		JButton magic = (JButton) ComponentLookup.named(tabs, "style-tab-magic");
		JButton saved = (JButton) ComponentLookup.named(tabs, "style-tab-saved");
		assertEquals(melee.getY(), ranged.getY());
		assertEquals(magic.getY(), saved.getY());
		assertTrue(magic.getY() > melee.getY());
		assertEquals(melee.getX(), magic.getX());
		assertEquals(ranged.getX(), saved.getX());
	}

	@Test
	public void keepsThreeTabsOnOneRow()
	{
		StyleTabs tabs = new StyleTabs(
			List.of(
				GearTab.style(CombatStyle.MELEE),
				GearTab.style(CombatStyle.RANGED),
				GearTab.style(CombatStyle.MAGIC)),
			GearTab.style(CombatStyle.MELEE),
			tab ->
			{
			});
		tabs.setSize(220, 40);
		tabs.doLayout();

		JButton melee = (JButton) ComponentLookup.named(tabs, "style-tab-melee");
		JButton ranged = (JButton) ComponentLookup.named(tabs, "style-tab-ranged");
		JButton magic = (JButton) ComponentLookup.named(tabs, "style-tab-magic");
		assertEquals(melee.getY(), ranged.getY());
		assertEquals(ranged.getY(), magic.getY());
		assertTrue(melee.getX() < ranged.getX());
		assertTrue(ranged.getX() < magic.getX());
	}
}
