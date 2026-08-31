package com.slayeratlas.data;

public final class GearRecommendation
{
	private final boolean onlyOwned;
	private final OwnedItems owned;

	private GearRecommendation(boolean onlyOwned, OwnedItems owned)
	{
		this.onlyOwned = onlyOwned;
		this.owned = owned == null ? OwnedItems.none() : owned;
	}

	public static GearRecommendation specialized()
	{
		return new GearRecommendation(false, OwnedItems.none());
	}

	public static GearRecommendation of(boolean onlyOwned, OwnedItems owned)
	{
		return new GearRecommendation(onlyOwned, owned);
	}

	public boolean onlyOwned()
	{
		return onlyOwned;
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
