package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearLoadout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GearTab
{
	private final CombatStyle style;
	private final boolean saved;

	private GearTab(CombatStyle style, boolean saved)
	{
		this.style = style;
		this.saved = saved;
	}

	public static GearTab style(CombatStyle style)
	{
		return new GearTab(style, false);
	}

	public static GearTab saved()
	{
		return new GearTab(null, true);
	}

	public static GearTab initial(boolean hasSaved, List<GearLoadout> loadouts)
	{
		if (hasSaved)
		{
			return saved();
		}
		if (loadouts != null)
		{
			for (GearLoadout loadout : loadouts)
			{
				if (loadout != null && loadout.getStyle() != null)
				{
					return style(loadout.getStyle());
				}
			}
		}
		return style(CombatStyle.MELEE);
	}

	public static List<GearTab> of(List<GearLoadout> loadouts, boolean includeSaved)
	{
		List<GearTab> tabs = new ArrayList<>();
		if (loadouts != null)
		{
			for (GearLoadout loadout : loadouts)
			{
				if (loadout != null && loadout.getStyle() != null)
				{
					tabs.add(style(loadout.getStyle()));
				}
			}
		}
		if (includeSaved)
		{
			tabs.add(saved());
		}
		return tabs;
	}

	public boolean isSaved()
	{
		return saved;
	}

	public CombatStyle style()
	{
		return style;
	}

	public String displayName()
	{
		return saved ? PanelCopy.SAVED_LOADOUT : style.displayName();
	}

	public String componentName()
	{
		return "style-tab-" + (saved ? "saved" : style.name().toLowerCase());
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		if (!(other instanceof GearTab))
		{
			return false;
		}
		GearTab tab = (GearTab) other;
		return saved == tab.saved && style == tab.style;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(style, saved);
	}
}
