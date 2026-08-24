package com.slayerguide.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayerguide.ComponentLookup;
import com.slayerguide.data.MonsterDatabase;
import com.slayerguide.data.SlayerMonster;
import javax.swing.JButton;
import org.junit.Test;

public class MonsterDetailHeaderTest
{
	@Test
	public void putsBackWikiAndDpsAroundThePortrait()
	{
		SlayerMonster wyverns = new MonsterDatabase(new Gson()).findByTaskName("Skeletal Wyverns");
		MonsterDetailHeader header = new MonsterDetailHeader(
			wyverns,
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

		assertTrue(ComponentLookup.containsText(header, PanelCopy.BACK_TO_LIST));
		assertTrue(ComponentLookup.containsText(header, PanelCopy.OPEN_WIKI));
		assertTrue(ComponentLookup.containsText(header, PanelCopy.OPEN_DPS));
		assertTrue(ComponentLookup.containsText(header, "Skeletal Wyverns"));
		assertFalse(ComponentLookup.containsText(header, "Open OSRS Wiki"));
		assertTrue(((JButton) ComponentLookup.named(header, "open-dps")).isEnabled());
	}
}
