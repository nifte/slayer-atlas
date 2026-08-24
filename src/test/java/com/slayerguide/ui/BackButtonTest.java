package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import org.junit.Test;

public class BackButtonTest
{
	@Test
	public void isSquare()
	{
		BackButton button = new BackButton(() ->
		{
		});
		Dimension size = button.getPreferredSize();
		assertEquals(BackButton.SIZE, size.width);
		assertEquals(BackButton.SIZE, size.height);
		assertEquals(size.width, size.height);
	}
}
