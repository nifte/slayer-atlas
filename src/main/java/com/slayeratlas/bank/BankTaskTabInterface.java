package com.slayeratlas.bank;

import com.slayeratlas.ui.PanelCopy;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.ScriptEvent;
import net.runelite.api.ScriptID;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.bank.BankSearch;

@Singleton
public class BankTaskTabInterface
{
	static final String VIEW_TAB = "View tab";
	static final String TAB_NAME = PanelCopy.TITLE;
	static final String ICON_NAME = "slayer-atlas-icon";
	static final String LEGACY_TAB_NAME = "slayer-atlas";

	@Getter
	private boolean loadoutTabActive;

	private Widget parent;
	private Widget background;
	private Widget icon;
	private boolean showButton;
	private Runnable onActivated;
	private Runnable onClicked;

	private final Client client;
	private final BankSearch bankSearch;

	@Inject
	public BankTaskTabInterface(Client client, BankSearch bankSearch)
	{
		this.client = client;
		this.bankSearch = bankSearch;
	}

	void setShowButton(boolean showButton)
	{
		this.showButton = showButton;
	}

	void setOnActivated(Runnable onActivated)
	{
		this.onActivated = onActivated;
	}

	void setOnClicked(Runnable onClicked)
	{
		this.onClicked = onClicked;
	}

	public void init()
	{
		if (!showButton)
		{
			hide();
			return;
		}
		if (isHidden())
		{
			return;
		}

		parent = client.getWidget(InterfaceID.Bankmain.UNIVERSE);
		if (parent == null)
		{
			return;
		}

		Widget existingBackground = childNamed(TAB_NAME);
		if (existingBackground == null)
		{
			existingBackground = childNamed(LEGACY_TAB_NAME);
		}
		Widget existingIcon = childNamed(ICON_NAME);
		if (existingBackground != null && existingIcon != null)
		{
			background = existingBackground;
			icon = existingIcon;
			background.setName(TAB_NAME);
			background.setAction(1, VIEW_TAB);
			background.setHidden(false);
			icon.setHidden(false);
			return;
		}

		background = existingBackground == null ? createGraphic(
			TAB_NAME,
			SpriteID.Miscgraphics3.UNKNOWN_BUTTON_SQUARE_SMALL,
			BankTaskButtonLayout.SIZE,
			BankTaskButtonLayout.SIZE,
			BankTaskButtonLayout.X,
			BankTaskButtonLayout.Y) : existingBackground;
		background.setName(TAB_NAME);
		background.setHidden(false);
		background.setAction(1, VIEW_TAB);
		background.setOnOpListener((JavaScriptCallback) this::handleTagTab);

		icon = existingIcon == null ? createGraphic(
			ICON_NAME,
			SpriteID.Staticons2.SLAYER,
			BankTaskButtonLayout.iconSize(),
			BankTaskButtonLayout.iconSize(),
			BankTaskButtonLayout.iconX(),
			BankTaskButtonLayout.iconY()) : existingIcon;
		icon.setHidden(false);
	}

	public void unload()
	{
		loadoutTabActive = false;
		parent = null;
		background = null;
		icon = null;
	}

	public void destroy()
	{
		if (loadoutTabActive)
		{
			closeTab();
			bankSearch.reset(true);
		}
		hide();
		unload();
	}

	public void handleClick(MenuOptionClicked event)
	{
		if (isHidden() || !loadoutTabActive)
		{
			return;
		}
		if (isOurButton(event.getWidget()))
		{
			return;
		}
		if (BankTaskTabClicks.closesLoadoutTab(event.getMenuOption(), event.getMenuTarget(), event.getParam1()))
		{
			closeTab();
		}
	}

	public void handleCurrentTab(int tab)
	{
		if (loadoutTabActive && BankTaskTabClicks.isPotionStoreTab(tab))
		{
			closeTab();
		}
	}

	public void handleSearch()
	{
		if (loadoutTabActive)
		{
			closeTab();
			client.setVarcStrValue(VarClientID.MESLAYERINPUT, "");
			client.setVarcIntValue(VarClientID.MESLAYERMODE, 0);
		}
	}

	public boolean isHidden()
	{
		Widget widget = client.getWidget(InterfaceID.Bankmain.UNIVERSE);
		return widget == null || widget.isHidden();
	}

	public void refreshTab()
	{
		if (!loadoutTabActive)
		{
			return;
		}
		client.setVarbit(VarbitID.BANK_CURRENTTAB, 0);
		bankSearch.reset(true);
		clearSearchButtonTimer();
	}

	private void handleTagTab(ScriptEvent event)
	{
		if (event.getOp() != 2)
		{
			return;
		}
		boolean wasInPotionStorage = BankTaskTabClicks.isPotionStoreTab(
			client.getVarbitValue(VarbitID.BANK_CURRENTTAB));
		client.setVarbit(VarbitID.BANK_CURRENTTAB, 0);
		if (loadoutTabActive)
		{
			closeTab();
			bankSearch.reset(true);
		}
		else
		{
			if (wasInPotionStorage)
			{
				client.menuAction(-1, InterfaceID.Bankmain.POTIONSTORE_BUTTON, MenuAction.CC_OP, 1, -1, "Potion store", "");
			}
			activateTab();
			if (onClicked != null)
			{
				onClicked.run();
			}
		}
		client.playSoundEffect(SoundEffectID.UI_BOOP);
	}

	private void activateTab()
	{
		if (loadoutTabActive)
		{
			return;
		}
		if (background != null)
		{
			background.setSpriteId(SpriteID.Miscgraphics3.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED);
			background.revalidate();
		}
		loadoutTabActive = true;
		if (onActivated != null)
		{
			onActivated.run();
		}
		bankSearch.reset(true);
		clearSearchButtonTimer();
	}

	void closeTab()
	{
		loadoutTabActive = false;
		if (background != null)
		{
			background.setSpriteId(SpriteID.Miscgraphics3.UNKNOWN_BUTTON_SQUARE_SMALL);
			background.revalidate();
		}
	}

	private void hide()
	{
		if (icon != null)
		{
			icon.setHidden(true);
		}
		if (background != null)
		{
			background.setHidden(true);
		}
	}

	private void clearSearchButtonTimer()
	{
		Widget searchButtonBackground = client.getWidget(InterfaceID.Bankmain.SEARCH);
		if (searchButtonBackground != null)
		{
			searchButtonBackground.setOnTimerListener((Object[]) null);
			searchButtonBackground.setSpriteId(SpriteID.Miscgraphics.EQUIPMENT_SLOT_TILE);
		}
	}

	private boolean isOurButton(Widget widget)
	{
		return widget != null && (widget == background || widget == icon);
	}

	private Widget childNamed(String name)
	{
		Widget[] children = parent.getDynamicChildren();
		if (children == null)
		{
			return null;
		}
		for (Widget child : children)
		{
			if (child != null && name.equals(child.getName()))
			{
				return child;
			}
		}
		return null;
	}

	private Widget createGraphic(String name, int spriteId, int width, int height, int x, int y)
	{
		Widget widget = parent.createChild(-1, WidgetType.GRAPHIC);
		widget.setOriginalWidth(width);
		widget.setOriginalHeight(height);
		widget.setOriginalX(x);
		widget.setOriginalY(y);
		widget.setSpriteId(spriteId);
		widget.setOnOpListener(ScriptID.NULL);
		widget.setHasListener(true);
		widget.setName(name);
		widget.revalidate();
		return widget;
	}
}
