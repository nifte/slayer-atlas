package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import java.awt.Component;
import java.awt.event.MouseEvent;
import net.runelite.client.ui.ColorScheme;
import org.junit.Test;

public class MonsterListItemTest
{
	@Test
	public void previewUsesTheHoverColor()
	{
		MonsterListItem item = item();
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, item.getBackground());

		item.setPreviewed(true);
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, item.getBackground());

		item.setPreviewed(false);
		assertEquals(ColorScheme.DARKER_GRAY_COLOR, item.getBackground());
	}

	@Test
	public void hoverKeepsThePreviewHighlight()
	{
		MonsterListItem item = item();
		item.setPreviewed(true);
		item.setSize(200, 48);

		item.dispatchEvent(enter(item));
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, item.getBackground());

		item.dispatchEvent(exit(item));
		assertEquals(ColorScheme.DARKER_GRAY_HOVER_COLOR, item.getBackground());
	}

	private static MonsterListItem item()
	{
		SlayerMonster monster = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		return new MonsterListItem(monster, false, MonsterImageLoader.none(), () ->
		{
		});
	}

	private static MouseEvent enter(Component component)
	{
		return new MouseEvent(
			component,
			MouseEvent.MOUSE_ENTERED,
			System.currentTimeMillis(),
			0,
			1,
			1,
			0,
			false);
	}

	private static MouseEvent exit(Component component)
	{
		return new MouseEvent(
			component,
			MouseEvent.MOUSE_EXITED,
			System.currentTimeMillis(),
			0,
			400,
			400,
			0,
			false);
	}
}
