package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.UnlockedPrayers;
import net.runelite.api.gameval.SpriteID;
import org.junit.Test;

public class CombatPrayerTest
{
	@Test
	public void mapsEachGearStyleToItsCombatPrayer()
	{
		assertEquals(CombatPrayer.PIETY, CombatPrayer.forStyle(CombatStyle.MELEE));
		assertEquals(CombatPrayer.RIGOUR, CombatPrayer.forStyle(CombatStyle.RANGED));
		assertEquals(CombatPrayer.AUGURY, CombatPrayer.forStyle(CombatStyle.MAGIC));
		assertEquals(CombatPrayer.PIETY, CombatPrayer.forStyle(null));
	}

	@Test
	public void usesOfficialPrayerNamesAndSprites()
	{
		assertEquals("Piety", CombatPrayer.PIETY.getDisplayName());
		assertEquals("Rigour", CombatPrayer.RIGOUR.getDisplayName());
		assertEquals("Augury", CombatPrayer.AUGURY.getDisplayName());
		assertEquals("Eagle Eye", CombatPrayer.EAGLE_EYE.getDisplayName());
		assertEquals("Chivalry", CombatPrayer.CHIVALRY.getDisplayName());
		assertEquals(SpriteID.Prayeron.PIETY, CombatPrayer.PIETY.getSpriteId());
		assertEquals(SpriteID.Prayeron.RIGOUR, CombatPrayer.RIGOUR.getSpriteId());
		assertEquals(SpriteID.Prayeron.AUGURY, CombatPrayer.AUGURY.getSpriteId());
		assertEquals(SpriteID.Prayeron.EAGLE_EYE, CombatPrayer.EAGLE_EYE.getSpriteId());
		assertEquals(SpriteID.Prayeron.DEADEYE, CombatPrayer.DEADEYE.getSpriteId());
		assertEquals(SpriteID.Prayeron.MYSTIC_VIGOUR, CombatPrayer.MYSTIC_VIGOUR.getSpriteId());
	}

	@Test
	public void keepsTheTopPrayerWhenUnlockedFilteringIsOff()
	{
		UnlockedPrayers low = UnlockedPrayers.known(1, 1, false, false, false, false, false);
		assertEquals(CombatPrayer.RIGOUR, CombatPrayer.recommended(CombatStyle.RANGED, false, low));
		assertEquals(CombatPrayer.RIGOUR, CombatPrayer.recommended(CombatStyle.RANGED, true, UnlockedPrayers.unknown()));
		assertEquals(CombatPrayer.RIGOUR, CombatPrayer.recommended(CombatStyle.RANGED, true, null));
	}

	@Test
	public void fallsBackFromRigourToEagleEyeWhenTheScrollIsMissing()
	{
		UnlockedPrayers noScroll = UnlockedPrayers.known(99, 99, true, false, true, false, true);
		assertEquals(CombatPrayer.EAGLE_EYE, CombatPrayer.recommended(CombatStyle.RANGED, true, noScroll));
	}

	@Test
	public void usesDeadeyeWhenItIsUnlockedAndRigourIsNot()
	{
		UnlockedPrayers deadeye = UnlockedPrayers.known(70, 70, true, false, false, true, false);
		assertEquals(CombatPrayer.DEADEYE, CombatPrayer.recommended(CombatStyle.RANGED, true, deadeye));
	}

	@Test
	public void usesRigourWhenLevelDefenceAndScrollAreMet()
	{
		UnlockedPrayers max = UnlockedPrayers.known(99, 99, true, true, true, true, true);
		assertEquals(CombatPrayer.RIGOUR, CombatPrayer.recommended(CombatStyle.RANGED, true, max));
		assertEquals(CombatPrayer.PIETY, CombatPrayer.recommended(CombatStyle.MELEE, true, max));
		assertEquals(CombatPrayer.AUGURY, CombatPrayer.recommended(CombatStyle.MAGIC, true, max));
	}

	@Test
	public void fallsBackFromPietyToChivalryThenUltimateStrength()
	{
		assertEquals(
			CombatPrayer.CHIVALRY,
			CombatPrayer.recommended(
				CombatStyle.MELEE,
				true,
				UnlockedPrayers.known(65, 65, true, false, false, false, false)));
		assertEquals(
			CombatPrayer.ULTIMATE_STRENGTH,
			CombatPrayer.recommended(
				CombatStyle.MELEE,
				true,
				UnlockedPrayers.known(70, 70, false, false, false, false, false)));
		assertEquals(
			CombatPrayer.SUPERHUMAN_STRENGTH,
			CombatPrayer.recommended(
				CombatStyle.MELEE,
				true,
				UnlockedPrayers.known(20, 1, false, false, false, false, false)));
	}

	@Test
	public void fallsBackFromAuguryToMysticMightWhenTheScrollIsMissing()
	{
		assertEquals(
			CombatPrayer.MYSTIC_MIGHT,
			CombatPrayer.recommended(
				CombatStyle.MAGIC,
				true,
				UnlockedPrayers.known(99, 99, true, true, false, true, false)));
		assertEquals(
			CombatPrayer.MYSTIC_VIGOUR,
			CombatPrayer.recommended(
				CombatStyle.MAGIC,
				true,
				UnlockedPrayers.known(70, 70, true, false, false, false, true)));
	}

	@Test
	public void hidesTheCombatPrayerWhenNoTierIsUnlocked()
	{
		assertNull(CombatPrayer.recommended(
			CombatStyle.RANGED,
			true,
			UnlockedPrayers.known(1, 1, false, false, false, false, false)));
	}

	@Test
	public void requiresSeventyDefenceForRigourAndPiety()
	{
		assertFalse(CombatPrayer.RIGOUR.unlockedBy(UnlockedPrayers.known(99, 69, true, true, true, true, true)));
		assertTrue(CombatPrayer.RIGOUR.unlockedBy(UnlockedPrayers.known(99, 70, true, true, true, true, true)));
		assertFalse(CombatPrayer.PIETY.unlockedBy(UnlockedPrayers.known(99, 69, true, true, true, true, true)));
		assertTrue(CombatPrayer.CHIVALRY.unlockedBy(UnlockedPrayers.known(60, 65, true, false, false, false, false)));
	}
}
