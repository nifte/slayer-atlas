package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearRecommendation;
import com.slayeratlas.data.OwnedItems;
import com.slayeratlas.data.SlayerMonster;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.junit.Test;

public class ItemSlotTest
{
	@Test
	public void emptySlotsArePlainSquaresWithoutAnOutline()
	{
		InventoryPanel panel = new InventoryPanel(null, MonsterImageLoader.none());
		assertEquals(28, panel.getComponentCount());
		for (Component child : panel.getComponents())
		{
			ItemSlot slot = (ItemSlot) child;
			assertNull(slot.getIcon());
			assertNull(slot.getBorder());
			assertNull(slot.getComponentPopupMenu());
			assertEquals(ItemSlot.SIZE, slot.getPreferredSize().width);
			assertEquals(ItemSlot.SIZE, slot.getPreferredSize().height);
		}
	}

	@Test
	public void filledSlotsShowAWikiButtonOnRightClick()
	{
		ItemSlot slot = new ItemSlot(GearItem.named("Shark"), MonsterImageLoader.none());
		JPopupMenu menu = slot.getComponentPopupMenu();
		assertNotNull(menu);
		JMenuItem wiki = (JMenuItem) ComponentLookup.named(menu, "item-wiki");
		assertNotNull(wiki);
		assertEquals(PanelCopy.OPEN_WIKI, wiki.getText());
	}

	@Test
	public void loadsGearIconsSmallerThanTheSlot()
	{
		AtomicInteger requested = new AtomicInteger();
		ItemSlot slot = new ItemSlot(GearItem.named("Shark"), recordingLoader(requested));
		assertEquals(ItemSlot.SIZE, slot.getPreferredSize().width);
		assertEquals(ItemSlot.GEAR_ICON_SIZE, requested.get());
		assertTrue(ItemSlot.GEAR_ICON_SIZE < ItemSlot.SIZE);
	}

	@Test
	public void loadsInventoryIconsSmallerThanGearIcons()
	{
		AtomicInteger requested = new AtomicInteger();
		ItemSlot slot = new ItemSlot(
			GearItem.named("Shark"),
			recordingLoader(requested),
			ItemSlot.INVENTORY_ICON_SIZE);
		assertEquals(ItemSlot.SIZE, slot.getPreferredSize().width);
		assertEquals(ItemSlot.INVENTORY_ICON_SIZE, requested.get());
		assertTrue(ItemSlot.INVENTORY_ICON_SIZE < ItemSlot.GEAR_ICON_SIZE);
	}

	@Test
	public void emptySlotsUseTheDefaultBackground()
	{
		ItemSlot slot = new ItemSlot(null, MonsterImageLoader.none());
		assertEquals(ItemSlot.EMPTY_BACKGROUND, slot.getBackground());
	}

	@Test
	public void greensSlotsThePlayerIsCarrying()
	{
		OwnedItems carried = OwnedItems.withoutBank(Set.of("Shark"));
		ItemSlot held = new ItemSlot(GearItem.named("Shark"), MonsterImageLoader.none(), carried);
		ItemSlot missing = new ItemSlot(GearItem.named("Trout"), MonsterImageLoader.none(), carried);
		assertEquals(ItemSlot.HELD_BACKGROUND, held.getBackground());
		assertEquals(ItemSlot.EMPTY_BACKGROUND, missing.getBackground());
	}

	@Test
	public void greensChargedItemsThatMatchTheRecommendation()
	{
		ItemSlot slot = new ItemSlot(
			GearItem.named("Amulet of glory"),
			MonsterImageLoader.none(),
			OwnedItems.withoutBank(Set.of("Amulet of glory (6)")));
		assertEquals(ItemSlot.HELD_BACKGROUND, slot.getBackground());
	}

	@Test
	public void redsUnownedItemsWhenMissingSlotsAreMarked()
	{
		ItemSlotOwnership ownership = ItemSlotOwnership.of(
			OwnedItems.none(),
			GearRecommendation.of(false, OwnedItems.withBank(Set.of("Shark"))));
		ItemSlot missing = new ItemSlot(GearItem.named("Trout"), MonsterImageLoader.none(), ownership);
		ItemSlot owned = new ItemSlot(GearItem.named("Shark"), MonsterImageLoader.none(), ownership);
		assertEquals(ItemSlot.MISSING_BACKGROUND, missing.getBackground());
		assertEquals(ItemSlot.EMPTY_BACKGROUND, owned.getBackground());
	}

	private static MonsterImageLoader recordingLoader(AtomicInteger requested)
	{
		return new MonsterImageLoader()
		{
			@Override
			public void load(SlayerMonster monster, int size, Consumer<BufferedImage> onLoaded)
			{
			}

			@Override
			public void loadFile(String fileName, int size, Consumer<BufferedImage> onLoaded)
			{
				requested.set(size);
			}
		};
	}
}
