package com.slayerguide.ui;

import com.slayerguide.data.SlayerMonster;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MonsterPortrait extends JLabel
{
	private final int size;
	private final MonsterImageLoader loader;
	private SlayerMonster shown;
	private SlayerMonster requested;

	public MonsterPortrait(SlayerMonster monster, int size, MonsterImageLoader loader)
	{
		super(new ImageIcon(PlaceholderImages.square(size)), SwingConstants.CENTER);
		this.size = size;
		this.loader = loader;
		setOpaque(false);
		setPreferredSize(new Dimension(size, size));
		setMinimumSize(new Dimension(size, size));
		setMaximumSize(new Dimension(size, size));
		ViewportVisibilityBinding.bind(this, this::loadIfVisible);
		setMonster(monster);
	}

	public void setMonster(SlayerMonster monster)
	{
		shown = monster;
		requested = null;
		setIcon(new ImageIcon(PlaceholderImages.square(size)));
		SwingUtilities.invokeLater(this::loadIfVisible);
	}

	private void loadIfVisible()
	{
		if (loader == null || shown == null || shown == requested)
		{
			return;
		}
		if (!ViewportVisibilityBinding.isVisible(this))
		{
			return;
		}
		SlayerMonster monster = shown;
		requested = monster;
		loader.load(monster, size, image ->
		{
			if (image != null && monster == shown)
			{
				setIcon(new ImageIcon(image));
			}
			else if (monster == shown)
			{
				requested = null;
			}
		});
	}
}
