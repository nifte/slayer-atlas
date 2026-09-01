package com.slayeratlas.ui;

import java.awt.Dimension;
import javax.swing.JLabel;
import net.runelite.client.game.SpriteManager;

public class PrayerIcon extends JLabel
{
	private static final int SIZE = 30;

	public PrayerIcon(ProtectionPrayer prayer, SpriteManager sprites)
	{
		this(prayer.getDisplayName(), prayer.getSpriteId(), sprites);
	}

	public PrayerIcon(CombatPrayer prayer, SpriteManager sprites)
	{
		this(prayer.getDisplayName(), prayer.getSpriteId(), sprites);
	}

	private PrayerIcon(String displayName, int spriteId, SpriteManager sprites)
	{
		setToolTipText(displayName);
		setComponentPopupMenu(ItemWikiMenu.forName(displayName));
		setOpaque(false);
		setPreferredSize(new Dimension(SIZE, SIZE));
		setMinimumSize(new Dimension(SIZE, SIZE));
		setMaximumSize(new Dimension(SIZE, SIZE));
		if (sprites != null)
		{
			sprites.addSpriteTo(this, spriteId, 0);
		}
	}
}
