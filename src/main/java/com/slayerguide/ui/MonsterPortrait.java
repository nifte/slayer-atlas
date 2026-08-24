package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MonsterPortrait extends JLabel
{
	public MonsterPortrait(SlayerMonster monster, int size, MonsterImageLoader loader)
	{
		super(new ImageIcon(PlaceholderImages.square(size)), SwingConstants.CENTER);
		setOpaque(false);
		setPreferredSize(new Dimension(size, size));
		setMinimumSize(new Dimension(size, size));
		setMaximumSize(new Dimension(size, size));
		if (loader != null)
		{
			loader.load(monster, size, image ->
			{
				if (image != null)
				{
					setIcon(new ImageIcon(image));
				}
			});
		}
	}
}
