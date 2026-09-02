package com.slayeratlas.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LoadoutBankMatcher
{
	private final List<String> names;
	private final Set<String> normalized;

	private LoadoutBankMatcher(List<String> names)
	{
		this.names = names;
		Set<String> keys = new HashSet<>();
		for (String name : names)
		{
			String key = OwnedItemNames.normalize(name);
			if (!key.isEmpty())
			{
				keys.add(key);
			}
		}
		this.normalized = Set.copyOf(keys);
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
		return normalized.contains(OwnedItemNames.normalize(itemName));
	}

	public boolean isEmpty()
	{
		return names.isEmpty();
	}

	public List<String> names()
	{
		return names;
	}

	public boolean sameItems(LoadoutBankMatcher other)
	{
		return other != null && names.equals(other.names);
	}
}
