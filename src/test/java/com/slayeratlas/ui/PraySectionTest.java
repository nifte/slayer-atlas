package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.SlayerAtlasConfig;
import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.UnlockedPrayers;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.junit.Test;

public class PraySectionTest
{
	@Test
	public void showsEagleEyeWhenRigourIsLocked()
	{
		GearRecommendationService service = new GearRecommendationService(unlockedConfig(true));
		service.setUnlockedPrayers(UnlockedPrayers.known(70, 70, true, false, false, false, false));
		PraySection section = new PraySection(wyverns(), null, service);
		section.setStyle(CombatStyle.RANGED);
		assertEquals(
			"Eagle Eye",
			((JLabel) ComponentLookup.named(section, "combat-pray-icon")).getToolTipText());
	}

	@Test
	public void keepsRigourWhenUnlockedFilteringIsOff()
	{
		GearRecommendationService service = new GearRecommendationService(unlockedConfig(false));
		service.setUnlockedPrayers(UnlockedPrayers.known(1, 1, false, false, false, false, false));
		PraySection section = new PraySection(wyverns(), null, service);
		section.setStyle(CombatStyle.RANGED);
		assertEquals(
			"Rigour",
			((JLabel) ComponentLookup.named(section, "combat-pray-icon")).getToolTipText());
	}

	@Test
	public void prayerIconsShowAWikiButtonOnRightClick()
	{
		PraySection section = new PraySection(wyverns(), null);
		assertWikiMenu((JLabel) ComponentLookup.named(section, "pray-icon-0"), "Protect from Melee");
		assertWikiMenu((JLabel) ComponentLookup.named(section, "combat-pray-icon"), "Piety");
	}

	@Test
	public void showsSavedQuickPrayersInsteadOfRecommendations()
	{
		PraySection section = new PraySection(wyverns(), null);
		assertEquals(
			"Protect from Melee",
			((JLabel) ComponentLookup.named(section, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Piety",
			((JLabel) ComponentLookup.named(section, "combat-pray-icon")).getToolTipText());
		section.showPrayers(CombatStyle.RANGED, List.of("Protect from Magic", "Rigour"));
		assertEquals(
			"Protect from Magic",
			((JLabel) ComponentLookup.named(section, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Rigour",
			((JLabel) ComponentLookup.named(section, "pray-icon-1")).getToolTipText());
		assertNull(ComponentLookup.named(section, "combat-pray-icon"));
		section.setStyle(CombatStyle.RANGED);
		assertEquals(
			"Protect from Melee",
			((JLabel) ComponentLookup.named(section, "pray-icon-0")).getToolTipText());
		assertEquals(
			"Rigour",
			((JLabel) ComponentLookup.named(section, "combat-pray-icon")).getToolTipText());
	}

	@Test
	public void hidesProtectFromMeleeBelowLevelFortyThree()
	{
		GearRecommendationService service = new GearRecommendationService(unlockedConfig(true));
		service.setUnlockedPrayers(UnlockedPrayers.known(40, 40, false, false, false, false, false));
		PraySection section = new PraySection(wyverns(), null, service);
		assertNull(ComponentLookup.named(section, "pray-icon-0"));
		assertEquals(
			"Ultimate Strength",
			((JLabel) ComponentLookup.named(section, "combat-pray-icon")).getToolTipText());
	}

	private static void assertWikiMenu(JLabel icon, String pageName)
	{
		assertEquals(pageName, icon.getToolTipText());
		JPopupMenu menu = icon.getComponentPopupMenu();
		assertNotNull(menu);
		JMenuItem wiki = (JMenuItem) ComponentLookup.named(menu, "item-wiki");
		assertNotNull(wiki);
		assertEquals(PanelCopy.OPEN_WIKI, wiki.getText());
	}

	private static SlayerMonster wyverns()
	{
		return new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
	}

	private static SlayerAtlasConfig unlockedConfig(boolean onlyUnlocked)
	{
		return new SlayerAtlasConfig()
		{
			@Override
			public boolean onlyRecommendUnlockedPrayers()
			{
				return onlyUnlocked;
			}
		};
	}
}
