package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.slayeratlas.ComponentLookup;
import com.slayeratlas.data.GearItem;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.junit.Test;

public class ItemWikiMenuTest
{
	@Test
	public void wikiButtonOpensTheItemPage()
	{
		List<String> opened = new ArrayList<>();
		JPopupMenu menu = ItemWikiMenu.forItem(GearItem.named("Shark"), opened::add);
		JMenuItem wiki = (JMenuItem) ComponentLookup.named(menu, "item-wiki");
		assertNotNull(wiki);
		assertEquals(PanelCopy.OPEN_WIKI, wiki.getText());
		wiki.doClick();
		assertEquals(List.of(WikiItemUrl.fromName("Shark")), opened);
	}

	@Test
	public void showsWikiAsPlainTextWithoutAnIcon()
	{
		JPopupMenu menu = ItemWikiMenu.forItem(GearItem.named("Shark"), url ->
		{
		});
		JMenuItem wiki = (JMenuItem) ComponentLookup.named(menu, "item-wiki");
		assertNull(wiki.getIcon());
		assertEquals(PanelCopy.OPEN_WIKI, wiki.getText());
		assertEquals(new Insets(5, 5, 5, 5), menu.getInsets());
	}

	@Test
	public void missingItemsHaveNoMenu()
	{
		assertNull(ItemWikiMenu.forItem(null, url ->
		{
		}));
	}
}
