package com.slayeratlas.ui;

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
	MAGIC("Protect from Magic", SpriteID.Prayeron.PROTECT_FROM_MAGIC),
	MISSILES("Protect from Missiles", SpriteID.Prayeron.PROTECT_FROM_MISSILES),
	MELEE("Protect from Melee", SpriteID.Prayeron.PROTECT_FROM_MELEE);

	private final String displayName;
	private final int spriteId;

	ProtectionPrayer(String displayName, int spriteId)
	{
		this.displayName = displayName;
		this.spriteId = spriteId;
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
