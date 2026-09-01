package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearItem;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.GearLoadouts;
import com.slayeratlas.data.GearRecommendation;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.LoadoutSelection;
import com.slayeratlas.data.RankedGearLoadout;
import com.slayeratlas.data.SlayerMonster;
import com.slayeratlas.data.TaskLoadouts;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.JButton;

public class GearSection extends ViewportWidthPanel
{
	private final SlayerMonster monster;
	private final MonsterImageLoader images;
	private final WikiInventoryClient inventory;
	private final GearRecommendationService recommendations;
	private final TaskLoadouts taskLoadouts;
	private final LoadoutSelection loadoutSelection;
	private final BiConsumer<CombatStyle, List<String>> onPrayers;
	private List<RankedGearLoadout> ranked = List.of();
	private List<GearItem> sharedInventory = List.of();
	private List<GearLoadout> loadouts;
	private GearTab selected;

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
		this(monster, images, wiki, inventory, recommendations, wrap(onStyleChange), TaskLoadouts.none());
	}

	public GearSection(
		SlayerMonster monster,
		MonsterImageLoader images,
		WikiLoadoutClient wiki,
		WikiInventoryClient inventory,
		GearRecommendationService recommendations,
		BiConsumer<CombatStyle, List<String>> onPrayers,
		TaskLoadouts taskLoadouts)
	{
		this(monster, images, wiki, inventory, recommendations, onPrayers, taskLoadouts, LoadoutSelection.none());
	}

	public GearSection(
		SlayerMonster monster,
		MonsterImageLoader images,
		WikiLoadoutClient wiki,
		WikiInventoryClient inventory,
		GearRecommendationService recommendations,
		BiConsumer<CombatStyle, List<String>> onPrayers,
		TaskLoadouts taskLoadouts,
		LoadoutSelection loadoutSelection)
	{
		setName("gear-section");
		add(PanelWidgets.sectionHeading("Recommended Gear"));
		this.monster = monster;
		this.images = images;
		this.inventory = inventory == null ? WikiInventoryClient.none() : inventory;
		this.recommendations = recommendations;
		this.taskLoadouts = taskLoadouts == null ? TaskLoadouts.none() : taskLoadouts;
		this.loadoutSelection = loadoutSelection == null ? LoadoutSelection.none() : loadoutSelection;
		this.onPrayers = onPrayers == null ? (style, prayers) ->
		{
		} : onPrayers;
		this.loadouts = GearLoadouts.forMonster(monster, List.of(), recommendation());
		this.selected = restoreOrInitial();
		publishPrayers();
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
		if (!selected.isSaved() && !containsStyle(selected.style()))
		{
			selected = GearTab.style(loadouts.get(0).getStyle());
			publishPrayers();
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

	private void select(GearTab tab)
	{
		if (tab == null || tab.equals(selected))
		{
			return;
		}
		selected = tab;
		publishPrayers();
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
		GearLoadout saved = savedLoadout();
		if (selected.isSaved() && saved == null)
		{
			selected = GearTab.style(loadouts.get(0).getStyle());
			publishPrayers();
		}
		boolean showingSaved = selected.isSaved() && saved != null;
		GearLoadout loadout = showingSaved ? saved : selectedLoadout();
		if (recommendation().showBankHint() && !showingSaved)
		{
			add(new BankHintPanel());
			add(Box.createVerticalStrut(6));
		}
		add(new StyleTabs(GearTab.of(loadouts, saved != null), selected, this::select));
		add(Box.createVerticalStrut(6));
		add(new EquipmentPanel(loadout, images));
		add(Box.createVerticalStrut(6));
		add(new InventoryPanel(loadout, images, showingSaved));
		add(Box.createVerticalStrut(6));
		add(loadoutButton(saved != null));
		publishLoadout();
	}

	private JButton loadoutButton(boolean saved)
	{
		JButton button = PanelWidgets.button(
			saved ? PanelCopy.CLEAR_SAVED_LOADOUT : PanelCopy.SAVE_CURRENT_LOADOUT);
		button.setName(saved ? "clear-saved-loadout" : "save-current-loadout");
		button.setToolTipText(saved
			? "Remove your saved loadout and show recommendations again"
			: "Save the gear and inventory you are using for this task");
		button.addActionListener(event ->
		{
			if (saved)
			{
				clearSaved();
			}
			else
			{
				saveCurrent();
			}
		});
		return button;
	}

	private void saveCurrent()
	{
		taskLoadouts.captureCurrent(prayerStyle(), captured ->
		{
			if (captured == null)
			{
				return;
			}
			taskLoadouts.save(monster.getId(), captured);
			selected = GearTab.saved();
			publishPrayers();
			refreshAfterLoadoutChange();
		});
	}

	private void clearSaved()
	{
		boolean wasSaved = selected.isSaved();
		taskLoadouts.clear(monster.getId());
		if (wasSaved)
		{
			selected = GearTab.style(loadouts.get(0).getStyle());
		}
		publishPrayers();
		refreshAfterLoadoutChange();
	}

	private void refreshAfterLoadoutChange()
	{
		rebuild();
		revalidate();
		repaint();
	}

	private GearLoadout savedLoadout()
	{
		return monster == null ? null : taskLoadouts.load(monster.getId());
	}

	private GearLoadout selectedLoadout()
	{
		CombatStyle style = selected.style();
		for (GearLoadout loadout : loadouts)
		{
			if (loadout.getStyle() == style)
			{
				return loadout;
			}
		}
		return loadouts.get(0);
	}

	private GearTab restoreOrInitial()
	{
		String monsterId = monster == null ? null : monster.getId();
		GearLoadout saved = savedLoadout();
		if (loadoutSelection.saved(monsterId) && saved != null)
		{
			return GearTab.saved();
		}
		CombatStyle style = loadoutSelection.style(monsterId);
		if (style != null && containsStyle(style))
		{
			return GearTab.style(style);
		}
		return GearTab.initial(saved != null, loadouts);
	}

	private void publishLoadout()
	{
		if (monster == null)
		{
			return;
		}
		boolean showingSaved = selected.isSaved() && savedLoadout() != null;
		loadoutSelection.set(
			monster.getId(),
			selected.style(),
			selected.isSaved(),
			showingSaved ? savedLoadout() : selectedLoadout());
	}

	private CombatStyle prayerStyle()
	{
		if (selected.isSaved())
		{
			GearLoadout saved = savedLoadout();
			if (saved != null && saved.getStyle() != null)
			{
				return saved.getStyle();
			}
		}
		return selected.style() == null ? loadouts.get(0).getStyle() : selected.style();
	}

	private void publishPrayers()
	{
		onPrayers.accept(prayerStyle(), List.of());
	}

	private static BiConsumer<CombatStyle, List<String>> wrap(Consumer<CombatStyle> onStyleChange)
	{
		if (onStyleChange == null)
		{
			return (style, prayers) ->
			{
			};
		}
		return (style, prayers) -> onStyleChange.accept(style);
	}
}
