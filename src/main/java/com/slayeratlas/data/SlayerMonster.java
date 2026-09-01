package com.slayeratlas.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
	private Integer combatLevelMin;
	private Integer combatLevelMax;
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
	private String image;
	private String dps;
	private Boolean assignment;

	static SlayerMonster forAlternative(String name)
	{
		return alternative(name, true);
	}

	static SlayerMonster catalogAlternative(String name)
	{
		return alternative(name, false);
	}

	private static SlayerMonster alternative(String name, boolean stub)
	{
		if (name == null || name.trim().isEmpty())
		{
			return null;
		}
		String title = name.trim();
		SlayerMonster monster = new SlayerMonster();
		monster.id = (stub ? "alternative-" : "") + AlternativeMonsters.slug(title);
		monster.name = title;
		monster.image = title + ".png";
		monster.wiki = wikiPage(title);
		monster.assignment = false;
		monster.normalize();
		return monster;
	}

	void copyAssignmentDetails(SlayerMonster parent)
	{
		if (parent == null)
		{
			return;
		}
		inheritTaskContext(parent);
		slayerLevel = parent.slayerLevel;
		combatRequirement = parent.combatRequirement;
		requiredItems = parent.requiredItems;
		recommendedEquipment = parent.recommendedEquipment;
		recommendedPotions = parent.recommendedPotions;
		masters = parent.masters;
		requirements = parent.requirements;
		attackStyle = parent.attackStyle;
		locationIds = parent.locationIds;
		recommendedLocationId = parent.recommendedLocationId;
		combatLevelMin = parent.combatLevelMin;
		combatLevelMax = parent.combatLevelMax;
		dps = parent.dps;
		notes = "Counts toward " + parent.name + " slayer tasks.";
	}

	void applyOverride(AlternativeOverride override)
	{
		if (override == null)
		{
			return;
		}
		if (override.getSlayerLevel() != null)
		{
			slayerLevel = override.getSlayerLevel();
		}
		if (override.getCombatLevelMin() != null)
		{
			combatLevelMin = override.getCombatLevelMin();
		}
		if (override.getCombatLevelMax() != null)
		{
			combatLevelMax = override.getCombatLevelMax();
		}
		attribute = firstNonBlank(override.getAttribute(), attribute);
		attackStyle = firstNonBlank(override.getAttackStyle(), attackStyle);
		weakness = firstNonBlank(override.getWeakness(), weakness);
		protectionPrayer = firstNonBlank(override.getProtectionPrayer(), protectionPrayer);
		recommendedStyle = firstNonBlank(override.getRecommendedStyle(), recommendedStyle);
		if (override.getRequiredItems() != null)
		{
			requiredItems = override.getRequiredItems();
		}
		if (override.getRecommendedPotions() != null)
		{
			recommendedPotions = override.getRecommendedPotions();
		}
		if (override.getRequirements() != null)
		{
			requirements = override.getRequirements();
		}
		if (override.getAliases() != null)
		{
			aliases = override.getAliases();
		}
		if (override.getLocationIds() != null)
		{
			locationIds = override.getLocationIds();
		}
		recommendedLocationId = firstNonBlank(override.getRecommendedLocationId(), recommendedLocationId);
		notes = firstNonBlank(override.getNotes(), notes);
		wiki = firstNonBlank(override.getWiki(), wiki);
		image = firstNonBlank(override.getImage(), image);
		dps = firstNonBlank(override.getDps(), dps);
	}

	void setAlternatives(List<String> values)
	{
		alternatives = values == null ? Collections.emptyList() : values;
	}

	public boolean isAssignment()
	{
		return assignment == null || assignment;
	}

	void inheritTaskContext(SlayerMonster parent)
	{
		if (parent == null)
		{
			return;
		}
		if (attribute == null || attribute.isEmpty())
		{
			attribute = parent.attribute;
		}
		if (recommendedStyle == null || recommendedStyle.isEmpty())
		{
			recommendedStyle = parent.recommendedStyle;
		}
		if (weakness == null || weakness.isEmpty())
		{
			weakness = parent.weakness;
		}
		if (recommendedPotions == null || recommendedPotions.isEmpty())
		{
			recommendedPotions = parent.recommendedPotions;
		}
		if (protectionPrayer == null || protectionPrayer.isEmpty())
		{
			protectionPrayer = parent.protectionPrayer;
		}
	}

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
		requiredItems = dropKaruulmBootsWhenNotInKaruulm(requiredItems, locationIds);
	}

	private static List<String> dropKaruulmBootsWhenNotInKaruulm(List<String> items, List<String> locations)
	{
		if (visitsKaruulm(locations))
		{
			return items;
		}
		List<String> kept = null;
		for (int index = 0; index < items.size(); index++)
		{
			String item = items.get(index);
			if (isKaruulmFloorProtection(item))
			{
				if (kept == null)
				{
					kept = new ArrayList<>(items.subList(0, index));
				}
			}
			else if (kept != null)
			{
				kept.add(item);
			}
		}
		return kept == null ? items : kept;
	}

	private static boolean visitsKaruulm(List<String> locations)
	{
		for (String id : locations)
		{
			if ("karuulm_slayer_dungeon".equals(id) || "mount_karuulm".equals(id) || "hydra_lair".equals(id))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isKaruulmFloorProtection(String item)
	{
		if (item == null)
		{
			return false;
		}
		String lower = item.toLowerCase();
		return lower.contains("boots of stone") || lower.contains("boots of brimstone");
	}

	private static String wikiPage(String title)
	{
		try
		{
			return new URI("https", "oldschool.runescape.wiki", "/w/" + title.replace(' ', '_'), null)
				.toASCIIString();
		}
		catch (URISyntaxException ex)
		{
			return "";
		}
	}

	private static List<String> emptyIfNull(List<String> values)
	{
		return values == null ? Collections.emptyList() : values;
	}

	private static String firstNonBlank(String preferred, String fallback)
	{
		return preferred == null || preferred.isEmpty() ? fallback : preferred;
	}
}
