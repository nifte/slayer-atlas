package com.slayeratlas.ui;

import com.google.inject.ImplementedBy;
import com.slayeratlas.data.SlayerMonster;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

@ImplementedBy(WikiMonsterImageLoader.class)
public interface MonsterImageLoader
{
	void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded);

	default void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded, boolean urgent)
	{
		load(monster, size, onLoaded);
	}

	default void loadFile(String fileName, int size, Consumer<BufferedImage> onLoaded)
	{
	}

	default void prefetch(Iterable<SlayerMonster> monsters)
	{
	}

	static MonsterImageLoader none()
	{
		return (monster, size, onLoaded) ->
		{
		};
	}
}
