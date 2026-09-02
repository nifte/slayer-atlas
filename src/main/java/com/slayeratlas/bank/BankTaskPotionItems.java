package com.slayeratlas.bank;

import com.slayeratlas.data.LoadoutBankMatcher;
import com.slayeratlas.data.LoadoutPotionFilter;
import com.slayeratlas.data.PotionStorageItems;
import com.slayeratlas.data.PotionStorageSlot;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.ScriptEvent;
import net.runelite.api.ScriptID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

public final class BankTaskPotionItems
{
	private BankTaskPotionItems()
	{
	}

	public static void show(
		Client client,
		ItemManager items,
		LoadoutBankMatcher matcher,
		List<PotionStorageSlot> stored)
	{
		if (client == null || items == null || matcher == null || matcher.isEmpty())
		{
			return;
		}
		Widget container = client.getWidget(InterfaceID.Bankmain.ITEMS);
		if (container == null)
		{
			return;
		}
		Widget[] children = container.getChildren();
		if (children == null || children.length == 0)
		{
			return;
		}
		hideStorageExtras(children, client.getItemContainer(InventoryID.BANK), stored);
		if (stored == null || stored.isEmpty())
		{
			container.setScrollHeight(BankItemGrid.scrollHeight(visibleCount(children)));
			container.revalidate();
			return;
		}
		List<String> shown = visibleNames(children, items);
		List<PotionStorageSlot> extra = LoadoutPotionFilter.extra(
			stored,
			matcher,
			shown,
			itemId -> itemName(items, itemId));
		int next = firstFreeIndex(children);
		int slot = visibleCount(children);
		for (PotionStorageSlot potion : extra)
		{
			if (next >= children.length)
			{
				break;
			}
			Widget widget = children[next++];
			if (widget == null || widget.getOriginalHeight() < BankItemGrid.ITEM_HEIGHT)
			{
				break;
			}
			draw(client, items, widget, potion, slot++);
		}
		container.setScrollHeight(BankItemGrid.scrollHeight(slot));
		container.revalidate();
	}

	public static void remapClick(Client client, MenuOptionClicked event, List<PotionStorageSlot> stored)
	{
		if (client == null || event == null || event.getParam1() != InterfaceID.Bankmain.ITEMS)
		{
			return;
		}
		Widget widget = event.getWidget();
		if (widget == null || widget.getItemId() <= 0)
		{
			return;
		}
		ItemContainer bank = client.getItemContainer(InventoryID.BANK);
		if (bank != null && bank.contains(widget.getItemId()))
		{
			return;
		}
		int index = PotionStorageItems.storeIndexOf(stored, widget.getItemId());
		if (index < 0)
		{
			return;
		}
		PotionStorageItems.prepareClickTargets(client);
		event.getMenuEntry().setParam1(InterfaceID.Bankmain.POTIONSTORE_ITEMS);
		event.getMenuEntry().setParam0(index);
	}

	private static void draw(
		Client client,
		ItemManager items,
		Widget widget,
		PotionStorageSlot potion,
		int slot)
	{
		ItemComposition composition = items.getItemComposition(potion.itemId());
		if (composition == null)
		{
			return;
		}
		widget.setHidden(false);
		widget.setOpacity(0);
		widget.setItemId(potion.itemId());
		widget.setItemQuantity(potion.quantity());
		widget.setItemQuantityMode(ItemQuantityMode.ALWAYS);
		widget.setOriginalX(BankItemGrid.x(slot));
		widget.setOriginalY(BankItemGrid.y(slot));
		widget.setOriginalWidth(BankItemGrid.ITEM_WIDTH);
		widget.setOriginalHeight(BankItemGrid.ITEM_HEIGHT);
		widget.setName("<col=ff9040>" + composition.getName() + " ");
		widget.setDragDeadTime(1000);
		widget.clearActions();
		setWithdrawActions(client, widget);
		widget.setOnDragListener(
			ScriptID.BANKMAIN_DRAGSCROLL,
			ScriptEvent.WIDGET_ID,
			ScriptEvent.WIDGET_INDEX,
			ScriptEvent.MOUSE_X,
			ScriptEvent.MOUSE_Y,
			InterfaceID.Bankmain.SCROLLBAR,
			0);
		widget.setOnDragCompleteListener((JavaScriptCallback) event ->
		{
		});
		widget.revalidate();
	}

	private static void setWithdrawActions(Client client, Widget widget)
	{
		int quantityType = client.getVarbitValue(VarbitID.BANK_QUANTITY_TYPE);
		int requestQty = client.getVarbitValue(VarbitID.BANK_REQUESTEDQUANTITY);
		String suffix;
		switch (quantityType)
		{
			case 1:
				suffix = "5";
				break;
			case 2:
				suffix = "10";
				break;
			case 3:
				suffix = Integer.toString(Math.max(1, requestQty));
				break;
			case 4:
				suffix = "All";
				break;
			default:
				suffix = "1";
				break;
		}
		widget.setAction(0, "Withdraw-" + suffix);
		if (quantityType != 0)
		{
			widget.setAction(1, "Withdraw-1");
		}
		widget.setAction(2, "Withdraw-5");
		widget.setAction(3, "Withdraw-10");
		if (requestQty > 0)
		{
			widget.setAction(4, "Withdraw-" + requestQty);
		}
		widget.setAction(5, "Withdraw-X");
		widget.setAction(6, "Withdraw-All");
		widget.setAction(7, "Withdraw-All-but-1");
	}

	static boolean keepStorageWidget(boolean inBank, boolean inStored)
	{
		return inBank || inStored;
	}

	private static void hideStorageExtras(Widget[] children, ItemContainer bank, List<PotionStorageSlot> stored)
	{
		for (Widget child : children)
		{
			if (!isShownItem(child))
			{
				continue;
			}
			if (keepStorageWidget(inBank(bank, child.getItemId()), inStored(stored, child.getItemId())))
			{
				continue;
			}
			child.setHidden(true);
			child.setItemId(-1);
			child.setItemQuantity(0);
		}
	}

	private static boolean inStored(List<PotionStorageSlot> stored, int itemId)
	{
		if (stored == null)
		{
			return false;
		}
		for (PotionStorageSlot slot : stored)
		{
			if (slot != null && slot.itemId() == itemId)
			{
				return true;
			}
		}
		return false;
	}

	private static boolean inBank(ItemContainer bank, int itemId)
	{
		return bank != null && bank.contains(itemId);
	}

	private static List<String> visibleNames(Widget[] children, ItemManager items)
	{
		List<String> names = new ArrayList<>();
		for (Widget child : children)
		{
			if (!isShownItem(child))
			{
				continue;
			}
			String name = itemName(items, child.getItemId());
			if (name != null && !name.isEmpty())
			{
				names.add(name);
			}
		}
		return names;
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

	private static int visibleCount(Widget[] children)
	{
		int count = 0;
		for (Widget child : children)
		{
			if (isShownItem(child))
			{
				count++;
			}
		}
		return count;
	}

	private static int firstFreeIndex(Widget[] children)
	{
		int last = -1;
		for (int i = 0; i < children.length; i++)
		{
			if (isShownItem(children[i]))
			{
				last = i;
			}
		}
		return last + 1;
	}

	private static boolean isShownItem(Widget widget)
	{
		return widget != null && !widget.isHidden() && widget.getItemId() > 0;
	}
}
