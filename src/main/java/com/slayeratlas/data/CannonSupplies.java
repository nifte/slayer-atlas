package com.slayeratlas.data;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public final class CannonSupplies
{
	public static final String CANNON_BASE = "Cannon base";
	public static final String CANNON_STAND = "Cannon stand";
	public static final String CANNON_BARRELS = "Cannon barrels";
	public static final String CANNON_FURNACE = "Cannon furnace";
	public static final String CANNONBALL = "Cannonball";

	private static final List<String> ITEM_NAMES = List.of(
		CANNON_BASE,
		CANNON_STAND,
		CANNON_BARRELS,
		CANNON_FURNACE,
		CANNONBALL);

	private CannonSupplies()
	{
	}

	public static boolean needsCannon(SlayerMonster monster)
	{
		if (monster == null || monster.getLocationIds() == null || monster.getLocationIds().isEmpty())
		{
			return false;
		}
		MonsterDatabase database = catalog();
		List<SlayerMonster> alternatives = alternatives(monster, database);
		int checked = 0;
		for (String locationId : monster.getLocationIds())
		{
			if (!TaskLocations.include(monster, locationId, alternatives))
			{
				continue;
			}
			if (!CannonableLocations.isCannonable(locationId, database.getLocation(locationId)))
			{
				return false;
			}
			checked++;
		}
		return checked > 0;
	}

	public static List<GearItem> items()
	{
		List<GearItem> items = new ArrayList<>();
		for (String name : ITEM_NAMES)
		{
			items.add(GearItem.named(name));
		}
		return items;
	}

	public static boolean isCannonItem(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		for (String item : ITEM_NAMES)
		{
			if (OwnedItemNames.matches(name, item))
			{
				return true;
			}
		}
		return false;
	}

	private static List<SlayerMonster> alternatives(SlayerMonster monster, MonsterDatabase database)
	{
		List<SlayerMonster> found = new ArrayList<>();
		if (monster.getAlternatives() == null)
		{
			return found;
		}
		for (String label : monster.getAlternatives())
		{
			SlayerMonster alternative = AlternativeMonsters.find(database, label, monster);
			if (alternative != null)
			{
				found.add(alternative);
			}
		}
		return found;
	}

	private static MonsterDatabase catalog()
	{
		return Catalog.INSTANCE;
	}

	private static final class Catalog
	{
		private static final MonsterDatabase INSTANCE = new MonsterDatabase(new Gson());
	}
}
