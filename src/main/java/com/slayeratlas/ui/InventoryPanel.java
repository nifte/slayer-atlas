package com.slayeratlas.ui;

import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.InventoryLoadouts;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class InventoryPanel extends JPanel
{
	private static final int COLUMNS = 4;
	private static final int ROWS = 7;

	public InventoryPanel(GearLoadout loadout, MonsterImageLoader images)
	{
		this(loadout, images, false);
	}

	public InventoryPanel(GearLoadout loadout, MonsterImageLoader images, boolean exact)
	{
		setLayout(new GridBagLayout());
		setOpaque(true);
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setName("inventory-panel");
		setAlignmentX(Component.LEFT_ALIGNMENT);
		List<GearItem> items = items(loadout, exact);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		int slots = COLUMNS * ROWS;
		for (int index = 0; index < slots; index++)
		{
			constraints.gridx = index % COLUMNS;
			constraints.gridy = index / COLUMNS;
			GearItem item = index < items.size() ? items.get(index) : null;
			add(new ItemSlot(item, images, ItemSlot.INVENTORY_ICON_SIZE), constraints);
		}
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
	}

	private static List<GearItem> items(GearLoadout loadout, boolean exact)
	{
		if (loadout == null)
		{
			return List.of();
		}
		return exact
			? InventoryLoadouts.slots(loadout.getInventory())
			: InventoryLoadouts.filled(loadout.getInventory());
	}
}
