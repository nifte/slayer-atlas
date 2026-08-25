package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.BisLoadouts;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.InventoryLoadouts;
import java.awt.Component;
import java.util.List;
import javax.swing.JLabel;
import org.junit.Test;

public class InventoryPanelTest
{
	@Test
	public void stretchesToFullWidthWithoutSpreadingColumns()
	{
		InventoryPanel panel = new InventoryPanel(null, MonsterImageLoader.none());
		assertEquals(28, panel.getComponentCount());
		assertEquals(Integer.MAX_VALUE, panel.getMaximumSize().width);
		panel.setSize(220, panel.getPreferredSize().height);
		panel.doLayout();
		for (int column = 0; column < 3; column++)
		{
			Component left = panel.getComponent(column);
			Component right = panel.getComponent(column + 1);
			assertEquals(ItemSlot.SIZE, left.getWidth());
			assertEquals(ItemSlot.SIZE, left.getHeight());
			assertEquals(2, right.getX() - (left.getX() + left.getWidth()));
		}
	}

	@Test
	public void fillsShortInventoriesSoNoSlotIsEmpty()
	{
		InventoryPanel panel = new InventoryPanel(
			BisLoadouts.melee().withInventory(List.of(GearItem.named("Shark"))),
			MonsterImageLoader.none());
		assertEquals(InventoryLoadouts.SIZE, panel.getComponentCount());
		assertNotNull(ComponentLookup.named(panel, "item-Shark"));
		int food = 0;
		for (Component child : panel.getComponents())
		{
			JLabel slot = (JLabel) child;
			assertNotNull(slot.getToolTipText());
			if (InventoryLoadouts.FOOD.equals(slot.getToolTipText()))
			{
				food++;
			}
		}
		assertEquals(InventoryLoadouts.SIZE - 1, food);
	}
}
