package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.ScriptID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

public final class PotionStorageItems
{
	private PotionStorageItems()
	{
	}

	public static List<Integer> fromClient(Client client)
	{
		if (client == null)
		{
			return null;
		}
		List<Integer> fromWidgets = fromWidgets(client);
		if (fromWidgets != null && !fromWidgets.isEmpty())
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

	public static boolean storeBuilt(Client client)
	{
		Widget[] children = storeChildren(client);
		return children != null && children.length >= 5;
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
