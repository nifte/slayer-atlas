package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class GearLoadout
{
	private final CombatStyle style;
	private final boolean primary;
	private final Map<EquipmentSlot, GearItem> worn;
	private final List<GearItem> inventory;
	private final List<String> prayers;

	public GearLoadout(
		CombatStyle style,
		boolean primary,
		Map<EquipmentSlot, GearItem> worn,
		List<GearItem> inventory)
	{
		this(style, primary, worn, inventory, List.of());
	}

	public GearLoadout(
		CombatStyle style,
		boolean primary,
		Map<EquipmentSlot, GearItem> worn,
		List<GearItem> inventory,
		List<String> prayers)
	{
		this.style = style;
		this.primary = primary;
		Map<EquipmentSlot, GearItem> copy = new EnumMap<>(EquipmentSlot.class);
		copy.putAll(worn);
		this.worn = Collections.unmodifiableMap(copy);
		this.inventory = Collections.unmodifiableList(new ArrayList<>(inventory));
		this.prayers = Collections.unmodifiableList(new ArrayList<>(prayers == null ? List.of() : prayers));
	}

	public CombatStyle getStyle()
	{
		return style;
	}

	public boolean isPrimary()
	{
		return primary;
	}

	public GearItem worn(EquipmentSlot slot)
	{
		return worn.get(slot);
	}

	public List<GearItem> getInventory()
	{
		return inventory;
	}

	public List<String> getPrayers()
	{
		return prayers;
	}

	public GearLoadout withInventory(List<GearItem> items)
	{
		return new GearLoadout(style, primary, worn, items, prayers);
	}

	public GearLoadout withWorn(EquipmentSlot slot, GearItem item)
	{
		Map<EquipmentSlot, GearItem> copy = new EnumMap<>(worn);
		if (item == null)
		{
			copy.remove(slot);
		}
		else
		{
			copy.put(slot, item);
		}
		return new GearLoadout(style, primary, copy, inventory, prayers);
	}

	public GearLoadout withPrayers(List<String> prayers)
	{
		return new GearLoadout(style, primary, worn, inventory, prayers);
	}
}
