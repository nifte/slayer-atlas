package com.slayeratlas;

import com.slayeratlas.data.BankSnapshotStore;
import com.slayeratlas.data.OwnedItemNames;
import com.slayeratlas.data.OwnedItems;
import com.slayeratlas.data.PotionStorageItems;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
	private final Set<String> potions = new LinkedHashSet<>();
	private final Map<String, String> lastEquipped = new LinkedHashMap<>();
	private final List<Integer> bankIds = new ArrayList<>();
	private final List<Integer> potionIds = new ArrayList<>();
	private boolean hasBankSnapshot;
	private boolean potionsDirty;
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
		names.addAll(potions);
		Map<String, String> equipped = Map.copyOf(lastEquipped);
		return hasBankSnapshot ? OwnedItems.withBank(names, equipped) : OwnedItems.withoutBank(names, equipped);
	}

	public synchronized OwnedItems carried()
	{
		Set<String> names = new LinkedHashSet<>();
		names.addAll(worn);
		names.addAll(inventory);
		return OwnedItems.withoutBank(names);
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
		if (rememberEquipped(worn) && hasBankSnapshot)
		{
			persist(hash);
		}
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
			if (rememberEquipped(worn) && hasBankSnapshot)
			{
				persist(client.getAccountHash());
			}
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

	public synchronized void markPotionsDirty()
	{
		potionsDirty = true;
	}

	public synchronized boolean onClientTick()
	{
		if (!potionsDirty)
		{
			return false;
		}
		potionsDirty = false;
		if (capturePotions() && hasBankSnapshot)
		{
			persist(client.getAccountHash());
			return true;
		}
		return false;
	}

	public static boolean tracksPotionStore(int varpId)
	{
		return PotionStorageItems.tracksVarp(varpId);
	}

	private void loadBank(long hash)
	{
		bank.clear();
		potions.clear();
		bankIds.clear();
		potionIds.clear();
		lastEquipped.clear();
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
		bankIds.addAll(ids);
		potionIds.addAll(store.loadPotions(hash));
		lastEquipped.putAll(store.loadLastEquipped(hash));
		bank.addAll(namesFromIds(bankIds));
		potions.addAll(namesFromIds(potionIds));
	}

	private void saveBank(long hash, ItemContainer container)
	{
		bankIds.clear();
		bankIds.addAll(ids(container));
		bank.clear();
		bank.addAll(namesFromIds(bankIds));
		hasBankSnapshot = true;
		potionsDirty = true;
		persist(hash);
	}

	private boolean capturePotions()
	{
		List<Integer> captured = PotionStorageItems.fromClient(client);
		if (captured == null)
		{
			return false;
		}
		if (captured.isEmpty() && !PotionStorageItems.storeBuilt(client))
		{
			return false;
		}
		potionIds.clear();
		potionIds.addAll(captured);
		potions.clear();
		potions.addAll(namesFromIds(potionIds));
		return true;
	}

	private void persist(long hash)
	{
		if (validAccount(hash))
		{
			store.save(hash, bankIds, potionIds, lastEquipped);
		}
	}

	private boolean rememberEquipped(Set<String> equipped)
	{
		boolean changed = false;
		if (equipped == null)
		{
			return false;
		}
		for (String name : equipped)
		{
			String family = OwnedItemNames.familyKey(name);
			if (family.isEmpty())
			{
				continue;
			}
			String previous = lastEquipped.get(family);
			if (previous != null && OwnedItemNames.sameItem(previous, name))
			{
				continue;
			}
			lastEquipped.put(family, name);
			changed = true;
		}
		return changed;
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
