package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WeaponStylesTest
{
	@Test
	public void treatsUnarmedAndMeleeBonusesAsMelee()
	{
		assertEquals(CombatStyle.MELEE, WeaponStyles.of(null, null));
		assertEquals(CombatStyle.MELEE, WeaponStyles.of(82, 0, 0, 0, 0));
		assertEquals(CombatStyle.MELEE, WeaponStyles.of(0, 82, 0, 20, 0));
	}

	@Test
	public void treatsRangedAttackAsRanged()
	{
		assertEquals(CombatStyle.RANGED, WeaponStyles.of(0, 0, 0, 0, 70));
		assertEquals(CombatStyle.RANGED, WeaponStyles.of(0, 0, 0, 15, 70));
	}

	@Test
	public void treatsMagicAttackAsMagic()
	{
		assertEquals(CombatStyle.MAGIC, WeaponStyles.of(0, 0, 0, 25, 0));
		assertEquals(CombatStyle.MAGIC, WeaponStyles.of(20, 0, 0, 25, 0));
	}
}
