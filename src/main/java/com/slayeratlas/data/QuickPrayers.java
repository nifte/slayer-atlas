package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.VarbitID;

public final class QuickPrayers
{
	static final String EAGLE_EYE = "Eagle Eye";
	static final String DEADEYE = "Deadeye";
	static final String MYSTIC_MIGHT = "Mystic Might";
	static final String MYSTIC_VIGOUR = "Mystic Vigour";

	private static final String[] BOOK = {
		"Thick Skin",
		"Burst of Strength",
		"Clarity of Thought",
		"Sharp Eye",
		"Mystic Will",
		"Rock Skin",
		"Superhuman Strength",
		"Improved Reflexes",
		"Rapid Restore",
		"Rapid Heal",
		"Protect Item",
		"Hawk Eye",
		"Mystic Lore",
		"Steel Skin",
		"Ultimate Strength",
		"Incredible Reflexes",
		"Protect from Magic",
		"Protect from Missiles",
		"Protect from Melee",
		EAGLE_EYE,
		MYSTIC_MIGHT,
		"Retribution",
		"Redemption",
		"Smite",
		"Preserve",
		"Chivalry",
		"Piety",
		"Rigour",
		"Augury"
	};

	private QuickPrayers()
	{
	}

	public static List<String> fromClient(Client client)
	{
		if (client == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return List.of();
		}
		return fromBits(
			client.getVarbitValue(VarbitID.QUICKPRAYER_SELECTED),
			client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED) != 0,
			client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED) != 0);
	}

	public static List<String> fromBits(int bits, boolean deadeye, boolean mysticVigour)
	{
		List<String> prayers = new ArrayList<>();
		for (int bit = 0; bit < BOOK.length; bit++)
		{
			if ((bits & (1 << bit)) == 0)
			{
				continue;
			}
			prayers.add(nameAt(bit, deadeye, mysticVigour));
		}
		return prayers;
	}

	private static String nameAt(int bit, boolean deadeye, boolean mysticVigour)
	{
		String name = BOOK[bit];
		if (EAGLE_EYE.equals(name) && deadeye)
		{
			return DEADEYE;
		}
		if (MYSTIC_MIGHT.equals(name) && mysticVigour)
		{
			return MYSTIC_VIGOUR;
		}
		return name;
	}
}
