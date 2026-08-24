package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import org.junit.Test;

public class MonsterImageScalerTest
{
	@Test
	public void fitsNonSquareSourceIntoSquare()
	{
		BufferedImage source = new BufferedImage(40, 20, BufferedImage.TYPE_INT_ARGB);
		BufferedImage fitted = MonsterImageScaler.fitSquare(source, 20);
		assertEquals(20, fitted.getWidth());
		assertEquals(20, fitted.getHeight());
	}
}
