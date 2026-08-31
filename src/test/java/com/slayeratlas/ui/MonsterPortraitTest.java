package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import org.junit.Test;

public class MonsterPortraitTest
{
	@Test
	public void requestsTheIconWithoutWaitingForScroll()
	{
		SlayerMonster jellies = new MonsterDatabase(new Gson()).findByTaskName("Jellies");
		AtomicReference<SlayerMonster> loaded = new AtomicReference<>();
		MonsterImageLoader loader = (monster, size, onLoaded) -> loaded.set(monster);

		new MonsterPortrait(jellies, MonsterImageSizes.LIST, loader);

		assertEquals(jellies, loaded.get());
	}

	@Test
	public void showsTheLoadedImageImmediately()
	{
		SlayerMonster jellies = new MonsterDatabase(new Gson()).findByTaskName("Jellies");
		BufferedImage image = new BufferedImage(
			MonsterImageSizes.LIST,
			MonsterImageSizes.LIST,
			BufferedImage.TYPE_INT_ARGB);
		MonsterImageLoader loader = (monster, size, onLoaded) -> onLoaded.accept(image);

		MonsterPortrait portrait = new MonsterPortrait(jellies, MonsterImageSizes.LIST, loader);

		assertSame(image, ((ImageIcon) portrait.getIcon()).getImage());
	}

	@Test
	public void loadsAWikiFileWhenNoMonsterIsAvailable()
	{
		AtomicReference<String> requested = new AtomicReference<>();
		BufferedImage image = new BufferedImage(
			MonsterImageSizes.LIST,
			MonsterImageSizes.LIST,
			BufferedImage.TYPE_INT_ARGB);
		MonsterImageLoader loader = new MonsterImageLoader()
		{
			@Override
			public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded)
			{
			}

			@Override
			public void loadFile(String fileName, int size, Consumer<BufferedImage> onLoaded)
			{
				requested.set(fileName);
				onLoaded.accept(image);
			}
		};

		MonsterPortrait portrait = new MonsterPortrait("Black Heather.png", MonsterImageSizes.LIST, loader);

		assertEquals("Black Heather.png", requested.get());
		assertSame(image, ((ImageIcon) portrait.getIcon()).getImage());
	}

	@Test
	public void listPortraitsDoNotJumpTheDownloadQueue()
	{
		SlayerMonster jellies = new MonsterDatabase(new Gson()).findByTaskName("Jellies");
		boolean[] urgent = {true};
		MonsterImageLoader loader = new MonsterImageLoader()
		{
			@Override
			public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded)
			{
				load(monster, size, onLoaded, true);
			}

			@Override
			public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded, boolean priority)
			{
				urgent[0] = priority;
			}
		};

		new MonsterPortrait(jellies, MonsterImageSizes.LIST, loader, false);

		assertFalse(urgent[0]);
		new MonsterPortrait(jellies, MonsterImageSizes.LIST, loader);
		assertTrue(urgent[0]);
	}
}
