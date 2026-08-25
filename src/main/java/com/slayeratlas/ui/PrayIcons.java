package com.slayeratlas.ui;

import java.awt.Component;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import net.runelite.client.game.SpriteManager;

public class PrayIcons extends JPanel
{
	public PrayIcons(List<ProtectionPrayer> prayers, CombatPrayer combat, SpriteManager sprites)
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setName("pray-icons");

		int index = 0;
		for (ProtectionPrayer prayer : prayers)
		{
			if (index > 0)
			{
				add(Box.createHorizontalStrut(6));
			}
			PrayerIcon icon = new PrayerIcon(prayer, sprites);
			icon.setName("pray-icon-" + index);
			add(icon);
			index++;
		}
		if (combat != null)
		{
			if (index > 0)
			{
				add(Box.createHorizontalStrut(6));
			}
			PrayerIcon icon = new PrayerIcon(combat, sprites);
			icon.setName("combat-pray-icon");
			add(icon);
		}
		add(Box.createHorizontalGlue());
	}
}
