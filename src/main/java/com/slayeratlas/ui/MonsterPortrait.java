package com.slayeratlas.ui;

import com.slayeratlas.data.SlayerMonster;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MonsterPortrait extends JLabel
{
	private final int size;
	private final MonsterImageLoader loader;
	private final boolean urgent;
	private SlayerMonster shown;
	private SlayerMonster requested;

	public MonsterPortrait(SlayerMonster monster, int size, MonsterImageLoader loader)
	{
		this(monster, size, loader, true);
	}

	public MonsterPortrait(SlayerMonster monster, int size, MonsterImageLoader loader, boolean urgent)
	{
		this(size, loader, urgent);
		setMonster(monster);
	}

	public MonsterPortrait(String fileName, int size, MonsterImageLoader loader)
	{
		this(size, loader, true);
		requestFile(fileName);
	}

	private MonsterPortrait(int size, MonsterImageLoader loader, boolean urgent)
	{
		super(new ImageIcon(PlaceholderImages.square(size)), SwingConstants.CENTER);
		this.size = size;
		this.loader = loader;
		this.urgent = urgent;
		setOpaque(false);
		setPreferredSize(new Dimension(size, size));
		setMinimumSize(new Dimension(size, size));
		setMaximumSize(new Dimension(size, size));
	}

	public void setMonster(SlayerMonster monster)
	{
		shown = monster;
		requested = null;
		setIcon(new ImageIcon(PlaceholderImages.square(size)));
		requestImage();
	}

	private void requestImage()
	{
		if (loader == null || shown == null || shown == requested)
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
		}, urgent);
	}

	private void requestFile(String fileName)
	{
		if (loader == null || fileName == null || fileName.isEmpty())
		{
			return;
		}
		loader.loadFile(fileName, size, image ->
		{
			if (image != null)
			{
				setIcon(new ImageIcon(image));
			}
		});
	}
}
