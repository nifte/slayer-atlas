package com.slayeratlas.ui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.BisLoadouts;
import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearLoadout;
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
			List.of(BisLoadouts.melee(), BisLoadouts.ranged(), BisLoadouts.magic()),
			CombatStyle.MAGIC,
			style ->
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
		List<GearLoadout> loadouts = List.of(BisLoadouts.melee(), BisLoadouts.ranged());
		StyleTabs tabs = new StyleTabs(loadouts, CombatStyle.MELEE, style ->
		{
		});
		assertTrue(tabs.isVisible());
		assertNotNull(ComponentLookup.named(tabs, "style-tab-melee"));
		assertNotNull(ComponentLookup.named(tabs, "style-tab-ranged"));
	}
}
