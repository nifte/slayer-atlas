package com.slayeratlas.data;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.VarbitID;

public final class QuickPrayers
{
	static final String EAGLE_EYE = "Eagle Eye";
	static final String DEADEYE = "Deadeye";
	static final String MYSTIC_MIGHT = "Mystic Might";
	static final String MYSTIC_VIGOUR = "Mystic Vigour";

	// Fallback when the cache enum is unavailable. Bits are prayer ids (enum 860 /
	// PRAYERS_NORMAL keys), not the on-screen grid — hide/reorder only moves widgets.
	private static final String[] BOOK = {
		"Thick Skin",
		"Burst of Strength",
		"Clarity of Thought",
		"Rock Skin",
		"Superhuman Strength",
		"Improved Reflexes",
		"Rapid Restore",
		"Rapid Heal",
		"Protect Item",
		"Steel Skin",
		"Ultimate Strength",
		"Incredible Reflexes",
		"Protect from Magic",
		"Protect from Missiles",
		"Protect from Melee",
		"Retribution",
		"Redemption",
		"Smite",
		"Sharp Eye",
		"Mystic Will",
		"Hawk Eye",
		"Mystic Lore",
		EAGLE_EYE,
		MYSTIC_MIGHT,
		"Chivalry",
		"Piety",
		"Rigour",
		"Augury",
		"Preserve"
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
		int bits = client.getVarbitValue(VarbitID.QUICKPRAYER_SELECTED);
		List<String> fromEnum = fromBookEnum(bits, client);
		if (fromEnum != null)
		{
			return fromEnum;
		}
		return fromBits(
			bits,
			client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED) != 0,
			client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED) != 0);
	}

	public static List<String> fromBits(int bits, boolean deadeye, boolean mysticVigour)
	{
		int[] ids = new int[BOOK.length];
		String[] names = new String[BOOK.length];
		for (int id = 0; id < BOOK.length; id++)
		{
			ids[id] = id;
			names[id] = nameAt(id, deadeye, mysticVigour);
		}
		return fromIds(bits, ids, names);
	}

	static List<String> fromIds(int bits, int[] ids, String[] names)
	{
		if (ids == null || names == null)
		{
			return List.of();
		}
		String[] byId = new String[32];
		int limit = Math.min(ids.length, names.length);
		for (int i = 0; i < limit; i++)
		{
			int id = ids[i];
			if (id < 0 || id >= byId.length || names[i] == null || names[i].isEmpty())
			{
				continue;
			}
			byId[id] = names[i];
		}
		List<String> prayers = new ArrayList<>();
		for (int id = 0; id < byId.length; id++)
		{
			if ((bits & (1 << id)) == 0 || byId[id] == null)
			{
				continue;
			}
			prayers.add(byId[id]);
		}
		return prayers;
	}

	private static List<String> fromBookEnum(int bits, Client client)
	{
		EnumComposition prayers = bookEnum(client);
		if (prayers == null)
		{
			return null;
		}
		int[] ids = prayers.getKeys();
		if (ids == null || ids.length == 0)
		{
			return null;
		}
		String[] names = new String[ids.length];
		for (int i = 0; i < ids.length; i++)
		{
			names[i] = nameOf(client, prayers, ids[i]);
		}
		List<String> decoded = fromIds(bits, ids, names);
		if (decoded.isEmpty() && bits != 0)
		{
			return null;
		}
		return decoded;
	}

	private static EnumComposition bookEnum(Client client)
	{
		try
		{
			if (client.getVarbitValue(VarbitID.PRAYERBOOK) == 1)
			{
				return client.getEnum(EnumID.PRAYERS_RUINOUS);
			}
			boolean deadeye = client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED) != 0;
			boolean vigour = client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED) != 0;
			if (deadeye && vigour)
			{
				return client.getEnum(EnumID.PRAYERS_NORMAL_DEADEYE_MYSTIC_VIGOUR);
			}
			if (deadeye)
			{
				return client.getEnum(EnumID.PRAYERS_NORMAL_DEADEYE);
			}
			if (vigour)
			{
				return client.getEnum(EnumID.PRAYERS_NORMAL_MYSTIC_VIGOUR);
			}
			return client.getEnum(EnumID.PRAYERS_NORMAL);
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
	}

	private static String nameOf(Client client, EnumComposition prayers, int id)
	{
		try
		{
			ItemComposition item = client.getItemDefinition(prayers.getIntValue(id));
			if (item == null)
			{
				return null;
			}
			String name = item.getName();
			if (name == null || name.isEmpty() || name.equals("null"))
			{
				return null;
			}
			return name;
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
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
