package com.slayeratlas.data;

import java.util.List;
import java.util.Objects;

public final class RecommendedLoadouts
{
	private RecommendedLoadouts()
	{
	}

	public static boolean same(SlayerMonster left, SlayerMonster right)
	{
		if (left == null || right == null)
		{
			return left == right;
		}
		return sameText(left.getRecommendedStyle(), right.getRecommendedStyle())
			&& sameText(left.getAttribute(), right.getAttribute())
			&& sameList(left.getRequiredItems(), right.getRequiredItems())
			&& sameList(left.getRecommendedEquipment(), right.getRecommendedEquipment())
			&& sameList(left.getRecommendedPotions(), right.getRecommendedPotions())
			&& InventoryRations.fullyNegated(left) == InventoryRations.fullyNegated(right);
	}

	private static boolean sameText(String left, String right)
	{
		return Objects.equals(blankToEmpty(left), blankToEmpty(right));
	}

	private static boolean sameList(List<String> left, List<String> right)
	{
		List<String> first = left == null ? List.of() : left;
		List<String> second = right == null ? List.of() : right;
		return first.equals(second);
	}

	private static String blankToEmpty(String value)
	{
		return value == null ? "" : value.trim();
	}
}
