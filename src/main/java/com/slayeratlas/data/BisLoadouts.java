package com.slayeratlas.data;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class BisLoadouts
{
	private BisLoadouts()
	{
	}

	public static GearLoadout forStyle(CombatStyle style)
	{
		if (style == CombatStyle.RANGED)
		{
			return ranged();
		}
		if (style == CombatStyle.MAGIC)
		{
			return magic();
		}
		return melee();
	}

	public static GearLoadout melee()
	{
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.HEAD, SlayerHelmet.IMBUED);
		worn.put(EquipmentSlot.CAPE, GearItem.named("Infernal cape"));
		worn.put(EquipmentSlot.NECK, GearItem.named("Amulet of rancour"));
		worn.put(EquipmentSlot.AMMO, GearItem.named("Rada's blessing 4"));
		worn.put(EquipmentSlot.WEAPON, GearItem.named("Ghrazi rapier"));
		worn.put(EquipmentSlot.BODY, GearItem.named("Torva platebody"));
		worn.put(EquipmentSlot.SHIELD, OffhandGear.MELEE);
		worn.put(EquipmentSlot.LEGS, GearItem.named("Torva platelegs"));
		worn.put(EquipmentSlot.HANDS, GearItem.named("Ferocious gloves"));
		worn.put(EquipmentSlot.FEET, GearItem.named("Avernic treads (max)"));
		worn.put(EquipmentSlot.RING, GearItem.named("Ultor ring"));
		return new GearLoadout(CombatStyle.MELEE, true, worn, List.of());
	}

	public static GearLoadout ranged()
	{
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.HEAD, SlayerHelmet.IMBUED);
		worn.put(EquipmentSlot.CAPE, GearItem.named("Blessed dizana's quiver"));
		worn.put(EquipmentSlot.NECK, GearItem.named("Necklace of rupture"));
		worn.put(EquipmentSlot.AMMO, GearItem.named("Ruby dragon bolts (e)"));
		worn.put(EquipmentSlot.WEAPON, GearItem.named("Zaryte crossbow"));
		worn.put(EquipmentSlot.BODY, GearItem.named("Masori body (f)"));
		worn.put(EquipmentSlot.SHIELD, OffhandGear.RANGED);
		worn.put(EquipmentSlot.LEGS, GearItem.named("Masori chaps (f)"));
		worn.put(EquipmentSlot.HANDS, GearItem.named("Zaryte vambraces"));
		worn.put(EquipmentSlot.FEET, GearItem.named("Avernic treads (max)"));
		worn.put(EquipmentSlot.RING, GearItem.named("Venator ring"));
		return new GearLoadout(CombatStyle.RANGED, true, worn, List.of());
	}

	public static GearLoadout magic()
	{
		Map<EquipmentSlot, GearItem> worn = new EnumMap<>(EquipmentSlot.class);
		worn.put(EquipmentSlot.HEAD, SlayerHelmet.IMBUED);
		worn.put(EquipmentSlot.CAPE, GearItem.named("Imbued Saradomin cape"));
		worn.put(EquipmentSlot.NECK, GearItem.named("Occult necklace"));
		worn.put(EquipmentSlot.WEAPON, GearItem.named("Tumeken's shadow"));
		worn.put(EquipmentSlot.BODY, GearItem.named("Ancestral robe top"));
		worn.put(EquipmentSlot.SHIELD, OffhandGear.MAGIC);
		worn.put(EquipmentSlot.LEGS, GearItem.named("Ancestral robe bottom"));
		worn.put(EquipmentSlot.HANDS, GearItem.named("Confliction gauntlets"));
		worn.put(EquipmentSlot.AMMO, GearItem.named("Rada's blessing 4"));
		worn.put(EquipmentSlot.FEET, GearItem.named("Avernic treads (max)"));
		worn.put(EquipmentSlot.RING, GearItem.named("Magus ring"));
		return new GearLoadout(CombatStyle.MAGIC, true, worn, List.of());
	}
}
