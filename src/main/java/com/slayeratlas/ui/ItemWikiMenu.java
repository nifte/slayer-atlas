package com.slayeratlas.ui;

import com.slayeratlas.data.GearItem;
import java.awt.Color;
import java.awt.Cursor;
import java.util.function.Consumer;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import net.runelite.client.ui.ColorScheme;
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
		if (item == null || openUrl == null)
		{
			return null;
		}
		String url = WikiItemUrl.fromName(item.getName());
		if (url.isEmpty())
		{
			return null;
		}
		JPopupMenu menu = new JPopupMenu();
		menu.setName("item-wiki-menu");
		menu.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		JMenuItem wiki = new JMenuItem(PanelCopy.OPEN_WIKI, ExternalLinkIcon.icon());
		wiki.setName("item-wiki");
		wiki.setFont(PanelFonts.bodyBold());
		wiki.setForeground(Color.WHITE);
		wiki.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		wiki.setOpaque(true);
		wiki.setHorizontalTextPosition(SwingConstants.LEFT);
		wiki.setIconTextGap(6);
		wiki.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		wiki.addActionListener(event -> openUrl.accept(url));
		menu.add(wiki);
		return menu;
	}
}
