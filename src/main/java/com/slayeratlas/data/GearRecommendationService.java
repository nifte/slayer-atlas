package com.slayeratlas.data;

import com.slayeratlas.SlayerAtlasConfig;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GearRecommendationService
{
	private final SlayerAtlasConfig config;
	private OwnedItems owned = OwnedItems.none();
	private OwnedItems carried = OwnedItems.none();
	private UnlockedPrayers unlockedPrayers = UnlockedPrayers.unknown();
	private Runnable onChange;

	@Inject
	public GearRecommendationService(SlayerAtlasConfig config)
	{
		this.config = config;
	}

	public synchronized GearRecommendation recommendation()
	{
		boolean onlyOwned = config == null || config.onlyRecommendOwnedEquipment();
		boolean useGoading = config != null && config.useGoadingPotions();
		return GearRecommendation.of(onlyOwned, useGoading, owned);
	}

	public void setOnChange(Runnable onChange)
	{
		this.onChange = onChange;
	}

	public synchronized void setOwnedItems(OwnedItems owned)
	{
		OwnedItems next = owned == null ? OwnedItems.none() : owned;
		boolean changed = !next.equals(this.owned);
		this.owned = next;
		if (changed)
		{
			notifyChange();
		}
	}

	public synchronized OwnedItems owned()
	{
		return owned;
	}

	public synchronized void setCarriedItems(OwnedItems carried)
	{
		this.carried = carried == null ? OwnedItems.none() : carried;
	}

	public synchronized OwnedItems carried()
	{
		return carried;
	}

	public synchronized void setUnlockedPrayers(UnlockedPrayers unlockedPrayers)
	{
		this.unlockedPrayers = unlockedPrayers == null ? UnlockedPrayers.unknown() : unlockedPrayers;
	}

	public synchronized UnlockedPrayers unlockedPrayers()
	{
		return unlockedPrayers;
	}

	public synchronized boolean onlyUnlockedPrayers()
	{
		return config == null || config.onlyRecommendUnlockedPrayers();
	}

	private void notifyChange()
	{
		if (onChange != null)
		{
			onChange.run();
		}
	}
}
