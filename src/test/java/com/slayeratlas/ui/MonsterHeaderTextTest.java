package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayeratlas.data.SlayerMonster;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class MonsterHeaderTextTest
{
	@Test
	public void formatsSlayerCombatRangeAndType()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"slayerLevel\":1,\"combatLevelMin\":72,\"combatLevelMax\":96,\"attribute\":\"Undead\"}",
			SlayerMonster.class);
		assertEquals(
			Arrays.asList("Slayer level: 1", "Combat level: 72-96", "Type: Undead"),
			MonsterHeaderText.lines(monster));
	}

	@Test
	public void formatsSingleCombatLevelAndType()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"slayerLevel\":72,\"combatLevelMin\":140,\"combatLevelMax\":140,\"attribute\":\"Dragon\"}",
			SlayerMonster.class);
		assertEquals(
			Arrays.asList("Slayer level: 72", "Combat level: 140", "Type: Dragon"),
			MonsterHeaderText.lines(monster));
	}

	@Test
	public void omitsTypeWhenMissing()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"slayerLevel\":1,\"combatLevelMin\":124,\"combatLevelMax\":124}",
			SlayerMonster.class);
		assertEquals(
			Arrays.asList("Slayer level: 1", "Combat level: 124"),
			MonsterHeaderText.lines(monster));
	}

	@Test
	public void typeOnlyWhenCombatLevelsMissing()
	{
		SlayerMonster monster = new Gson().fromJson("{\"attribute\":\"Demon\"}", SlayerMonster.class);
		assertEquals(Collections.singletonList("Type: Demon"), MonsterHeaderText.lines(monster));
	}

	@Test
	public void emptyWhenNothingToShow()
	{
		SlayerMonster monster = new Gson().fromJson("{}", SlayerMonster.class);
		assertEquals(Collections.emptyList(), MonsterHeaderText.lines(monster));
	}

	@Test
	public void slayerLevelOnly()
	{
		SlayerMonster monster = new Gson().fromJson("{\"slayerLevel\":72}", SlayerMonster.class);
		assertEquals(Collections.singletonList("Slayer level: 72"), MonsterHeaderText.lines(monster));
	}
}
