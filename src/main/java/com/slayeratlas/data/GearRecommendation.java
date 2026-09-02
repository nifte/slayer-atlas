package com.slayeratlas.data;

public final class GearRecommendation
{
	private final boolean onlyOwned;
	private final boolean useGoadingPotions;
	private final OwnedItems owned;

	private GearRecommendation(boolean onlyOwned, boolean useGoadingPotions, OwnedItems owned)
	{
		this.onlyOwned = onlyOwned;
		this.useGoadingPotions = useGoadingPotions;
		this.owned = owned == null ? OwnedItems.none() : owned;
	}

	public static GearRecommendation specialized()
	{
		return new GearRecommendation(false, false, OwnedItems.none());
	}

	public static GearRecommendation of(boolean onlyOwned, OwnedItems owned)
	{
		return of(onlyOwned, false, owned);
	}

	public static GearRecommendation of(boolean onlyOwned, boolean useGoadingPotions, OwnedItems owned)
	{
		return new GearRecommendation(onlyOwned, useGoadingPotions, owned);
	}

	public boolean onlyOwned()
	{
		return onlyOwned;
	}

	public boolean useGoadingPotions()
	{
		return useGoadingPotions;
	}

	public OwnedItems owned()
	{
		return owned;
	}

	public boolean filterToOwned()
	{
		return onlyOwned && owned.hasBankSnapshot();
	}

	public boolean showBankHint()
	{
		return onlyOwned && !owned.hasBankSnapshot();
	}
}
