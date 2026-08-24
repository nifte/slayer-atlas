package com.slayerguide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.MonsterDatabase;
import java.awt.Component;
import javax.swing.JScrollPane;
import org.junit.Before;
import org.junit.Test;

public class SlayerGuidePanelChromeTest
{
	private MonsterDatabase database;
	private SlayerGuidePanel panel;

	@Before
	public void setUp()
	{
		database = new MonsterDatabase(new Gson());
		panel = new SlayerGuidePanel(database, null, new SlayerGuideConfig()
		{
		});
	}

	@Test
	public void hidesSearchOnMonsterDetail()
	{
		Component searchSlot = ComponentLookup.named(panel, "search-slot");
		assertNotNull(searchSlot);
		assertTrue(searchSlot.isVisible());

		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));

		assertFalse(searchSlot.isVisible());
	}

	@Test
	public void showsSearchAgainAfterLeavingDetail()
	{
		Component searchSlot = ComponentLookup.named(panel, "search-slot");
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		assertFalse(searchSlot.isVisible());

		panel.selectMonster(null);

		assertTrue(searchSlot.isVisible());
	}

	@Test
	public void hidesCurrentTaskUntilAnAssignmentExists()
	{
		Component taskSlot = ComponentLookup.named(panel, "task-slot");
		assertNotNull(taskSlot);
		assertFalse(taskSlot.isVisible());

		panel.setCurrentTask(new CurrentSlayerTask("Skeletal Wyverns", null, 31, 31));
		assertTrue(taskSlot.isVisible());

		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		assertFalse(taskSlot.isVisible());
	}

	@Test
	public void monsterDetailOpensScrolledToTheTop()
	{
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		JScrollPane detailScroll = (JScrollPane) ComponentLookup.named(panel, "detail-scroll");
		assertNotNull(detailScroll);
		assertEquals(0, detailScroll.getVerticalScrollBar().getValue());
		assertEquals(0, detailScroll.getViewport().getViewPosition().y);
	}

	@Test
	public void listTitleIsSlayerAtlas()
	{
		Component title = ComponentLookup.named(panel, "panel-title");
		assertNotNull(title);
		assertTrue(ComponentLookup.containsText(title, "Slayer Atlas"));
		assertFalse(ComponentLookup.containsText(title, "Slayer Guide"));
	}

	@Test
	public void detailHeaderHasBackWikiAndName()
	{
		panel.selectMonster(database.findByTaskName("Skeletal Wyverns"));
		assertTrue(ComponentLookup.containsText(panel, "Back to list"));
		assertTrue(ComponentLookup.containsText(panel, "Open wiki"));
		assertTrue(ComponentLookup.containsText(panel, "Skeletal Wyverns"));
	}
}
