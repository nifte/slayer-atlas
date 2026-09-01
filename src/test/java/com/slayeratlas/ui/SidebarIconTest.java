package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.SlayerAtlasPlugin;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.client.util.ImageUtil;
import org.junit.Test;

public class SidebarIconTest
{
	@Test
	public void enlargeReturnsNullWhenSourceIsNull()
	{
		assertNull(SidebarIcon.enlarge(null));
	}

	@Test
	public void enlargeCropsASquareFromTheCenter()
	{
		BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = source.createGraphics();
		graphics.setColor(Color.RED);
		graphics.fillRect(0, 0, 100, 100);
		graphics.setColor(Color.BLUE);
		graphics.fillRect(40, 40, 20, 20);
		graphics.dispose();

		BufferedImage enlarged = SidebarIcon.enlarge(source);

		int crop = (int) Math.round(100 / SidebarIcon.ENLARGE);
		assertEquals(crop, enlarged.getWidth());
		assertEquals(crop, enlarged.getHeight());
		assertEquals(Color.BLUE.getRGB(), enlarged.getRGB(enlarged.getWidth() / 2, enlarged.getHeight() / 2));
	}

	@Test
	public void enlargeMakesThePluginIconFillMoreOfTheTab()
	{
		BufferedImage source = ImageUtil.loadImageResource(SlayerAtlasPlugin.class, "icon.png");
		BufferedImage enlarged = SidebarIcon.enlarge(source);

		assertTrue(enlarged.getWidth() < source.getWidth());
		assertTrue(enlarged.getHeight() < source.getHeight());
		assertEquals(enlarged.getWidth(), enlarged.getHeight());
	}
}
