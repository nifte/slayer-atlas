package com.slayeratlas.data;

import java.util.List;

public final class LoadoutBankMatcher
{
	private final List<String> names;

	private LoadoutBankMatcher(List<String> names)
	{
		this.names = names;
	}

	public static LoadoutBankMatcher of(GearLoadout loadout)
	{
		return new LoadoutBankMatcher(LoadoutItemNames.of(loadout));
	}

	public static LoadoutBankMatcher of(List<String> names)
	{
		return new LoadoutBankMatcher(names == null ? List.of() : List.copyOf(names));
	}

	public boolean matches(String itemName)
	{
		if (itemName == null || itemName.isEmpty() || itemName.equals("null"))
		{
			return false;
		}
		for (String name : names)
		{
			if (OwnedItemNames.sameItem(name, itemName))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty()
	{
		return names.isEmpty();
	}

	public List<String> names()
	{
		return names;
	}
}
