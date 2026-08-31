package com.slayeratlas;

import com.slayeratlas.data.UnlockedPrayers;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarbitID;

@Singleton
public class UnlockedPrayersTracker
{
	static final int KNIGHT_WAVES_COMPLETE = 8;

	private final Client client;

	@Inject
	public UnlockedPrayersTracker(Client client)
	{
		this.client = client;
	}

	public UnlockedPrayers snapshot()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return UnlockedPrayers.unknown();
		}
		int prayer = client.getRealSkillLevel(Skill.PRAYER);
		if (prayer <= 0)
		{
			return UnlockedPrayers.unknown();
		}
		return UnlockedPrayers.known(
			prayer,
			client.getRealSkillLevel(Skill.DEFENCE),
			client.getVarbitValue(VarbitID.KR_KNIGHTWAVES_STATE) == KNIGHT_WAVES_COMPLETE,
			unlocked(VarbitID.PRAYER_RIGOUR_UNLOCKED),
			unlocked(VarbitID.PRAYER_AUGURY_UNLOCKED),
			unlocked(VarbitID.PRAYER_DEADEYE_UNLOCKED),
			unlocked(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED));
	}

	static boolean tracksSkill(Skill skill)
	{
		return skill == Skill.PRAYER || skill == Skill.DEFENCE;
	}

	static boolean tracksVarbit(int varbitId)
	{
		return varbitId == VarbitID.PRAYER_RIGOUR_UNLOCKED
			|| varbitId == VarbitID.PRAYER_AUGURY_UNLOCKED
			|| varbitId == VarbitID.PRAYER_DEADEYE_UNLOCKED
			|| varbitId == VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED
			|| varbitId == VarbitID.KR_KNIGHTWAVES_STATE;
	}

	private boolean unlocked(int varbitId)
	{
		return client.getVarbitValue(varbitId) != 0;
	}
}
