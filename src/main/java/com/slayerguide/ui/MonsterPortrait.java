package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MonsterPortrait extends JLabel
{
	private final int size;
	private final MonsterImageLoader loader;
	private SlayerMonster shown;

	public MonsterPortrait(SlayerMonster monster, int size, MonsterImageLoader loader)
	{
		super(new ImageIcon(PlaceholderImages.square(size)), SwingConstants.CENTER);
		this.size = size;
		this.loader = loader;
		setOpaque(false);
		setPreferredSize(new Dimension(size, size));
		setMinimumSize(new Dimension(size, size));
		setMaximumSize(new Dimension(size, size));
		setMonster(monster);
	}

	public void setMonster(SlayerMonster monster)
	{
		shown = monster;
		setIcon(new ImageIcon(PlaceholderImages.square(size)));
		if (loader == null || monster == null)
		{
			return;
		}
		SlayerMonster requested = monster;
		loader.load(monster, size, image ->
		{
			if (image != null && requested == shown)
			{
				setIcon(new ImageIcon(image));
			}
		});
	}
}
