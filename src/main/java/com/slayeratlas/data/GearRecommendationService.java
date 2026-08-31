package com.slayeratlas.data;

import com.slayeratlas.SlayerAtlasConfig;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GearRecommendationService
{
	private final SlayerAtlasConfig config;
	private OwnedItems owned = OwnedItems.none();
	private UnlockedPrayers unlockedPrayers = UnlockedPrayers.unknown();

	@Inject
	public GearRecommendationService(SlayerAtlasConfig config)
	{
		this.config = config;
	}

	public synchronized GearRecommendation recommendation()
	{
		boolean onlyOwned = config == null || config.onlyRecommendOwnedEquipment();
		return GearRecommendation.of(onlyOwned, owned);
	}

	public synchronized void setOwnedItems(OwnedItems owned)
	{
		this.owned = owned == null ? OwnedItems.none() : owned;
	}

	public synchronized OwnedItems owned()
	{
		return owned;
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
}
