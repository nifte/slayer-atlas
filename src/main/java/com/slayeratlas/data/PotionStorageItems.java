package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.ScriptID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;

public final class PotionStorageItems
{
	public static final int COMPONENTS_PER_POTION = 5;

	private PotionStorageItems()
	{
	}

	public static int storeIndex(int potionOrdinal)
	{
		return potionOrdinal * COMPONENTS_PER_POTION;
	}

	public static boolean tracksVarp(int varpId)
	{
		return varpId == VarPlayerID.POTIONSTORE_BASE_VAR_1
			|| varpId == VarPlayerID.POTIONSTORE_BASE_VAR_2
			|| varpId == VarPlayerID.POTIONSTORE_BASE_VAR_3
			|| varpId == VarPlayerID.POTIONSTORE_BASE_VAR_4
			|| varpId == VarPlayerID.POTIONSTORE_BASE_VAR_5
			|| varpId == VarPlayerID.POTIONSTORE_BASE_VAR_6
			|| varpId == VarPlayerID.POTIONSTORE_VIALS;
	}

	public static List<Integer> fromClient(Client client)
	{
		if (client == null)
		{
			return null;
		}
		List<Integer> fromWidgets = fromWidgets(client);
		if (useWidgets(fromWidgets))
		{
			return fromWidgets;
		}
		if (!bankOpen(client))
		{
			return null;
		}
		return fromScripts(client);
	}

	static int parseDoses(String text)
	{
		if (text == null || text.isEmpty())
		{
			return 0;
		}
		int colon = text.lastIndexOf(':');
		if (colon < 0 || colon + 1 >= text.length())
		{
			return 0;
		}
		try
		{
			return Integer.parseInt(text.substring(colon + 1).trim().replace(",", ""));
		}
		catch (NumberFormatException ignored)
		{
			return 0;
		}
	}

	public static boolean bankOpen(Client client)
	{
		return client != null
			&& (client.getWidget(InterfaceID.Bankmain.ITEMS) != null
			|| client.getWidget(InterfaceID.Bankmain.POTIONSTORE_ITEMS) != null);
	}

	static boolean useWidgets(List<?> fromWidgets)
	{
		return fromWidgets != null && !fromWidgets.isEmpty();
	}

	public static boolean storeBuilt(Client client)
	{
		Widget[] children = storeChildren(client);
		return children != null && children.length >= 5;
	}

	public static List<PotionStorageSlot> slots(Client client)
	{
		if (client == null)
		{
			return null;
		}
		List<PotionStorageSlot> fromWidgets = slotsFromWidgets(client);
		if (useWidgets(fromWidgets))
		{
			return fromWidgets;
		}
		if (!bankOpen(client))
		{
			return null;
		}
		return slotsFromScripts(client);
	}

	public static int storeIndexOf(List<PotionStorageSlot> slots, int itemId)
	{
		if (slots == null)
		{
			return -1;
		}
		for (PotionStorageSlot slot : slots)
		{
			if (slot != null && slot.itemId() == itemId)
			{
				return slot.storeIndex();
			}
		}
		return -1;
	}

	public static int storeIndexOf(Client client, int itemId)
	{
		return storeIndexOf(slots(client), itemId);
	}

	public static void prepareClickTargets(Client client)
	{
		if (client == null)
		{
			return;
		}
		Widget store = client.getWidget(InterfaceID.Bankmain.POTIONSTORE_ITEMS);
		if (store == null || store.getChildren() != null)
		{
			return;
		}
		int slots = enumSize(client, EnumID.POTIONSTORE_POTIONS)
			+ enumSize(client, EnumID.POTIONSTORE_UNFINISHED_POTIONS)
			+ 1;
		int childIdx = 0;
		for (int i = 0; i < slots; i++)
		{
			for (int j = 0; j < COMPONENTS_PER_POTION; j++)
			{
				store.createChild(childIdx++, WidgetType.GRAPHIC);
			}
		}
		store.createChild(childIdx++, WidgetType.GRAPHIC);
		store.createChild(childIdx++, WidgetType.RECTANGLE);
		store.createChild(childIdx++, WidgetType.TEXT);
		store.createChild(childIdx++, WidgetType.RECTANGLE);
		store.createChild(childIdx++, WidgetType.TEXT);
	}

	private static int enumSize(Client client, int enumId)
	{
		try
		{
			EnumComposition store = client.getEnum(enumId);
			return store == null || store.getIntVals() == null ? 0 : store.getIntVals().length;
		}
		catch (RuntimeException ignored)
		{
			return 0;
		}
	}

	public static List<PotionStorageSlot> slotsFromScripts(Client client)
	{
		try
		{
			List<PotionStorageSlot> slots = new ArrayList<>();
			int ordinal = 0;
			ordinal = collectSlots(client, client.getEnum(EnumID.POTIONSTORE_POTIONS), slots, ordinal);
			collectSlots(client, client.getEnum(EnumID.POTIONSTORE_UNFINISHED_POTIONS), slots, ordinal);
			return slots;
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
	}

	private static int collectSlots(
		Client client,
		EnumComposition store,
		List<PotionStorageSlot> slots,
		int ordinal)
	{
		if (store == null || store.getIntVals() == null)
		{
			return ordinal;
		}
		for (int potionEnumId : store.getIntVals())
		{
			EnumComposition potion = client.getEnum(potionEnumId);
			if (potion != null)
			{
				int doses = scriptInt(client, ScriptID.POTIONSTORE_DOSES, potionEnumId);
				int withdrawDoses = scriptInt(client, ScriptID.POTIONSTORE_WITHDRAW_DOSES, potionEnumId);
				if (doses > 0 && withdrawDoses > 0)
				{
					int itemId = potion.getIntValue(withdrawDoses);
					if (itemId > 0)
					{
						slots.add(new PotionStorageSlot(itemId, doses / withdrawDoses, storeIndex(ordinal)));
					}
				}
			}
			ordinal++;
		}
		return ordinal;
	}

	public static List<PotionStorageSlot> slotsFromWidgets(Client client)
	{
		try
		{
			Widget[] children = storeChildren(client);
			if (children == null)
			{
				return null;
			}
			List<PotionStorageSlot> slots = new ArrayList<>();
			for (int i = 0; i + 4 < children.length; i += COMPONENTS_PER_POTION)
			{
				Widget item = children[i + 1];
				Widget doses = children[i + 3];
				if (item == null || item.getItemId() <= 0 || doses == null)
				{
					continue;
				}
				int doseCount = parseDoses(doses.getText());
				if (doseCount > 0)
				{
					slots.add(new PotionStorageSlot(item.getItemId(), Math.max(1, doseCount / 4), i));
				}
			}
			return slots;
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
	}

	private static List<Integer> fromScripts(Client client)
	{
		try
		{
			List<Integer> ids = new ArrayList<>();
			collect(client, client.getEnum(EnumID.POTIONSTORE_POTIONS), ids);
			collect(client, client.getEnum(EnumID.POTIONSTORE_UNFINISHED_POTIONS), ids);
			return ids;
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
	}

	private static void collect(Client client, EnumComposition store, List<Integer> ids)
	{
		if (store == null || store.getIntVals() == null)
		{
			return;
		}
		for (int potionEnumId : store.getIntVals())
		{
			EnumComposition potion = client.getEnum(potionEnumId);
			if (potion == null)
			{
				continue;
			}
			int doses = scriptInt(client, ScriptID.POTIONSTORE_DOSES, potionEnumId);
			int withdrawDoses = scriptInt(client, ScriptID.POTIONSTORE_WITHDRAW_DOSES, potionEnumId);
			if (doses <= 0 || withdrawDoses <= 0)
			{
				continue;
			}
			int itemId = potion.getIntValue(withdrawDoses);
			if (itemId > 0)
			{
				ids.add(itemId);
			}
		}
	}

	private static int scriptInt(Client client, int scriptId, int argument)
	{
		client.runScript(scriptId, argument);
		int[] stack = client.getIntStack();
		if (stack == null || stack.length == 0)
		{
			return 0;
		}
		return stack[0];
	}

	private static List<Integer> fromWidgets(Client client)
	{
		try
		{
			Widget[] children = storeChildren(client);
			if (children == null)
			{
				return null;
			}
			List<Integer> ids = new ArrayList<>();
			for (int i = 0; i + 4 < children.length; i += 5)
			{
				Widget item = children[i + 1];
				Widget doses = children[i + 3];
				if (item == null || item.getItemId() <= 0 || doses == null)
				{
					continue;
				}
				if (parseDoses(doses.getText()) > 0)
				{
					ids.add(item.getItemId());
				}
			}
			return ids;
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
	}

	private static Widget[] storeChildren(Client client)
	{
		if (client == null)
		{
			return null;
		}
		Widget store = client.getWidget(InterfaceID.Bankmain.POTIONSTORE_ITEMS);
		if (store == null)
		{
			return null;
		}
		Widget[] children = store.getDynamicChildren();
		if (children != null && children.length > 0)
		{
			return children;
		}
		return store.getChildren();
	}
}
