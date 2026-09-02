package com.slayeratlas.bank;

import com.slayeratlas.data.GearLoadout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.ScriptEvent;
import net.runelite.api.ScriptID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

public final class BankTaskTabLayout
{
	private BankTaskTabLayout()
	{
	}

	public static Map<Integer, Integer> apply(Client client, ItemManager items, GearLoadout loadout)
	{
		if (client == null || items == null || loadout == null)
		{
			return Map.of();
		}
		Widget container = client.getWidget(InterfaceID.Bankmain.ITEMS);
		if (container == null)
		{
			return Map.of();
		}
		Widget[] children = container.getChildren();
		if (children == null || children.length == 0)
		{
			return Map.of();
		}
		hideSurplusBankStacks(children, client.getItemContainer(InventoryID.BANK));
		List<Widget> shown = new ArrayList<>();
		List<String> names = new ArrayList<>();
		for (Widget child : children)
		{
			if (!isShownItem(child))
			{
				continue;
			}
			shown.add(child);
			names.add(itemName(items, child.getItemId()));
		}
		if (shown.isEmpty())
		{
			return Map.of();
		}
		boolean[] placed = new boolean[shown.size()];
		Map<Integer, Integer> copies = new HashMap<>();
		int maxIndex = 0;
		for (BankTabLayout.Placement placement : BankTabLayout.placements(loadout, names))
		{
			Widget source = shown.get(placement.sourceIndex());
			Widget widget = source;
			if (placed[placement.sourceIndex()])
			{
				widget = takeFree(children);
				if (widget == null)
				{
					break;
				}
				copyItem(widget, source);
				recordCopy(copies, children, widget, source);
			}
			placed[placement.sourceIndex()] = true;
			widget.setOriginalX(BankTabLayout.x(placement.gridIndex()));
			widget.setOriginalY(BankTabLayout.y(placement.gridIndex()));
			widget.revalidate();
			maxIndex = Math.max(maxIndex, placement.gridIndex());
		}
		container.setScrollHeight(BankTabLayout.scrollHeight(maxIndex));
		container.revalidate();
		return Map.copyOf(copies);
	}

	static boolean isSurplusStack(int seenCount, int bankStacks)
	{
		return seenCount > bankStacks;
	}

	private static void hideSurplusBankStacks(Widget[] children, ItemContainer bank)
	{
		if (bank == null)
		{
			return;
		}
		Map<Integer, Integer> stacks = stackCounts(bank);
		Map<Integer, Integer> seen = new HashMap<>();
		for (Widget child : children)
		{
			if (!isShownItem(child))
			{
				continue;
			}
			int itemId = child.getItemId();
			int bankStacks = stacks.getOrDefault(itemId, 0);
			if (bankStacks == 0)
			{
				continue;
			}
			int count = seen.merge(itemId, 1, Integer::sum);
			if (!isSurplusStack(count, bankStacks))
			{
				continue;
			}
			child.setHidden(true);
			child.setItemId(-1);
			child.setItemQuantity(0);
		}
	}

	static Map<Integer, Integer> stackCounts(ItemContainer bank)
	{
		Map<Integer, Integer> counts = new HashMap<>();
		if (bank == null)
		{
			return counts;
		}
		Item[] items = bank.getItems();
		if (items == null)
		{
			return counts;
		}
		for (Item item : items)
		{
			if (item != null && item.getId() > 0)
			{
				counts.merge(item.getId(), 1, Integer::sum);
			}
		}
		return counts;
	}

	static void recordCopy(Map<Integer, Integer> copies, Widget[] children, Widget dest, Widget source)
	{
		int destIndex = indexOf(children, dest);
		int sourceIndex = indexOf(children, source);
		if (copies == null || destIndex < 0 || sourceIndex < 0 || destIndex == sourceIndex)
		{
			return;
		}
		copies.put(destIndex, sourceIndex);
	}

	static int indexOf(Widget[] children, Widget widget)
	{
		if (children == null || widget == null)
		{
			return -1;
		}
		for (int i = 0; i < children.length; i++)
		{
			if (children[i] == widget)
			{
				return i;
			}
		}
		return -1;
	}

	private static Widget takeFree(Widget[] children)
	{
		for (Widget child : children)
		{
			if (child != null && !isShownItem(child) && child.getOriginalHeight() >= BankItemGrid.ITEM_HEIGHT)
			{
				return child;
			}
		}
		return null;
	}

	private static void copyItem(Widget dest, Widget source)
	{
		dest.setHidden(false);
		dest.setOpacity(0);
		dest.setItemId(source.getItemId());
		dest.setItemQuantity(source.getItemQuantity());
		dest.setItemQuantityMode(source.getItemQuantityMode());
		dest.setOriginalWidth(BankItemGrid.ITEM_WIDTH);
		dest.setOriginalHeight(BankItemGrid.ITEM_HEIGHT);
		dest.setName(source.getName());
		dest.setDragDeadTime(source.getDragDeadTime());
		dest.clearActions();
		String[] actions = source.getActions();
		if (actions != null)
		{
			for (int i = 0; i < actions.length; i++)
			{
				if (actions[i] != null)
				{
					dest.setAction(i, actions[i]);
				}
			}
		}
		dest.setOnDragListener(
			ScriptID.BANKMAIN_DRAGSCROLL,
			ScriptEvent.WIDGET_ID,
			ScriptEvent.WIDGET_INDEX,
			ScriptEvent.MOUSE_X,
			ScriptEvent.MOUSE_Y,
			InterfaceID.Bankmain.SCROLLBAR,
			0);
		dest.setOnDragCompleteListener((JavaScriptCallback) event ->
		{
		});
	}

	private static String itemName(ItemManager items, int itemId)
	{
		if (itemId <= 0)
		{
			return "";
		}
		ItemComposition composition = items.getItemComposition(items.canonicalize(itemId));
		return composition == null ? "" : composition.getName();
	}

	private static boolean isShownItem(Widget widget)
	{
		return widget != null && !widget.isHidden() && widget.getItemId() > 0;
	}
}
