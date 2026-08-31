package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.SlayerAtlasConfig;
import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.UnlockedPrayers;
import javax.swing.JLabel;
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
