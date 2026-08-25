package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.BisLoadouts;
import javax.swing.JLabel;
import org.junit.Test;

public class EquipmentPanelTest
{
	@Test
	public void placesWeaponAndShieldOnTheBodyRow()
	{
		EquipmentPanel panel = new EquipmentPanel(BisLoadouts.melee(), MonsterImageLoader.none());
		panel.setSize(panel.getPreferredSize());
		panel.doLayout();

		JLabel head = (JLabel) ComponentLookup.named(panel, "item-Slayer helmet (i)");
		JLabel weapon = (JLabel) ComponentLookup.named(panel, "item-Osmumten's fang");
		JLabel body = (JLabel) ComponentLookup.named(panel, "item-Torva platebody");
		JLabel shield = (JLabel) ComponentLookup.named(panel, "item-Avernic defender");
		JLabel legs = (JLabel) ComponentLookup.named(panel, "item-Torva platelegs");

		assertEquals(weapon.getY(), body.getY());
		assertEquals(shield.getY(), body.getY());
		assertEquals(head.getX(), body.getX());
		assertEquals(legs.getX(), body.getX());
		assertTrue(weapon.getX() < body.getX());
		assertTrue(body.getX() < shield.getX());
		assertTrue(head.getY() < body.getY());
		assertTrue(body.getY() < legs.getY());
	}

	@Test
	public void stretchesToFullWidthWithoutSpreadingColumns()
	{
		EquipmentPanel panel = new EquipmentPanel(BisLoadouts.melee(), MonsterImageLoader.none());
		assertEquals(Integer.MAX_VALUE, panel.getMaximumSize().width);
		panel.setSize(220, panel.getPreferredSize().height);
		panel.doLayout();

		JLabel weapon = (JLabel) ComponentLookup.named(panel, "item-Osmumten's fang");
		JLabel body = (JLabel) ComponentLookup.named(panel, "item-Torva platebody");
		JLabel shield = (JLabel) ComponentLookup.named(panel, "item-Avernic defender");
		assertEquals(ItemSlot.SIZE, weapon.getWidth());
		assertEquals(ItemSlot.SIZE, body.getWidth());
		assertEquals(ItemSlot.SIZE, shield.getWidth());
		assertEquals(2, body.getX() - (weapon.getX() + weapon.getWidth()));
		assertEquals(2, shield.getX() - (body.getX() + body.getWidth()));
	}
}
