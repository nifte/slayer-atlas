package com.slayerguide;

import java.awt.Component;
import java.awt.Container;

final class ComponentLookup
{
	private ComponentLookup()
	{
	}

	static Component named(Container root, String name)
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
}
