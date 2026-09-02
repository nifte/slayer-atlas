package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.BisLoadouts;
import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.EquipmentSlot;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.GearRecommendation;
import com.slayeratlas.data.InventoryLoadouts;
import com.slayeratlas.data.OwnedItems;
import java.util.ArrayList;
import java.util.EnumMap;
import java.awt.Component;
import java.util.List;
import java.util.Set;
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
	public void fillsHolesOnAFullWikiInventory()
	{
		List<GearItem> wiki = new java.util.ArrayList<>();
		wiki.add(GearItem.named("Fishing explosive"));
		wiki.add(null);
		wiki.add(GearItem.named("Prayer potion"));
		while (wiki.size() < InventoryLoadouts.SIZE)
		{
			wiki.add(GearItem.named("Cooked sunlight antelope"));
		}
		InventoryPanel panel = new InventoryPanel(
			BisLoadouts.melee().withInventory(wiki),
			MonsterImageLoader.none());
		assertEquals(InventoryLoadouts.SIZE, panel.getComponentCount());
		assertNotNull(ComponentLookup.named(panel, "item-Fishing explosive"));
		assertEquals("Prayer potion(4)", ((JLabel) panel.getComponent(1)).getToolTipText());
		for (Component child : panel.getComponents())
		{
			assertNotNull(((JLabel) child).getToolTipText());
		}
	}

	@Test
	public void keepsEmptySlotsWhenShowingAnExactLoadout()
	{
		List<GearItem> items = new ArrayList<>();
		items.add(GearItem.named("Trout"));
		items.add(null);
		items.add(GearItem.named("Trout"));
		InventoryPanel panel = new InventoryPanel(
			new GearLoadout(CombatStyle.MELEE, true, new EnumMap<>(EquipmentSlot.class), items),
			MonsterImageLoader.none(),
			true);
		assertEquals(InventoryLoadouts.SIZE, panel.getComponentCount());
		assertEquals("Trout", ((JLabel) panel.getComponent(0)).getToolTipText());
		assertNull(((JLabel) panel.getComponent(1)).getToolTipText());
		assertEquals("Trout", ((JLabel) panel.getComponent(2)).getToolTipText());
	}

	@Test
	public void fillsShortInventoriesSoNoSlotIsEmpty()
	{
		InventoryPanel panel = new InventoryPanel(
			BisLoadouts.melee().withInventory(List.of(GearItem.named("Shark"))),
			MonsterImageLoader.none());
		assertEquals(InventoryLoadouts.SIZE, panel.getComponentCount());
		assertNotNull(ComponentLookup.named(panel, "item-Shark"));
		int sharks = 0;
		for (Component child : panel.getComponents())
		{
			JLabel slot = (JLabel) child;
			assertNotNull(slot.getToolTipText());
			if ("Shark".equals(slot.getToolTipText()))
			{
				sharks++;
			}
		}
		assertEquals(InventoryLoadouts.SIZE, sharks);
	}

	@Test
	public void greensEveryCopyWhenThePlayerHasAtLeastOne()
	{
		InventoryPanel panel = new InventoryPanel(
			BisLoadouts.melee().withInventory(List.of(GearItem.named("Shark"))),
			MonsterImageLoader.none(),
			false,
			OwnedItems.withoutBank(Set.of("Shark")));
		for (Component child : panel.getComponents())
		{
			assertEquals(ItemSlot.HELD_BACKGROUND, child.getBackground());
		}
	}

	@Test
	public void greensExactSlotsThePlayerIsCarrying()
	{
		List<GearItem> items = new ArrayList<>();
		items.add(GearItem.named("Trout"));
		items.add(null);
		items.add(GearItem.named("Prayer potion(4)"));
		InventoryPanel panel = new InventoryPanel(
			new GearLoadout(CombatStyle.MELEE, true, new EnumMap<>(EquipmentSlot.class), items),
			MonsterImageLoader.none(),
			true,
			OwnedItems.withoutBank(Set.of("Trout", "Prayer potion(1)")));
		assertEquals(ItemSlot.HELD_BACKGROUND, panel.getComponent(0).getBackground());
		assertEquals(ItemSlot.EMPTY_BACKGROUND, panel.getComponent(1).getBackground());
		assertEquals(ItemSlot.HELD_BACKGROUND, panel.getComponent(2).getBackground());
	}

	@Test
	public void redsEveryCopyWhenThePlayerOwnsNone()
	{
		InventoryPanel panel = new InventoryPanel(
			BisLoadouts.melee().withInventory(List.of(GearItem.named("Shark"))),
			MonsterImageLoader.none(),
			false,
			ItemSlotOwnership.of(
				OwnedItems.none(),
				GearRecommendation.of(false, OwnedItems.withBank(Set.of("Trout")))));
		for (Component child : panel.getComponents())
		{
			assertEquals(ItemSlot.MISSING_BACKGROUND, child.getBackground());
		}
	}
}
