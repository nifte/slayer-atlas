package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.UnlockedPrayers;
import lombok.Getter;
import net.runelite.api.gameval.SpriteID;

@Getter
public enum CombatPrayer
{
	PIETY("Piety", SpriteID.Prayeron.PIETY, CombatStyle.MELEE, 70, 70, ExtraUnlock.KNIGHT_WAVES),
	RIGOUR("Rigour", SpriteID.Prayeron.RIGOUR, CombatStyle.RANGED, 74, 70, ExtraUnlock.RIGOUR),
	AUGURY("Augury", SpriteID.Prayeron.AUGURY, CombatStyle.MAGIC, 77, 70, ExtraUnlock.AUGURY),
	CHIVALRY("Chivalry", SpriteID.Prayeron.CHIVALRY, CombatStyle.MELEE, 60, 65, ExtraUnlock.KNIGHT_WAVES),
	DEADEYE("Deadeye", SpriteID.Prayeron.DEADEYE, CombatStyle.RANGED, 62, 0, ExtraUnlock.DEADEYE),
	MYSTIC_VIGOUR("Mystic Vigour", SpriteID.Prayeron.MYSTIC_VIGOUR, CombatStyle.MAGIC, 63, 0, ExtraUnlock.MYSTIC_VIGOUR),
	ULTIMATE_STRENGTH("Ultimate Strength", SpriteID.Prayeron.ULTIMATE_STRENGTH, CombatStyle.MELEE, 31, 0, ExtraUnlock.NONE),
	EAGLE_EYE("Eagle Eye", SpriteID.Prayeron.EAGLE_EYE, CombatStyle.RANGED, 44, 0, ExtraUnlock.NONE),
	MYSTIC_MIGHT("Mystic Might", SpriteID.Prayeron.MYSTIC_MIGHT, CombatStyle.MAGIC, 45, 0, ExtraUnlock.NONE),
	SUPERHUMAN_STRENGTH("Superhuman Strength", SpriteID.Prayeron.SUPERHUMAN_STRENGTH, CombatStyle.MELEE, 13, 0, ExtraUnlock.NONE),
	HAWK_EYE("Hawk Eye", SpriteID.Prayeron.HAWK_EYE, CombatStyle.RANGED, 26, 0, ExtraUnlock.NONE),
	MYSTIC_LORE("Mystic Lore", SpriteID.Prayeron.MYSTIC_LORE, CombatStyle.MAGIC, 27, 0, ExtraUnlock.NONE),
	BURST_OF_STRENGTH("Burst of Strength", SpriteID.Prayeron.BURST_OF_STRENGTH, CombatStyle.MELEE, 4, 0, ExtraUnlock.NONE),
	SHARP_EYE("Sharp Eye", SpriteID.Prayeron.SHARP_EYE, CombatStyle.RANGED, 8, 0, ExtraUnlock.NONE),
	MYSTIC_WILL("Mystic Will", SpriteID.Prayeron.MYSTIC_WILL, CombatStyle.MAGIC, 9, 0, ExtraUnlock.NONE);

	private enum ExtraUnlock
	{
		NONE,
		KNIGHT_WAVES,
		RIGOUR,
		AUGURY,
		DEADEYE,
		MYSTIC_VIGOUR
	}

	private final String displayName;
	private final int spriteId;
	private final CombatStyle style;
	private final int prayerLevel;
	private final int defenceLevel;
	private final ExtraUnlock extraUnlock;

	CombatPrayer(
		String displayName,
		int spriteId,
		CombatStyle style,
		int prayerLevel,
		int defenceLevel,
		ExtraUnlock extraUnlock)
	{
		this.displayName = displayName;
		this.spriteId = spriteId;
		this.style = style;
		this.prayerLevel = prayerLevel;
		this.defenceLevel = defenceLevel;
		this.extraUnlock = extraUnlock;
	}

	public static CombatPrayer forStyle(CombatStyle style)
	{
		for (CombatPrayer prayer : values())
		{
			if (prayer.style == style)
			{
				return prayer;
			}
		}
		return PIETY;
	}

	public static CombatPrayer recommended(CombatStyle style, boolean onlyUnlocked, UnlockedPrayers unlocks)
	{
		if (!onlyUnlocked || unlocks == null || !unlocks.known())
		{
			return forStyle(style);
		}
		for (CombatPrayer prayer : ladder(style))
		{
			if (prayer.unlockedBy(unlocks))
			{
				return prayer;
			}
		}
		return null;
	}

	boolean unlockedBy(UnlockedPrayers unlocks)
	{
		if (unlocks == null || !unlocks.known())
		{
			return false;
		}
		if (unlocks.prayerLevel() < prayerLevel)
		{
			return false;
		}
		if (defenceLevel > 0 && unlocks.defenceLevel() < defenceLevel)
		{
			return false;
		}
		switch (extraUnlock)
		{
			case KNIGHT_WAVES:
				return unlocks.knightWaves();
			case RIGOUR:
				return unlocks.rigour();
			case AUGURY:
				return unlocks.augury();
			case DEADEYE:
				return unlocks.deadeye();
			case MYSTIC_VIGOUR:
				return unlocks.mysticVigour();
			case NONE:
			default:
				return true;
		}
	}

	private static CombatPrayer[] ladder(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return new CombatPrayer[] {RIGOUR, DEADEYE, EAGLE_EYE, HAWK_EYE, SHARP_EYE};
		}
		if (style == CombatStyle.MAGIC)
		{
			return new CombatPrayer[] {AUGURY, MYSTIC_VIGOUR, MYSTIC_MIGHT, MYSTIC_LORE, MYSTIC_WILL};
		}
		return new CombatPrayer[] {PIETY, CHIVALRY, ULTIMATE_STRENGTH, SUPERHUMAN_STRENGTH, BURST_OF_STRENGTH};
	}
}
