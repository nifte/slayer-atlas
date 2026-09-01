package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.data.BisLoadouts;
import com.slayeratlas.data.CombatStyle;
import java.util.List;
import org.junit.Test;

public class GearTabTest
{
	@Test
	public void namesStyleAndSavedTabs()
	{
		assertEquals("Melee", GearTab.style(CombatStyle.MELEE).displayName());
		assertEquals("style-tab-melee", GearTab.style(CombatStyle.MELEE).componentName());
		assertEquals(PanelCopy.SAVED_LOADOUT, GearTab.saved().displayName());
		assertEquals("style-tab-saved", GearTab.saved().componentName());
		assertTrue(GearTab.saved().isSaved());
		assertFalse(GearTab.style(CombatStyle.RANGED).isSaved());
	}

	@Test
	public void defaultsToTheSavedTabWhenALoadoutExists()
	{
		assertEquals(GearTab.saved(), GearTab.initial(true, List.of(BisLoadouts.melee())));
		assertEquals(GearTab.style(CombatStyle.MELEE), GearTab.initial(false, List.of(BisLoadouts.melee())));
		assertEquals(GearTab.style(CombatStyle.MELEE), GearTab.initial(false, List.of()));
	}

	@Test
	public void appendsSavedAfterStyleTabs()
	{
		List<GearTab> tabs = GearTab.of(
			List.of(BisLoadouts.melee(), BisLoadouts.ranged(), BisLoadouts.magic()),
			true);
		assertEquals(4, tabs.size());
		assertEquals(GearTab.style(CombatStyle.MELEE), tabs.get(0));
		assertEquals(GearTab.style(CombatStyle.RANGED), tabs.get(1));
		assertEquals(GearTab.style(CombatStyle.MAGIC), tabs.get(2));
		assertEquals(GearTab.saved(), tabs.get(3));
	}
}
