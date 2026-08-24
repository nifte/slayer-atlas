package com.slayerguide.ui;

import com.google.inject.ImplementedBy;
import com.slayerguide.data.SlayerMonster;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

@ImplementedBy(WikiMonsterImageLoader.class)
public interface MonsterImageLoader
{
	void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded);

	static MonsterImageLoader none()
	{
		return (monster, size, onLoaded) ->
		{
		};
	}
}
