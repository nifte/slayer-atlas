package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.JToggleButton;
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

	@Test
	public void starTogglesFavoriteWithoutOpeningTheTask()
	{
		AtomicBoolean opened = new AtomicBoolean();
		AtomicReference<Boolean> favorite = new AtomicReference<>();
		MonsterListItem item = item(favorite::set, () -> opened.set(true));
		JToggleButton star = star(item);

		assertEquals(PanelCopy.MARK_FAVORITE, star.getToolTipText());
		assertEquals(StarIcon.off(), star.getIcon());
		star.doClick();

		assertTrue(star.isSelected());
		assertEquals(Boolean.TRUE, favorite.get());
		assertEquals(PanelCopy.UNFAVORITE, star.getToolTipText());
		assertFalse(opened.get());
	}

	@Test
	public void starBrightensOnHover()
	{
		MonsterListItem item = item();
		JToggleButton star = star(item);
		star.setSize(24, 24);

		assertEquals(StarIcon.offHover(), star.getRolloverIcon());
		assertEquals(StarIcon.onHover(), star.getRolloverSelectedIcon());
		assertFalse(star.getModel().isRollover());

		star.dispatchEvent(enter(star));
		assertTrue(star.getModel().isRollover());

		star.dispatchEvent(exit(star));
		assertFalse(star.getModel().isRollover());
	}

	@Test
	public void clickingTheRowStillOpensTheTask()
	{
		AtomicBoolean opened = new AtomicBoolean();
		MonsterListItem item = item(favorite ->
		{
		}, () -> opened.set(true));
		item.setSize(200, 48);
		click(item);
		assertTrue(opened.get());
	}

	private static MonsterListItem item()
	{
		return item(favorite ->
		{
		}, () ->
		{
		});
	}

	private static MonsterListItem item(Consumer<Boolean> onFavorite, Runnable onSelect)
	{
		SlayerMonster monster = new MonsterDatabase(new Gson()).findByTaskName("Dust devils");
		return new MonsterListItem(monster, false, false, MonsterImageLoader.none(), onFavorite, onSelect);
	}

	private static JToggleButton star(MonsterListItem item)
	{
		JToggleButton star = (JToggleButton) ComponentLookup.named(item, "favorite-" + item.getMonsterId());
		assertNotNull(star);
		return star;
	}

	private static void click(Component component)
	{
		component.dispatchEvent(new MouseEvent(
			component,
			MouseEvent.MOUSE_RELEASED,
			System.currentTimeMillis(),
			0,
			1,
			1,
			1,
			false,
			MouseEvent.BUTTON1));
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
