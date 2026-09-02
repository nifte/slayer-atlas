package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;

public final class CannonSupplies
{
	public static final String CANNON_BASE = "Cannon base";
	public static final String CANNON_STAND = "Cannon stand";
	public static final String CANNON_BARRELS = "Cannon barrels";
	public static final String CANNON_FURNACE = "Cannon furnace";
	public static final String GRANITE_CANNONBALL = "Granite cannonball";
	public static final String CANNONBALL = "Steel cannonball";

	public static final List<String> PIECES = List.of(
		CANNON_BASE,
		CANNON_STAND,
		CANNON_BARRELS,
		CANNON_FURNACE);

	public static final List<GearItem> CANNONBALLS = List.of(
		GearItem.named(GRANITE_CANNONBALL),
		GearItem.named(CANNONBALL));

	private static MonsterDatabase database;

	private CannonSupplies()
	{
	}

	public static void useDatabase(MonsterDatabase database)
	{
		CannonSupplies.database = database;
	}

	public static boolean needsCannon(SlayerMonster monster)
	{
		return needsCannon(monster, database);
	}

	public static boolean needsCannon(SlayerMonster monster, MonsterDatabase database)
	{
		if (monster == null || monster.getLocationIds() == null || monster.getLocationIds().isEmpty())
		{
			return false;
		}
		if (database == null)
		{
			return false;
		}
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

	public static boolean include(SlayerMonster monster, List<GearItem> items)
	{
		return include(monster, items, database);
	}

	public static boolean include(SlayerMonster monster, List<GearItem> items, MonsterDatabase database)
	{
		return needsCannon(monster, database) || hasCannonPiece(items);
	}

	public static List<GearItem> items()
	{
		List<GearItem> items = new ArrayList<>();
		for (String name : PIECES)
		{
			items.add(GearItem.named(name));
		}
		items.add(GearItem.named(GRANITE_CANNONBALL));
		return items;
	}

	public static GearItem pickCannonballs(GearRecommendation recommendation)
	{
		if (recommendation != null && recommendation.filterToOwned())
		{
			GearItem picked = OwnedSupplies.pick(CANNONBALLS, recommendation);
			return picked == null ? GearItem.named(CANNONBALL) : picked;
		}
		return GearItem.named(GRANITE_CANNONBALL);
	}

	public static boolean isCannonItem(String name)
	{
		return isCannonPiece(name) || isCannonball(name);
	}

	public static boolean isCannonPiece(String name)
	{
		if (name == null || name.isEmpty() || isCannonball(name))
		{
			return false;
		}
		for (String piece : PIECES)
		{
			if (OwnedItemNames.matches(name, piece))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isCannonball(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		for (GearItem item : CANNONBALLS)
		{
			if (item != null && OwnedItemNames.matches(name, item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean hasCannonPiece(List<GearItem> items)
	{
		if (items == null)
		{
			return false;
		}
		for (GearItem item : items)
		{
			if (item != null && isCannonPiece(item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean hasCannonballs(List<GearItem> items)
	{
		if (items == null)
		{
			return false;
		}
		for (GearItem item : items)
		{
			if (item != null && isCannonball(item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean hasCompleteCannon(List<GearItem> items)
	{
		if (!hasCannonballs(items))
		{
			return false;
		}
		for (String piece : PIECES)
		{
			if (!hasPiece(items, piece))
			{
				return false;
			}
		}
		return true;
	}

	public static boolean hasPiece(List<GearItem> items, String piece)
	{
		if (items == null || piece == null)
		{
			return false;
		}
		for (GearItem item : items)
		{
			if (item != null && item.getName() != null && OwnedItemNames.matches(item.getName(), piece))
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
}
