package com.slayeratlas.ui;

import com.slayeratlas.data.UnlockedPrayers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import lombok.Getter;
import net.runelite.api.gameval.SpriteID;

@Getter
public enum ProtectionPrayer
{
	MAGIC("Protect from Magic", SpriteID.Prayeron.PROTECT_FROM_MAGIC, 37),
	MISSILES("Protect from Missiles", SpriteID.Prayeron.PROTECT_FROM_MISSILES, 40),
	MELEE("Protect from Melee", SpriteID.Prayeron.PROTECT_FROM_MELEE, 43);

	private final String displayName;
	private final int spriteId;
	private final int prayerLevel;

	ProtectionPrayer(String displayName, int spriteId, int prayerLevel)
	{
		this.displayName = displayName;
		this.spriteId = spriteId;
		this.prayerLevel = prayerLevel;
	}

	public static List<ProtectionPrayer> recommended(
		List<ProtectionPrayer> prayers,
		boolean onlyUnlocked,
		UnlockedPrayers unlocks)
	{
		if (prayers == null || prayers.isEmpty())
		{
			return Collections.emptyList();
		}
		if (!onlyUnlocked || unlocks == null || !unlocks.known())
		{
			return prayers;
		}
		List<ProtectionPrayer> unlocked = new ArrayList<>();
		for (ProtectionPrayer prayer : prayers)
		{
			if (unlocks.prayerLevel() >= prayer.prayerLevel)
			{
				unlocked.add(prayer);
			}
		}
		return unlocked;
	}

	public static List<ProtectionPrayer> parse(String protectionPrayer)
	{
		if (protectionPrayer == null || protectionPrayer.isEmpty())
		{
			return Collections.emptyList();
		}
		String lower = protectionPrayer.toLowerCase(Locale.ROOT);
		TreeMap<Integer, ProtectionPrayer> found = new TreeMap<>();
		putIfMentioned(found, lower, "magic", MAGIC);
		putIfMentioned(found, lower, "missile", MISSILES);
		putIfMentioned(found, lower, "melee", MELEE);
		return new ArrayList<>(found.values());
	}

	private static void putIfMentioned(
		TreeMap<Integer, ProtectionPrayer> found,
		String lower,
		String needle,
		ProtectionPrayer prayer)
	{
		int index = lower.indexOf(needle);
		if (index >= 0)
		{
			found.put(index, prayer);
		}
	}
}
