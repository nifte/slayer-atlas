package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SlayerMonster;
import org.junit.Test;

public class MonsterDetailHeaderTest
{
	@Test
	public void putsBackAndIdentityInTheStickyHeader()
	{
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		MonsterDetailHeader header = new MonsterDetailHeader(
			wyverns,
			MonsterImageLoader.none(),
			() ->
			{
			});

		assertTrue(ComponentLookup.containsText(header, PanelCopy.BACK_TO_LIST));
		assertTrue(ComponentLookup.containsText(header, "Skeletal Wyverns"));
		assertTrue(ComponentLookup.containsText(header, "Slayer level: 72"));
		assertTrue(ComponentLookup.containsText(header, "Combat level: 140"));
		assertTrue(ComponentLookup.containsText(header, "Type: Draconic"));
		assertFalse(ComponentLookup.containsText(header, "lvl 140 dragon"));
		assertFalse(ComponentLookup.containsText(header, PanelCopy.OPEN_WIKI));
		assertFalse(ComponentLookup.containsText(header, PanelCopy.OPEN_DPS));
		assertFalse(ComponentLookup.containsText(header, "Open OSRS Wiki"));
		assertEquals(8, header.getInsets().bottom);
	}

	@Test
	public void titleCasesTheMonsterName()
	{
		SlayerMonster dragons = new MonsterDatabase(new Gson()).findByTaskName("Black dragons");
		MonsterDetailHeader header = new MonsterDetailHeader(
			dragons,
			MonsterImageLoader.none(),
			() ->
			{
			});

		assertTrue(ComponentLookup.containsText(header, "Black Dragons"));
	}
}
