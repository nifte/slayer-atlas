package com.slayeratlas.ui;

import com.slayeratlas.data.GearItem;
import java.util.function.Consumer;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.client.util.LinkBrowser;

public final class ItemWikiMenu
{
	private ItemWikiMenu()
	{
	}

	public static JPopupMenu forItem(GearItem item)
	{
		return forItem(item, LinkBrowser::browse);
	}

	public static JPopupMenu forItem(GearItem item, Consumer<String> openUrl)
	{
		if (item == null)
		{
			return null;
		}
		return forName(item.getName(), openUrl);
	}

	public static JPopupMenu forName(String name)
	{
		return forName(name, LinkBrowser::browse);
	}

	public static JPopupMenu forName(String name, Consumer<String> openUrl)
	{
		if (name == null || name.isEmpty() || openUrl == null)
		{
			return null;
		}
		String url = WikiItemUrl.fromName(name);
		if (url.isEmpty())
		{
			return null;
		}
		JPopupMenu menu = new JPopupMenu();
		menu.setName("item-wiki-menu");
		menu.setBorder(new EmptyBorder(5, 5, 5, 5));
		JMenuItem wiki = new JMenuItem(PanelCopy.OPEN_WIKI);
		wiki.setName("item-wiki");
		wiki.addActionListener(event -> openUrl.accept(url));
		menu.add(wiki);
		return menu;
	}
}
