package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.slayerguide.data.SlayerMonster;
import org.junit.Test;

public class MonsterHeaderTextTest
{
	@Test
	public void includesSlayerCombatAndType()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"slayerLevel\":72,\"combatRequirement\":140,\"attribute\":\"Dragon\"}",
			SlayerMonster.class);
		assertEquals("Slayer 72 · Combat 140 · Dragon", MonsterHeaderText.meta(monster));
	}

	@Test
	public void omitsMissingCombatAndType()
	{
		SlayerMonster monster = new Gson().fromJson("{\"slayerLevel\":1}", SlayerMonster.class);
		assertEquals("Slayer 1", MonsterHeaderText.meta(monster));
	}
}
