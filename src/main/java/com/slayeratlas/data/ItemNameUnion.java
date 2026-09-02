package com.slayeratlas.data;

import java.util.HashSet;
import java.util.Set;

public final class ItemNameUnion
{
	private ItemNameUnion()
	{
	}

	public static boolean same(Set<String> leftFirst, Set<String> leftSecond, Set<String> rightFirst, Set<String> rightSecond)
	{
		Set<String> leftA = leftFirst == null ? Set.of() : leftFirst;
		Set<String> leftB = leftSecond == null ? Set.of() : leftSecond;
		Set<String> rightA = rightFirst == null ? Set.of() : rightFirst;
		Set<String> rightB = rightSecond == null ? Set.of() : rightSecond;
		if (leftA.equals(rightA) && leftB.equals(rightB))
		{
			return true;
		}
		Set<String> left = new HashSet<>(leftA);
		left.addAll(leftB);
		Set<String> right = new HashSet<>(rightA);
		right.addAll(rightB);
		return left.equals(right);
	}
}
