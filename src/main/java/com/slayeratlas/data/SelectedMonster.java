package com.slayeratlas.data;

import java.util.Objects;
import javax.inject.Singleton;

@Singleton
public class SelectedMonster
{
	private final boolean enabled;
	private volatile SlayerMonster monster;
	private Runnable onChange;

	public SelectedMonster()
	{
		this(true);
	}

	private SelectedMonster(boolean enabled)
	{
		this.enabled = enabled;
	}

	public static SelectedMonster none()
	{
		return new SelectedMonster(false);
	}

	public void setOnChange(Runnable onChange)
	{
		this.onChange = onChange;
	}

	public void set(SlayerMonster monster)
	{
		if (!enabled || same(this.monster, monster))
		{
			return;
		}
		this.monster = monster;
		if (onChange != null)
		{
			onChange.run();
		}
	}

	public SlayerMonster get()
	{
		return monster;
	}

	public SlayerMonster or(SlayerMonster fallback)
	{
		return monster != null ? monster : fallback;
	}

	private static boolean same(SlayerMonster left, SlayerMonster right)
	{
		if (left == right)
		{
			return true;
		}
		if (left == null || right == null)
		{
			return false;
		}
		return Objects.equals(left.getId(), right.getId());
	}
}
