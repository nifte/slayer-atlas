package com.slayeratlas.data;

import java.util.List;
import lombok.Getter;

@Getter
public class AlternativeOverride
{
	private Integer slayerLevel;
	private Integer combatLevelMin;
	private Integer combatLevelMax;
	private String attribute;
	private String attackStyle;
	private String weakness;
	private String protectionPrayer;
	private String recommendedStyle;
	private List<String> requiredItems;
	private List<String> recommendedPotions;
	private List<String> requirements;
	private List<String> aliases;
	private List<String> locationIds;
	private String recommendedLocationId;
	private String notes;
	private String wiki;
	private String image;
	private String dps;
}
