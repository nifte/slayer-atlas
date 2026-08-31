package com.slayeratlas;

import com.slayeratlas.data.BankSnapshotStore;
import com.slayeratlas.data.OwnedItems;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.game.ItemManager;

@Singleton
public class OwnedItemsTracker
{
	private final Client client;
	private final ItemManager itemManager;
	private final BankSnapshotStore store;
	private final Set<String> worn = new LinkedHashSet<>();
	private final Set<String> inventory = new LinkedHashSet<>();
	private final Set<String> bank = new LinkedHashSet<>();
	private boolean hasBankSnapshot;
	private long accountHash;

	@Inject
	public OwnedItemsTracker(Client client, ItemManager itemManager, BankSnapshotStore store)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.store = store;
	}

	public synchronized OwnedItems snapshot()
	{
		Set<String> names = new LinkedHashSet<>();
		names.addAll(worn);
		names.addAll(inventory);
		names.addAll(bank);
		return hasBankSnapshot ? OwnedItems.withBank(names) : OwnedItems.withoutBank(names);
	}

	public synchronized void syncAccount()
	{
		long hash = client.getAccountHash();
		if (hash != accountHash)
		{
			accountHash = hash;
			loadBank(hash);
		}
		capture(InventoryID.WORN, worn);
		capture(InventoryID.INV, inventory);
		ItemContainer openBank = client.getItemContainer(InventoryID.BANK);
		if (isUsableBank(openBank))
		{
			saveBank(hash, openBank);
		}
	}

	public synchronized void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event == null)
		{
			return;
		}
		int id = event.getContainerId();
		if (id == InventoryID.WORN)
		{
			replace(worn, event.getItemContainer());
			return;
		}
		if (id == InventoryID.INV)
		{
			replace(inventory, event.getItemContainer());
			return;
		}
		if (id == InventoryID.BANK && isUsableBank(event.getItemContainer()))
		{
			saveBank(client.getAccountHash(), event.getItemContainer());
		}
	}

	private void loadBank(long hash)
	{
		bank.clear();
		if (!validAccount(hash))
		{
			hasBankSnapshot = false;
			return;
		}
		List<Integer> ids = store.load(hash);
		if (ids == null)
		{
			hasBankSnapshot = false;
			return;
		}
		hasBankSnapshot = true;
		bank.addAll(namesFromIds(ids));
	}

	private void saveBank(long hash, ItemContainer container)
	{
		List<Integer> ids = ids(container);
		bank.clear();
		bank.addAll(namesFromIds(ids));
		hasBankSnapshot = true;
		if (validAccount(hash))
		{
			store.save(hash, ids);
		}
	}

	private void capture(int inventoryId, Set<String> names)
	{
		replace(names, client.getItemContainer(inventoryId));
	}

	private void replace(Set<String> names, ItemContainer container)
	{
		names.clear();
		names.addAll(namesFromIds(ids(container)));
	}

	private List<String> namesFromIds(List<Integer> ids)
	{
		List<String> names = new ArrayList<>();
		if (ids == null)
		{
			return names;
		}
		for (Integer id : ids)
		{
			if (id == null || id <= 0)
			{
				continue;
			}
			int canonical = itemManager.canonicalize(id);
			ItemComposition composition = itemManager.getItemComposition(canonical);
			if (composition == null || composition.getName() == null || composition.getName().equals("null"))
			{
				continue;
			}
			names.add(composition.getName());
		}
		return names;
	}

	private static List<Integer> ids(ItemContainer container)
	{
		List<Integer> ids = new ArrayList<>();
		if (container == null)
		{
			return ids;
		}
		Item[] items = container.getItems();
		if (items == null)
		{
			return ids;
		}
		for (Item item : items)
		{
			if (item != null && item.getId() > 0)
			{
				ids.add(item.getId());
			}
		}
		return ids;
	}

	private static boolean isUsableBank(ItemContainer container)
	{
		return container != null && container.count() > 0;
	}

	private static boolean validAccount(long hash)
	{
		return hash != 0 && hash != -1;
	}
}
