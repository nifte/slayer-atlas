package com.slayeratlas.data;

import java.util.Objects;

public final class UnlockedPrayers
{
	private final boolean known;
	private final int prayerLevel;
	private final int defenceLevel;
	private final boolean knightWaves;
	private final boolean rigour;
	private final boolean augury;
	private final boolean deadeye;
	private final boolean mysticVigour;

	private UnlockedPrayers(
		boolean known,
		int prayerLevel,
		int defenceLevel,
		boolean knightWaves,
		boolean rigour,
		boolean augury,
		boolean deadeye,
		boolean mysticVigour)
	{
		this.known = known;
		this.prayerLevel = prayerLevel;
		this.defenceLevel = defenceLevel;
		this.knightWaves = knightWaves;
		this.rigour = rigour;
		this.augury = augury;
		this.deadeye = deadeye;
		this.mysticVigour = mysticVigour;
	}

	public static UnlockedPrayers unknown()
	{
		return new UnlockedPrayers(false, 0, 0, false, false, false, false, false);
	}

	public static UnlockedPrayers known(
		int prayerLevel,
		int defenceLevel,
		boolean knightWaves,
		boolean rigour,
		boolean augury,
		boolean deadeye,
		boolean mysticVigour)
	{
		return new UnlockedPrayers(
			true,
			prayerLevel,
			defenceLevel,
			knightWaves,
			rigour,
			augury,
			deadeye,
			mysticVigour);
	}

	public boolean known()
	{
		return known;
	}

	public int prayerLevel()
	{
		return prayerLevel;
	}

	public int defenceLevel()
	{
		return defenceLevel;
	}

	public boolean knightWaves()
	{
		return knightWaves;
	}

	public boolean rigour()
	{
		return rigour;
	}

	public boolean augury()
	{
		return augury;
	}

	public boolean deadeye()
	{
		return deadeye;
	}

	public boolean mysticVigour()
	{
		return mysticVigour;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof UnlockedPrayers))
		{
			return false;
		}
		UnlockedPrayers other = (UnlockedPrayers) obj;
		return known == other.known
			&& prayerLevel == other.prayerLevel
			&& defenceLevel == other.defenceLevel
			&& knightWaves == other.knightWaves
			&& rigour == other.rigour
			&& augury == other.augury
			&& deadeye == other.deadeye
			&& mysticVigour == other.mysticVigour;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(
			known,
			prayerLevel,
			defenceLevel,
			knightWaves,
			rigour,
			augury,
			deadeye,
			mysticVigour);
	}
}
