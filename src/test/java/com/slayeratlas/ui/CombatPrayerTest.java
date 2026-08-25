package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.slayeratlas.data.CombatStyle;
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
		assertEquals(SpriteID.Prayeron.PIETY, CombatPrayer.PIETY.getSpriteId());
		assertEquals(SpriteID.Prayeron.RIGOUR, CombatPrayer.RIGOUR.getSpriteId());
		assertEquals(SpriteID.Prayeron.AUGURY, CombatPrayer.AUGURY.getSpriteId());
	}
}
