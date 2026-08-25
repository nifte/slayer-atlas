package com.slayeratlas.ui;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

final class WikiImageWaiter
{
	private final int size;
	private final Consumer<BufferedImage> onLoaded;

	WikiImageWaiter(int size, Consumer<BufferedImage> onLoaded)
	{
		this.size = size;
		this.onLoaded = onLoaded;
	}

	int size()
	{
		return size;
	}

	Consumer<BufferedImage> onLoaded()
	{
		return onLoaded;
	}
}
