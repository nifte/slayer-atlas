package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OwnedItems
{
	private final List<String> originals;
	private final Set<String> names;
	private final boolean hasBankSnapshot;

	private OwnedItems(List<String> originals, Set<String> names, boolean hasBankSnapshot)
	{
		this.originals = Collections.unmodifiableList(originals);
		this.names = Collections.unmodifiableSet(names);
		this.hasBankSnapshot = hasBankSnapshot;
	}

	public static OwnedItems none()
	{
		return new OwnedItems(List.of(), Set.of(), false);
	}

	public static OwnedItems withoutBank(Set<String> names)
	{
		return of(names, false);
	}

	public static OwnedItems withBank(Set<String> names)
	{
		return of(names, true);
	}

	public boolean hasBankSnapshot()
	{
		return hasBankSnapshot;
	}

	public boolean contains(GearItem item)
	{
		if (item == null || item.getName() == null)
		{
			return false;
		}
		for (String key : OwnedItemNames.keys(item.getName()))
		{
			if (names.contains(key))
			{
				return true;
			}
		}
		return false;
	}

	public GearItem shownAs(GearItem wikiItem)
	{
		if (wikiItem == null)
		{
			return null;
		}
		String owned = OwnedItemNames.preferredOwnedName(wikiItem.getName(), originals);
		return owned == null ? wikiItem : GearItem.named(owned);
	}

	public Set<String> names()
	{
		return names;
	}

	private static OwnedItems of(Set<String> names, boolean hasBankSnapshot)
	{
		List<String> originals = new ArrayList<>();
		Set<String> keys = new HashSet<>();
		if (names == null)
		{
			return new OwnedItems(originals, keys, hasBankSnapshot);
		}
		for (String name : names)
		{
			if (name == null || name.isEmpty())
			{
				continue;
			}
			originals.add(name);
			keys.addAll(OwnedItemNames.keys(name));
		}
		return new OwnedItems(originals, keys, hasBankSnapshot);
	}
}
