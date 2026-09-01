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
		this();
		int index = 0;
		for (ProtectionPrayer prayer : prayers)
		{
			index = addIcon(new PrayerIcon(prayer, sprites), "pray-icon-" + index, index);
		}
		if (combat != null)
		{
			addIcon(new PrayerIcon(combat, sprites), "combat-pray-icon", index);
		}
		add(Box.createHorizontalGlue());
	}

	private PrayIcons()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setName("pray-icons");
	}

	private int addIcon(PrayerIcon icon, String name, int index)
	{
		if (index > 0)
		{
			add(Box.createHorizontalStrut(6));
		}
		icon.setName(name);
		add(icon);
		return index + 1;
	}
}
