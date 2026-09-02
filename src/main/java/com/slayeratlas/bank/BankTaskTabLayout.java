package com.slayeratlas.bank;

import com.slayeratlas.data.GearLoadout;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

public final class BankTaskTabLayout
{
	private BankTaskTabLayout()
	{
	}

	public static void apply(Client client, ItemManager items, GearLoadout loadout)
	{
		if (client == null || items == null || loadout == null)
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
			return;
		}
		int[] grids = BankTabLayout.gridIndexes(loadout, names);
		int maxIndex = 0;
		for (int i = 0; i < shown.size(); i++)
		{
			int grid = grids[i];
			if (grid < 0)
			{
				continue;
			}
			Widget widget = shown.get(i);
			widget.setOriginalX(BankTabLayout.x(grid));
			widget.setOriginalY(BankTabLayout.y(grid));
			widget.revalidate();
			maxIndex = Math.max(maxIndex, grid);
		}
		container.setScrollHeight(BankTabLayout.scrollHeight(maxIndex));
		container.revalidate();
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
