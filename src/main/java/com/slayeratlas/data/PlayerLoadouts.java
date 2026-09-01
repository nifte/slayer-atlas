package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.game.ItemManager;

public final class PlayerLoadouts
{
	private PlayerLoadouts()
	{
	}

	public static GearLoadout named(
		CombatStyle style,
		Map<EquipmentSlot, String> wornNames,
		List<String> inventoryNames)
	{
		return named(style, wornNames, inventoryNames, List.of());
	}

	public static GearLoadout named(
		CombatStyle style,
		Map<EquipmentSlot, String> wornNames,
		List<String> inventoryNames,
		List<String> prayers)
	{
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		if (wornNames != null)
		{
			for (Map.Entry<EquipmentSlot, String> entry : wornNames.entrySet())
			{
				if (entry.getKey() == null || !entry.getKey().onWornGrid())
				{
					continue;
				}
				GearItem item = GearItem.named(entry.getValue());
				if (item != null)
				{
					worn.put(entry.getKey(), item);
				}
			}
		}
		List<GearItem> inventory = new ArrayList<>();
		if (inventoryNames != null)
		{
			for (String name : inventoryNames)
			{
				inventory.add(GearItem.named(name));
			}
		}
		return new GearLoadout(style == null ? CombatStyle.MELEE : style, true, worn, inventory, prayers);
	}

	public static GearLoadout fromClient(Client client, ItemManager items, CombatStyle style)
	{
		if (client == null || items == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}
		Map<EquipmentSlot, String> worn = new EnumMap<>(EquipmentSlot.class);
		ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
		for (EquipmentInventorySlot gameSlot : EquipmentInventorySlot.values())
		{
			EquipmentSlot slot = fromGame(gameSlot);
			if (slot == null)
			{
				continue;
			}
			String name = itemName(items, itemAt(equipment, gameSlot.getSlotIdx()));
			if (name != null)
			{
				worn.put(slot, name);
			}
		}
		List<String> inventory = new ArrayList<>(InventoryLoadouts.SIZE);
		ItemContainer bag = client.getItemContainer(InventoryID.INV);
		for (int index = 0; index < InventoryLoadouts.SIZE; index++)
		{
			inventory.add(itemName(items, itemAt(bag, index)));
		}
		Item weapon = itemAt(equipment, EquipmentInventorySlot.WEAPON.getSlotIdx());
		CombatStyle weaponStyle = WeaponStyles.of(items, weapon);
		return named(weaponStyle == null ? style : weaponStyle, worn, inventory);
	}

	static EquipmentSlot fromGame(EquipmentInventorySlot slot)
	{
		if (slot == null)
		{
			return null;
		}
		switch (slot)
		{
			case HEAD:
				return EquipmentSlot.HEAD;
			case CAPE:
				return EquipmentSlot.CAPE;
			case AMULET:
				return EquipmentSlot.NECK;
			case AMMO:
				return EquipmentSlot.AMMO;
			case WEAPON:
				return EquipmentSlot.WEAPON;
			case BODY:
				return EquipmentSlot.BODY;
			case SHIELD:
				return EquipmentSlot.SHIELD;
			case LEGS:
				return EquipmentSlot.LEGS;
			case GLOVES:
				return EquipmentSlot.HANDS;
			case BOOTS:
				return EquipmentSlot.FEET;
			case RING:
				return EquipmentSlot.RING;
			default:
				return null;
		}
	}

	private static Item itemAt(ItemContainer container, int index)
	{
		return container == null ? null : container.getItem(index);
	}

	private static String itemName(ItemManager items, Item item)
	{
		if (items == null || item == null || item.getId() <= 0)
		{
			return null;
		}
		int canonical = items.canonicalize(item.getId());
		ItemComposition composition = items.getItemComposition(canonical);
		if (composition == null || composition.getName() == null || composition.getName().equals("null"))
		{
			return null;
		}
		return composition.getName();
	}
}
