package com.slayerguide;

import java.awt.Component;
import java.awt.Container;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

public final class ComponentLookup
{
	private ComponentLookup()
	{
	}

	public static Component named(Container root, String name)
	{
		if (name.equals(root.getName()))
		{
			return root;
		}
		for (Component child : root.getComponents())
		{
			if (name.equals(child.getName()))
			{
				return child;
			}
			if (child instanceof Container)
			{
				Component nested = named((Container) child, name);
				if (nested != null)
				{
					return nested;
				}
			}
		}
		return null;
	}

	public static boolean containsText(Component root, String needle)
	{
		String text = textOf(root);
		if (text != null && text.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT)))
		{
			return true;
		}
		if (root instanceof Container)
		{
			for (Component child : ((Container) root).getComponents())
			{
				if (containsText(child, needle))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static String textOf(Component component)
	{
		if (component instanceof JLabel)
		{
			return ((JLabel) component).getText();
		}
		if (component instanceof JTextComponent)
		{
			return ((JTextComponent) component).getText();
		}
		if (component instanceof AbstractButton)
		{
			return ((AbstractButton) component).getText();
		}
		return null;
	}
}
