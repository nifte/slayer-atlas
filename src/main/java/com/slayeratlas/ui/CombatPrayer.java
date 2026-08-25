package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import lombok.Getter;
import net.runelite.api.gameval.SpriteID;

@Getter
public enum CombatPrayer
{
	PIETY("Piety", SpriteID.Prayeron.PIETY, CombatStyle.MELEE),
	RIGOUR("Rigour", SpriteID.Prayeron.RIGOUR, CombatStyle.RANGED),
	AUGURY("Augury", SpriteID.Prayeron.AUGURY, CombatStyle.MAGIC);

	private final String displayName;
	private final int spriteId;
	private final CombatStyle style;

	CombatPrayer(String displayName, int spriteId, CombatStyle style)
	{
		this.displayName = displayName;
		this.spriteId = spriteId;
		this.style = style;
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
}
