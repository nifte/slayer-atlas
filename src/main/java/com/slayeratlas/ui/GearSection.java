package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.GearLoadouts;
import com.slayeratlas.data.GearRecommendation;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.RankedGearLoadout;
import com.slayeratlas.data.SlayerMonster;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Box;

public class GearSection extends ViewportWidthPanel
{
	private final SlayerMonster monster;
	private final MonsterImageLoader images;
	private final WikiInventoryClient inventory;
	private final GearRecommendationService recommendations;
	private final Consumer<CombatStyle> onStyleChange;
	private List<RankedGearLoadout> ranked = List.of();
	private List<GearItem> sharedInventory = List.of();
	private List<GearLoadout> loadouts;
	private CombatStyle selected;

	public GearSection(SlayerMonster monster, MonsterImageLoader images, WikiLoadoutClient wiki)
	{
		this(monster, images, wiki, WikiInventoryClient.none(), null, null);
	}

	public GearSection(
		SlayerMonster monster,
		MonsterImageLoader images,
		WikiLoadoutClient wiki,
		Consumer<CombatStyle> onStyleChange)
	{
		this(monster, images, wiki, WikiInventoryClient.none(), null, onStyleChange);
	}

	public GearSection(
		SlayerMonster monster,
		MonsterImageLoader images,
		WikiLoadoutClient wiki,
		WikiInventoryClient inventory,
		GearRecommendationService recommendations,
		Consumer<CombatStyle> onStyleChange)
	{
		setName("gear-section");
		add(PanelWidgets.sectionHeading("Recommended Gear"));
		this.monster = monster;
		this.images = images;
		this.inventory = inventory == null ? WikiInventoryClient.none() : inventory;
		this.recommendations = recommendations;
		this.onStyleChange = onStyleChange == null ? ignored ->
		{
		} : onStyleChange;
		this.loadouts = GearLoadouts.forMonster(monster, List.of(), recommendation());
		this.selected = loadouts.get(0).getStyle();
		this.onStyleChange.accept(selected);
		rebuild();
		if (wiki != null)
		{
			wiki.loadRanked(monster, this::applyWiki);
		}
	}

	public void refreshRecommendations()
	{
		apply(ranked);
	}

	private void applyWiki(List<RankedGearLoadout> wikiRanked)
	{
		WikiInventoryPages.load(inventory, monster, wikiRanked, this::applyLoaded);
	}

	private void apply(List<RankedGearLoadout> wikiRanked)
	{
		apply(wikiRanked, sharedInventory);
	}

	private void applyLoaded(List<RankedGearLoadout> wikiRanked, List<GearItem> extraInventory)
	{
		apply(wikiRanked, extraInventory);
	}

	private void apply(List<RankedGearLoadout> wikiRanked, List<GearItem> extraInventory)
	{
		sharedInventory = extraInventory == null ? List.of() : extraInventory;
		List<GearLoadout> merged = GearLoadouts.forMonster(monster, wikiRanked, recommendation(), sharedInventory);
		if (merged.isEmpty())
		{
			return;
		}
		ranked = wikiRanked == null ? List.of() : wikiRanked;
		loadouts = merged;
		if (!containsStyle(selected))
		{
			selected = loadouts.get(0).getStyle();
			onStyleChange.accept(selected);
		}
		rebuild();
		revalidate();
		repaint();
	}

	private GearRecommendation recommendation()
	{
		return recommendations == null ? GearRecommendation.specialized() : recommendations.recommendation();
	}

	private boolean containsStyle(CombatStyle style)
	{
		for (GearLoadout loadout : loadouts)
		{
			if (loadout.getStyle() == style)
			{
				return true;
			}
		}
		return false;
	}

	private void select(CombatStyle style)
	{
		if (style == selected)
		{
			return;
		}
		selected = style;
		onStyleChange.accept(selected);
		rebuild();
		revalidate();
		repaint();
	}

	private void rebuild()
	{
		while (getComponentCount() > 1)
		{
			remove(getComponentCount() - 1);
		}
		GearLoadout loadout = selectedLoadout();
		if (recommendation().showBankHint())
		{
			add(new BankHintPanel());
			add(Box.createVerticalStrut(6));
		}
		add(new StyleTabs(loadouts, selected, this::select));
		add(Box.createVerticalStrut(6));
		add(new EquipmentPanel(loadout, images));
		add(Box.createVerticalStrut(6));
		add(new InventoryPanel(loadout, images));
	}

	private GearLoadout selectedLoadout()
	{
		for (GearLoadout loadout : loadouts)
		{
			if (loadout.getStyle() == selected)
			{
				return loadout;
			}
		}
		return loadouts.get(0);
	}
}
