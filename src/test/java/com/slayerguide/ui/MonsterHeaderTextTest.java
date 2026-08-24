package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayerguide.data.SlayerMonster;
import org.junit.Test;

public class MonsterHeaderTextTest
{
	@Test
	public void formatsCombatRangeAndLowercaseType()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"combatLevelMin\":72,\"combatLevelMax\":96,\"attribute\":\"Undead\"}",
			SlayerMonster.class);
		assertEquals("lvl 72-96 undead", MonsterHeaderText.meta(monster));
	}

	@Test
	public void formatsSingleCombatLevelAndType()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"combatLevelMin\":140,\"combatLevelMax\":140,\"attribute\":\"Dragon\"}",
			SlayerMonster.class);
		assertEquals("lvl 140 dragon", MonsterHeaderText.meta(monster));
	}

	@Test
	public void omitsTypeWhenMissing()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"combatLevelMin\":124,\"combatLevelMax\":124}",
			SlayerMonster.class);
		assertEquals("lvl 124", MonsterHeaderText.meta(monster));
	}

	@Test
	public void typeOnlyWhenCombatLevelsMissing()
	{
		SlayerMonster monster = new Gson().fromJson("{\"attribute\":\"Demon\"}", SlayerMonster.class);
		assertEquals("demon", MonsterHeaderText.meta(monster));
	}

	@Test
	public void emptyWhenNothingToShow()
	{
		SlayerMonster monster = new Gson().fromJson("{\"slayerLevel\":72}", SlayerMonster.class);
		assertEquals("", MonsterHeaderText.meta(monster));
	}

	@Test
	public void doesNotIncludeSlayerLevel()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"slayerLevel\":72,\"combatLevelMin\":140,\"combatLevelMax\":140,\"attribute\":\"Dragon\"}",
			SlayerMonster.class);
		assertEquals("lvl 140 dragon", MonsterHeaderText.meta(monster));
	}
}
