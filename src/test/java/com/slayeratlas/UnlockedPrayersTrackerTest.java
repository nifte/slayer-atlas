package com.slayeratlas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.Skill;
import net.runelite.api.gameval.VarbitID;
import org.junit.Test;

public class UnlockedPrayersTrackerTest
{
	@Test
	public void tracksPrayerAndDefenceLevels()
	{
		assertTrue(UnlockedPrayersTracker.tracksSkill(Skill.PRAYER));
		assertTrue(UnlockedPrayersTracker.tracksSkill(Skill.DEFENCE));
		assertFalse(UnlockedPrayersTracker.tracksSkill(Skill.ATTACK));
		assertFalse(UnlockedPrayersTracker.tracksSkill(null));
	}

	@Test
	public void tracksScrollAndKnightWavesVarbit()
	{
		assertTrue(UnlockedPrayersTracker.tracksVarbit(VarbitID.PRAYER_RIGOUR_UNLOCKED));
		assertTrue(UnlockedPrayersTracker.tracksVarbit(VarbitID.PRAYER_AUGURY_UNLOCKED));
		assertTrue(UnlockedPrayersTracker.tracksVarbit(VarbitID.PRAYER_DEADEYE_UNLOCKED));
		assertTrue(UnlockedPrayersTracker.tracksVarbit(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED));
		assertTrue(UnlockedPrayersTracker.tracksVarbit(VarbitID.KR_KNIGHTWAVES_STATE));
		assertFalse(UnlockedPrayersTracker.tracksVarbit(VarbitID.SLAYER_TARGET_BOSSID));
	}
}
