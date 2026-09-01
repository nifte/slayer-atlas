package com.slayeratlas.ui;

import com.slayeratlas.data.EquipmentSlot;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.OwnedItems;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class EquipmentPanel extends JPanel
{
	private static final int COLUMNS = 3;
	private static final int ROWS = 5;

	public EquipmentPanel(GearLoadout loadout, MonsterImageLoader images)
	{
		this(loadout, images, OwnedItems.none());
	}

	public EquipmentPanel(GearLoadout loadout, MonsterImageLoader images, OwnedItems carried)
	{
		setLayout(new GridBagLayout());
		setOpaque(true);
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setName("equipment-panel");
		setAlignmentX(Component.LEFT_ALIGNMENT);
		OwnedItems held = carried == null ? OwnedItems.none() : carried;
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		for (int row = 0; row < ROWS; row++)
		{
			for (int column = 0; column < COLUMNS; column++)
			{
				constraints.gridx = column;
				constraints.gridy = row;
				EquipmentSlot slot = EquipmentSlot.at(column, row);
				if (slot == null)
				{
					add(Box.createRigidArea(new Dimension(ItemSlot.SIZE, ItemSlot.SIZE)), constraints);
					continue;
				}
				add(new ItemSlot(loadout == null ? null : loadout.worn(slot), images, held), constraints);
			}
		}
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
	}
}
