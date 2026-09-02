package com.slayeratlas.data;

import java.util.List;

public final class OwnedSupplies
{
	public static final List<GearItem> FOOD = List.of(
		GearItem.named(InventoryLoadouts.FOOD),
		GearItem.named(InventoryLoadouts.COMBO_FOOD),
		GearItem.named("Cooked sunlight antelope"),
		GearItem.named("Manta ray"),
		GearItem.named("Dark crab"),
		GearItem.named("Anglerfish"),
		GearItem.named("Tuna potato"),
		GearItem.named("Shark"),
		GearItem.named("Monkfish"),
		GearItem.named("Swordfish"),
		GearItem.named("Lobster"),
		GearItem.named("Salmon"),
		GearItem.named("Tuna"));

	public static final List<GearItem> PRAYER = List.of(
		GearItem.named("Prayer potion"),
		GearItem.named("Super restore"),
		GearItem.named("Sanfew serum"));

	public static final List<GearItem> MELEE_BOOST = List.of(
		GearItem.named("Divine super combat potion"),
		GearItem.named("Super combat potion"),
		GearItem.named("Divine super attack potion"),
		GearItem.named("Super attack potion"));

	public static final List<GearItem> RANGED_BOOST = List.of(
		GearItem.named("Divine bastion potion"),
		GearItem.named("Bastion potion"),
		GearItem.named("Divine ranging potion"),
		GearItem.named("Ranging potion"));

	public static final List<GearItem> ANTIFIRE = List.of(
		GearItem.named("Extended super antifire"),
		GearItem.named("Super antifire potion"),
		GearItem.named("Extended antifire"),
		GearItem.named("Antifire potion"));

	public static final List<GearItem> ANTIPOISON = List.of(
		GearItem.named("Anti-venom+"),
		GearItem.named("Anti-venom"),
		GearItem.named("Antidote++"),
		GearItem.named("Superantipoison"),
		GearItem.named("Antipoison"));

	private OwnedSupplies()
	{
	}

	public static GearItem pick(List<GearItem> ranks, GearRecommendation recommendation)
	{
		if (ranks == null || ranks.isEmpty())
		{
			return null;
		}
		boolean onlyOwned = recommendation != null && recommendation.filterToOwned();
		OwnedItems owned = recommendation == null ? OwnedItems.none() : recommendation.owned();
		return OwnedGearPicker.pick(ranks, owned, onlyOwned);
	}
}
