package com.slayerguide.data;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class SlayerMonster
{
	private String id;
	private String name;
	private List<String> aliases;
	private int slayerLevel;
	private Integer combatRequirement;
	private String attribute;
	private String attackStyle;
	private String weakness;
	private String protectionPrayer;
	private String recommendedStyle;
	private List<String> requiredItems;
	private List<String> recommendedEquipment;
	private List<String> recommendedPotions;
	private List<String> alternatives;
	private List<String> masters;
	private List<String> requirements;
	private List<String> locationIds;
	private String recommendedLocationId;
	private String notes;
	private String wiki;

	void normalize()
	{
		aliases = emptyIfNull(aliases);
		requiredItems = emptyIfNull(requiredItems);
		recommendedEquipment = emptyIfNull(recommendedEquipment);
		recommendedPotions = emptyIfNull(recommendedPotions);
		alternatives = emptyIfNull(alternatives);
		masters = emptyIfNull(masters);
		requirements = emptyIfNull(requirements);
		locationIds = emptyIfNull(locationIds);
	}

	private static List<String> emptyIfNull(List<String> values)
	{
		return values == null ? Collections.emptyList() : values;
	}
}
