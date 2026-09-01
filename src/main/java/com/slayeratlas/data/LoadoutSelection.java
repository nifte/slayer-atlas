package com.slayeratlas.data;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

@Singleton
public class LoadoutSelection
{
	private final Map<String, Selected> selected = new HashMap<>();
	private final boolean enabled;
	private Runnable onChange;

	public LoadoutSelection()
	{
		this(true);
	}

	private LoadoutSelection(boolean enabled)
	{
		this.enabled = enabled;
	}

	public static LoadoutSelection none()
	{
		return new LoadoutSelection(false);
	}

	public void setOnChange(Runnable onChange)
	{
		this.onChange = onChange;
	}

	public void set(String monsterId, CombatStyle style, boolean saved, GearLoadout loadout)
	{
		if (!enabled || monsterId == null || monsterId.isEmpty())
		{
			return;
		}
		selected.put(monsterId, new Selected(style, saved, loadout));
		if (onChange != null)
		{
			onChange.run();
		}
	}

	public GearLoadout loadout(String monsterId)
	{
		Selected value = get(monsterId);
		return value == null ? null : value.loadout;
	}

	public CombatStyle style(String monsterId)
	{
		Selected value = get(monsterId);
		return value == null ? null : value.style;
	}

	public boolean saved(String monsterId)
	{
		Selected value = get(monsterId);
		return value != null && value.saved;
	}

	private Selected get(String monsterId)
	{
		return monsterId == null ? null : selected.get(monsterId);
	}

	private static final class Selected
	{
		private final CombatStyle style;
		private final boolean saved;
		private final GearLoadout loadout;

		private Selected(CombatStyle style, boolean saved, GearLoadout loadout)
		{
			this.style = style;
			this.saved = saved;
			this.loadout = loadout;
		}
	}
}
