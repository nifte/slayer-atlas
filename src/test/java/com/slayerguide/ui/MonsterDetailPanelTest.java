package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.slayerguide.ComponentLookup;
import com.slayerguide.data.CurrentSlayerTask;
import com.slayerguide.data.MonsterDatabase;
import com.slayerguide.data.MonsterLocation;
import com.slayerguide.data.SlayerMonster;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;
import org.junit.Test;

public class MonsterDetailPanelTest
{
	@Test
	public void omitsRemainingCountForTheCurrentTask()
	{
		MonsterDatabase database = new MonsterDatabase(new Gson());
		SlayerMonster wyverns = database.findByTaskName("Skeletal Wyverns");
		CurrentSlayerTask task = new CurrentSlayerTask("Skeletal Wyverns", "Asgarnia", 31, 40);
		MonsterDetailPanel panel = new MonsterDetailPanel(
			wyverns,
			database.locationsFor(wyverns),
			task,
			new NoPathActions());

		assertFalse(ComponentLookup.containsText(panel, "remaining"));
	}

	@Test
	public void sectionHeadingsAreTitleCaseAndBold()
	{
		JPanel section = PanelWidgets.section("Attack style");
		JLabel heading = (JLabel) section.getComponent(0);
		assertEquals("Attack Style", heading.getText());
		assertEquals(PanelFonts.bodyBold(), heading.getFont());
		assertFalse("ATTACK STYLE".equals(heading.getText()));
		assertTrue(heading.getForeground().equals(ColorScheme.BRAND_ORANGE));
	}

	private static final class NoPathActions implements MonsterDetailPanel.Actions
	{
		@Override
		public void pathTo(MonsterLocation location)
		{
		}

		@Override
		public void pathToNearest(SlayerMonster monster)
		{
		}

		@Override
		public void openWiki(SlayerMonster monster)
		{
		}

		@Override
		public boolean canPath()
		{
			return false;
		}

		@Override
		public String pathUnavailableReason()
		{
			return "";
		}
	}
}
