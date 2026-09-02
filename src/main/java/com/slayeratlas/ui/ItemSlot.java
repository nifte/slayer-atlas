package com.slayeratlas.ui;

import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.OwnedItems;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ItemSlot extends JLabel
{
	public static final int SIZE = 40;
	public static final int GEAR_ICON_SIZE = 32;
	public static final int INVENTORY_ICON_SIZE = 28;
	public static final Color EMPTY_BACKGROUND = ItemSlotBackground.EMPTY;
	public static final Color HELD_BACKGROUND = ItemSlotBackground.HELD;
	public static final Color MISSING_BACKGROUND = ItemSlotBackground.MISSING;

	public ItemSlot(GearItem item, MonsterImageLoader images)
	{
		this(item, images, GEAR_ICON_SIZE);
	}

	public ItemSlot(GearItem item, MonsterImageLoader images, int iconSize)
	{
		this(item, images, iconSize, ItemSlotOwnership.none());
	}

	public ItemSlot(GearItem item, MonsterImageLoader images, OwnedItems carried)
	{
		this(item, images, GEAR_ICON_SIZE, ItemSlotOwnership.carried(carried));
	}

	public ItemSlot(GearItem item, MonsterImageLoader images, ItemSlotOwnership ownership)
	{
		this(item, images, GEAR_ICON_SIZE, ownership);
	}

	public ItemSlot(GearItem item, MonsterImageLoader images, int iconSize, OwnedItems carried)
	{
		this(item, images, iconSize, ItemSlotOwnership.carried(carried));
	}

	public ItemSlot(GearItem item, MonsterImageLoader images, int iconSize, ItemSlotOwnership ownership)
	{
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
		setOpaque(true);
		setBackground((ownership == null ? ItemSlotOwnership.none() : ownership).background(item));
		setBorder(null);
		setPreferredSize(new Dimension(SIZE, SIZE));
		setMinimumSize(new Dimension(SIZE, SIZE));
		setMaximumSize(new Dimension(SIZE, SIZE));
		if (item == null)
		{
			return;
		}
		setToolTipText(item.getName());
		setName("item-" + item.getName());
		setComponentPopupMenu(ItemWikiMenu.forItem(item));
		if (images != null)
		{
			images.loadFile(item.getImageFile(), iconSize, image ->
			{
				if (image != null)
				{
					setIcon(new ImageIcon(image));
				}
			});
		}
	}
}
