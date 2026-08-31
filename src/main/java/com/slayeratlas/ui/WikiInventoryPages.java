package com.slayeratlas.ui;

import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.RankedGearLoadout;
import com.slayeratlas.data.RankedLoadoutInventories;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.WikiPageNames;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class WikiInventoryPages
{
	private WikiInventoryPages()
	{
	}

	public static void load(
		WikiInventoryClient client,
		List<RankedGearLoadout> ranked,
		java.util.function.Consumer<List<RankedGearLoadout>> onLoaded)
	{
		load(client, null, ranked, (updated, ignored) -> onLoaded.accept(updated));
	}

	public static void load(
		WikiInventoryClient client,
		SlayerMonster monster,
		List<RankedGearLoadout> ranked,
		BiConsumer<List<RankedGearLoadout>, List<GearItem>> onLoaded)
	{
		if (onLoaded == null)
		{
			return;
		}
		List<RankedGearLoadout> source = ranked == null ? List.of() : ranked;
		if (client == null)
		{
			onLoaded.accept(source, List.of());
			return;
		}
		List<String> pages = RankedLoadoutInventories.pageNames(source);
		if (pages.isEmpty())
		{
			pages = WikiPageNames.inventoryPages(monster);
			loadFallback(client, pages, 0, onLoaded);
			return;
		}
		load(client, source, pages, 0, new HashMap<>(), onLoaded);
	}

	private static void loadFallback(
		WikiInventoryClient client,
		List<String> pages,
		int index,
		BiConsumer<List<RankedGearLoadout>, List<GearItem>> onLoaded)
	{
		if (pages == null || index >= pages.size())
		{
			onLoaded.accept(List.of(), List.of());
			return;
		}
		String page = pages.get(index);
		client.load(page, items ->
		{
			if (hasItem(items))
			{
				onLoaded.accept(List.of(), items);
				return;
			}
			loadFallback(client, pages, index + 1, onLoaded);
		});
	}

	private static boolean hasItem(List<GearItem> items)
	{
		if (items == null)
		{
			return false;
		}
		for (GearItem item : items)
		{
			if (item != null)
			{
				return true;
			}
		}
		return false;
	}

	private static void load(
		WikiInventoryClient client,
		List<RankedGearLoadout> ranked,
		List<String> pages,
		int index,
		Map<String, List<GearItem>> inventories,
		BiConsumer<List<RankedGearLoadout>, List<GearItem>> onLoaded)
	{
		if (index >= pages.size())
		{
			onLoaded.accept(RankedLoadoutInventories.withPageInventories(ranked, inventories), List.of());
			return;
		}
		String page = pages.get(index);
		client.load(page, items ->
		{
			inventories.put(page, items == null ? List.of() : items);
			load(client, ranked, pages, index + 1, inventories, onLoaded);
		});
	}
}
