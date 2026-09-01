package com.slayeratlas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.data.FavoriteTasks;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.ui.PanelCopy;
import com.slayeratlas.ui.SearchFieldSupport;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.junit.Before;
import org.junit.Test;

public class SlayerAtlasPanelFavoritesTest
{
	private MonsterDatabase database;
	private SlayerAtlasPanel panel;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
		panel = new SlayerAtlasPanel(database, null, new SlayerAtlasConfig()
		{
		}, FavoriteTasks.memory());
	}

	@Test
	public void starringATaskPinsItToTheTop()
	{
		SlayerMonster first = database.getMonsters().get(0);
		SlayerMonster dust = database.findByTaskName("Dust devils");
		assertNotEquals(first.getId(), dust.getId());
		assertEquals(first.getId(), visibleMonsterIds().get(0));

		star(dust).doClick();

		assertEquals(dust.getId(), visibleMonsterIds().get(0));
		assertEquals(first.getId(), visibleMonsterIds().get(1));
		assertTrue(star(dust).isSelected());
		assertEquals(PanelCopy.UNFAVORITE, star(dust).getToolTipText());
		assertNull(panel.getSelected());
	}

	@Test
	public void unpinningRestoresAlphabeticalOrder()
	{
		SlayerMonster first = database.getMonsters().get(0);
		SlayerMonster dust = database.findByTaskName("Dust devils");
		star(dust).doClick();
		star(dust).doClick();

		assertEquals(first.getId(), visibleMonsterIds().get(0));
		assertFalseStar(dust);
	}

	@Test
	public void multipleFavoritesStayInAlphabeticalOrderAtTheTop()
	{
		SlayerMonster dust = database.findByTaskName("Dust devils");
		SlayerMonster gargoyles = database.findByTaskName("Gargoyles");
		star(gargoyles).doClick();
		star(dust).doClick();

		assertEquals(dust.getId(), visibleMonsterIds().get(0));
		assertEquals(gargoyles.getId(), visibleMonsterIds().get(1));
	}

	@Test
	public void searchKeepsFavoritesFirstAmongMatches() throws Exception
	{
		List<SlayerMonster> dragons = database.search("dragon");
		assertTrue(dragons.size() > 2);
		SlayerMonster later = dragons.get(2);

		searchInput().setText("dragon");
		flushEdt();
		star(later).doClick();

		assertEquals(later.getId(), visibleMonsterIds().get(0));
		assertEquals(dragons.get(0).getId(), visibleMonsterIds().get(1));
		assertNull(panel.getSelected());
	}

	private void assertFalseStar(SlayerMonster monster)
	{
		JToggleButton star = star(monster);
		assertFalse(star.isSelected());
		assertEquals(PanelCopy.MARK_FAVORITE, star.getToolTipText());
	}

	private JToggleButton star(SlayerMonster monster)
	{
		JToggleButton star = (JToggleButton) ComponentLookup.named(panel, "favorite-" + monster.getId());
		assertNotNull(star);
		return star;
	}

	private List<String> visibleMonsterIds()
	{
		Container list = (Container) ComponentLookup.named(panel, "monster-list");
		assertNotNull(list);
		List<String> ids = new ArrayList<>();
		for (Component child : list.getComponents())
		{
			String name = child.getName();
			if (name != null && name.startsWith("monster-"))
			{
				ids.add(name.substring("monster-".length()));
			}
		}
		return ids;
	}

	private JTextField searchInput()
	{
		return SearchFieldSupport.findTextField((Container) ComponentLookup.named(panel, "search-bar"));
	}

	private static void flushEdt() throws InterruptedException, InvocationTargetException
	{
		SwingUtilities.invokeAndWait(() ->
		{
		});
	}
}
