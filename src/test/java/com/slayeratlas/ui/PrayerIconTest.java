package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.slayeratlas.ComponentLookup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.junit.Test;

public class PrayerIconTest
{
	@Test
	public void protectionIconsShowAWikiButtonOnRightClick()
	{
		assertWikiMenu(new PrayerIcon(ProtectionPrayer.MELEE, null), "Protect from Melee");
		assertWikiMenu(new PrayerIcon(ProtectionPrayer.STEEL_SKIN, null), "Steel Skin");
	}

	@Test
	public void combatIconsShowAWikiButtonOnRightClick()
	{
		assertWikiMenu(new PrayerIcon(CombatPrayer.PIETY, null), "Piety");
	}

	private static void assertWikiMenu(PrayerIcon icon, String pageName)
	{
		assertEquals(pageName, icon.getToolTipText());
		JPopupMenu menu = icon.getComponentPopupMenu();
		assertNotNull(menu);
		JMenuItem wiki = (JMenuItem) ComponentLookup.named(menu, "item-wiki");
		assertNotNull(wiki);
		assertEquals(PanelCopy.OPEN_WIKI, wiki.getText());
	}
}
