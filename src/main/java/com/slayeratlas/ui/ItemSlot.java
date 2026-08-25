package com.slayeratlas.ui;

import com.slayeratlas.data.GearItem;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import net.runelite.client.ui.ColorScheme;

public class ItemSlot extends JLabel
{
	public static final int SIZE = 40;
	public static final int GEAR_ICON_SIZE = 32;
	public static final int INVENTORY_ICON_SIZE = 28;

	public ItemSlot(GearItem item, MonsterImageLoader images)
	{
		this(item, images, GEAR_ICON_SIZE);
	}

	public ItemSlot(GearItem item, MonsterImageLoader images, int iconSize)
	{
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
		setOpaque(true);
		setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
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
