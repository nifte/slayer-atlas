package com.slayeratlas.ui;

import lombok.Getter;
import net.runelite.api.gameval.SpriteID;

@Getter
public enum QuickPrayer
{
	THICK_SKIN("Thick Skin", SpriteID.Prayeron.THICK_SKIN),
	BURST_OF_STRENGTH("Burst of Strength", SpriteID.Prayeron.BURST_OF_STRENGTH),
	CLARITY_OF_THOUGHT("Clarity of Thought", SpriteID.Prayeron.CLARITY_OF_THOUGHT),
	SHARP_EYE("Sharp Eye", SpriteID.Prayeron.SHARP_EYE),
	MYSTIC_WILL("Mystic Will", SpriteID.Prayeron.MYSTIC_WILL),
	ROCK_SKIN("Rock Skin", SpriteID.Prayeron.ROCK_SKIN),
	SUPERHUMAN_STRENGTH("Superhuman Strength", SpriteID.Prayeron.SUPERHUMAN_STRENGTH),
	IMPROVED_REFLEXES("Improved Reflexes", SpriteID.Prayeron.IMPROVED_REFLEXES),
	RAPID_RESTORE("Rapid Restore", SpriteID.Prayeron.RAPID_RESTORE),
	RAPID_HEAL("Rapid Heal", SpriteID.Prayeron.RAPID_HEAL),
	PROTECT_ITEM("Protect Item", SpriteID.Prayeron.PROTECT_ITEM),
	HAWK_EYE("Hawk Eye", SpriteID.Prayeron.HAWK_EYE),
	MYSTIC_LORE("Mystic Lore", SpriteID.Prayeron.MYSTIC_LORE),
	STEEL_SKIN("Steel Skin", SpriteID.Prayeron.STEEL_SKIN),
	ULTIMATE_STRENGTH("Ultimate Strength", SpriteID.Prayeron.ULTIMATE_STRENGTH),
	INCREDIBLE_REFLEXES("Incredible Reflexes", SpriteID.Prayeron.INCREDIBLE_REFLEXES),
	PROTECT_FROM_MAGIC("Protect from Magic", SpriteID.Prayeron.PROTECT_FROM_MAGIC),
	PROTECT_FROM_MISSILES("Protect from Missiles", SpriteID.Prayeron.PROTECT_FROM_MISSILES),
	PROTECT_FROM_MELEE("Protect from Melee", SpriteID.Prayeron.PROTECT_FROM_MELEE),
	EAGLE_EYE("Eagle Eye", SpriteID.Prayeron.EAGLE_EYE),
	MYSTIC_MIGHT("Mystic Might", SpriteID.Prayeron.MYSTIC_MIGHT),
	RETRIBUTION("Retribution", SpriteID.Prayeron.RETRIBUTION),
	REDEMPTION("Redemption", SpriteID.Prayeron.REDEMPTION),
	SMITE("Smite", SpriteID.Prayeron.SMITE),
	PRESERVE("Preserve", SpriteID.Prayeron.PRESERVE),
	CHIVALRY("Chivalry", SpriteID.Prayeron.CHIVALRY),
	PIETY("Piety", SpriteID.Prayeron.PIETY),
	RIGOUR("Rigour", SpriteID.Prayeron.RIGOUR),
	AUGURY("Augury", SpriteID.Prayeron.AUGURY),
	DEADEYE("Deadeye", SpriteID.Prayeron.DEADEYE),
	MYSTIC_VIGOUR("Mystic Vigour", SpriteID.Prayeron.MYSTIC_VIGOUR);

	private final String displayName;
	private final int spriteId;

	QuickPrayer(String displayName, int spriteId)
	{
		this.displayName = displayName;
		this.spriteId = spriteId;
	}

	public static QuickPrayer named(String name)
	{
		if (name == null || name.isEmpty())
		{
			return null;
		}
		for (QuickPrayer prayer : values())
		{
			if (prayer.displayName.equalsIgnoreCase(name))
			{
				return prayer;
			}
		}
		return null;
	}
}
