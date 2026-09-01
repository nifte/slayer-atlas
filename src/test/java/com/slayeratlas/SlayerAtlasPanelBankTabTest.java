package com.slayeratlas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.google.gson.Gson;
import com.slayeratlas.data.CurrentSlayerTask;
import com.slayeratlas.data.FavoriteTasks;
import com.slayeratlas.data.LoadoutSelection;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.SelectedMonster;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.TaskLoadouts;
import com.slayeratlas.ui.MonsterImageLoader;
import com.slayeratlas.ui.WikiInventoryClient;
import com.slayeratlas.ui.WikiLoadoutClient;
import org.junit.Before;
import org.junit.Test;

public class SlayerAtlasPanelBankTabTest
{
	private MonsterDatabase database;
	private SelectedMonster viewed;
	private SlayerAtlasPanel panel;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
		viewed = new SelectedMonster();
		panel = new SlayerAtlasPanel(
			database,
			null,
			new SlayerAtlasConfig()
			{
			},
			MonsterImageLoader.none(),
			null,
			WikiLoadoutClient.none(),
			WikiInventoryClient.none(),
			null,
			null,
			TaskLoadouts.none(),
			FavoriteTasks.none(),
			LoadoutSelection.none(),
			viewed);
	}

	@Test
	public void bankTabOpensTheCurrentTaskWhenNothingIsSelected()
	{
		SlayerMonster dust = database.findByTaskName("Dust devils");
		panel.setCurrentTask(new CurrentSlayerTask("Dust devils", null, 10, 10));
		panel.openFromBankTab();
		assertEquals("Dust devils", panel.getSelected().getName());
		assertSame(dust, viewed.get());
	}

	@Test
	public void bankTabKeepsADifferentSelectedMonster()
	{
		SlayerMonster demons = database.findByTaskName("Abyssal demons");
		panel.setCurrentTask(new CurrentSlayerTask("Dust devils", null, 10, 10));
		panel.selectMonster(demons);
		panel.openFromBankTab();
		assertSame(demons, panel.getSelected());
		assertSame(demons, viewed.get());
	}

	@Test
	public void switchingMonstersUpdatesTheBankTabSelection()
	{
		panel.selectMonster(database.findByTaskName("Dust devils"));
		panel.selectMonster(database.findByTaskName("Abyssal demons"));
		assertEquals("Abyssal demons", viewed.get().getName());
		panel.selectMonster(null);
		assertNull(viewed.get());
	}
}
