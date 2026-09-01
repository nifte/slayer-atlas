package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class OwnedItems
{
	private final List<String> originals;
	private final Set<String> names;
	private final Map<String, String> lastEquipped;
	private final boolean hasBankSnapshot;

	private OwnedItems(
		List<String> originals,
		Set<String> names,
		Map<String, String> lastEquipped,
		boolean hasBankSnapshot)
	{
		this.originals = Collections.unmodifiableList(originals);
		this.names = Collections.unmodifiableSet(names);
		this.lastEquipped = lastEquipped;
		this.hasBankSnapshot = hasBankSnapshot;
	}

	public static OwnedItems none()
	{
		return new OwnedItems(List.of(), Set.of(), Map.of(), false);
	}

	public static OwnedItems withoutBank(Set<String> names)
	{
		return of(names, false, Map.of());
	}

	public static OwnedItems withoutBank(Set<String> names, Map<String, String> lastEquipped)
	{
		return of(names, false, lastEquipped);
	}

	public static OwnedItems withBank(Set<String> names)
	{
		return of(names, true, Map.of());
	}

	public static OwnedItems withBank(Set<String> names, Map<String, String> lastEquipped)
	{
		return of(names, true, lastEquipped);
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
		String owned = OwnedItemNames.preferredOwnedName(wikiItem.getName(), originals, lastEquipped);
		return owned == null ? wikiItem : GearItem.named(owned);
	}

	public Set<String> names()
	{
		return names;
	}

	private static OwnedItems of(Set<String> names, boolean hasBankSnapshot, Map<String, String> lastEquipped)
	{
		List<String> originals = new ArrayList<>();
		Set<String> keys = new HashSet<>();
		Map<String, String> equipped = copyLastEquipped(lastEquipped);
		if (names == null)
		{
			return new OwnedItems(originals, keys, equipped, hasBankSnapshot);
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
		return new OwnedItems(originals, keys, equipped, hasBankSnapshot);
	}

	private static Map<String, String> copyLastEquipped(Map<String, String> lastEquipped)
	{
		if (lastEquipped == null || lastEquipped.isEmpty())
		{
			return Map.of();
		}
		return Map.copyOf(lastEquipped);
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		if (!(other instanceof OwnedItems))
		{
			return false;
		}
		OwnedItems owned = (OwnedItems) other;
		return hasBankSnapshot == owned.hasBankSnapshot
			&& names.equals(owned.names)
			&& lastEquipped.equals(owned.lastEquipped);
	}

	@Override
	public int hashCode()
	{
		return (names.hashCode() * 31 + Boolean.hashCode(hasBankSnapshot)) * 31 + lastEquipped.hashCode();
	}
}
