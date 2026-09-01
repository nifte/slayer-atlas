package com.slayeratlas.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import org.junit.Test;

public class PoisonSuppliesTest
{
	@Test
	public void caveSlimesRequireAntipoison()
	{
		SlayerMonster slimes = new MonsterDatabase(new Gson()).findByTaskName("Cave slimes");
		assertTrue(PoisonSupplies.needsPotion(slimes));
	}

	@Test
	public void optionalSuperantipoisonIsNotRequired()
	{
		SlayerMonster crocodiles = new MonsterDatabase(new Gson()).findByTaskName("Crocodiles");
		assertFalse(PoisonSupplies.needsPotion(crocodiles));
	}

	@Test
	public void mentioningPoisonDamageIsNotACureRequirement()
	{
		SlayerMonster monster = new Gson().fromJson(
			"{\"name\":\"Test\",\"weakness\":\"They poison.\",\"notes\":\"Very poisonous.\"}",
			SlayerMonster.class);
		assertFalse(PoisonSupplies.needsPotion(monster));
	}
}
