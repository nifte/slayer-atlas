package com.slayeratlas.data;

import java.util.Objects;

public final class CurrentSlayerTask
{
	private final String name;
	private final String location;
	private final int remaining;
	private final int initialAmount;

	public CurrentSlayerTask(String name, String location, int remaining, int initialAmount)
	{
		this.name = name;
		this.location = location;
		this.remaining = remaining;
		this.initialAmount = initialAmount;
	}

	public String getName()
	{
		return name;
	}

	public String getLocation()
	{
		return location;
	}

	public int getRemaining()
	{
		return remaining;
	}

	public int getInitialAmount()
	{
		return initialAmount;
	}

	public boolean hasTask()
	{
		return name != null && !name.trim().isEmpty() && remaining > 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof CurrentSlayerTask))
		{
			return false;
		}
		CurrentSlayerTask other = (CurrentSlayerTask) obj;
		return remaining == other.remaining
			&& initialAmount == other.initialAmount
			&& Objects.equals(name, other.name)
			&& Objects.equals(location, other.location);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, location, remaining, initialAmount);
	}

	public boolean sameAssignment(CurrentSlayerTask other)
	{
		if (other == null)
		{
			return false;
		}
		return Objects.equals(name, other.name) && Objects.equals(location, other.location);
	}
}
