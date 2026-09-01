package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import org.junit.Test;

public class MonsterDetailHeaderTest
{
	@Test
	public void putsBackIdentityAndWikiDpsInTheStickyHeader()
	{
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		MonsterDetailHeader header = header(wyverns);

		assertTrue(ComponentLookup.containsText(header, PanelCopy.BACK_TO_LIST));
		assertTrue(ComponentLookup.containsText(header, "Skeletal Wyverns"));
		assertTrue(ComponentLookup.containsText(header, "Slayer level: 72"));
		assertTrue(ComponentLookup.containsText(header, "Combat level: 140"));
		assertTrue(ComponentLookup.containsText(header, "Type: Draconic"));
		assertFalse(ComponentLookup.containsText(header, "lvl 140 dragon"));
		assertFalse(ComponentLookup.containsText(header, "Open OSRS Wiki"));
		assertEquals(8, header.getInsets().bottom);

		Container text = (Container) ComponentLookup.named(header, "detail-header-text");
		assertNotNull(text);
		assertEquals("wiki-dps-buttons", text.getComponent(text.getComponentCount() - 1).getName());
		assertTrue(((JButton) ComponentLookup.named(header, "open-dps")).isEnabled());
	}

	@Test
	public void titleCasesTheMonsterName()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		assertTrue(ComponentLookup.containsText(header(dragons), "Black Dragons"));
	}

	@Test
	public void wikiAndDpsInTheHeaderOpenTheirLinks()
	{
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		AtomicInteger wiki = new AtomicInteger();
		AtomicInteger dps = new AtomicInteger();
		MonsterDetailHeader header = new MonsterDetailHeader(
			wyverns,
			MonsterImageLoader.none(),
			() ->
			{
			},
			wiki::incrementAndGet,
			dps::incrementAndGet);

		((JButton) ComponentLookup.named(header, "open-wiki")).doClick();
		((JButton) ComponentLookup.named(header, "open-dps")).doClick();
		assertEquals(1, wiki.get());
		assertEquals(1, dps.get());
	}

	private static MonsterDetailHeader header(SlayerMonster monster)
	{
		return new MonsterDetailHeader(
			monster,
			MonsterImageLoader.none(),
			() ->
			{
			},
			() ->
			{
			},
			() ->
			{
			});
	}
}
