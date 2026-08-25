package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.GearLoadouts;
import com.slayeratlas.data.SlayerMonster;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Box;

public class GearSection extends ViewportWidthPanel
{
	private final SlayerMonster monster;
	private final MonsterImageLoader images;
	private final Consumer<CombatStyle> onStyleChange;
	private List<GearLoadout> loadouts;
	private CombatStyle selected;

	public GearSection(SlayerMonster monster, MonsterImageLoader images, WikiLoadoutClient wiki)
	{
		this(monster, images, wiki, null);
	}

	public GearSection(
		SlayerMonster monster,
		MonsterImageLoader images,
		WikiLoadoutClient wiki,
		Consumer<CombatStyle> onStyleChange)
	{
		setName("gear-section");
		add(PanelWidgets.sectionHeading("Recommended Gear"));
		this.monster = monster;
		this.images = images;
		this.onStyleChange = onStyleChange == null ? ignored ->
		{
		} : onStyleChange;
		this.loadouts = GearLoadouts.forMonster(monster, List.of());
		this.selected = loadouts.get(0).getStyle();
		this.onStyleChange.accept(selected);
		rebuild();
		if (wiki != null)
		{
			wiki.load(monster, this::applyWiki);
		}
	}

	private void applyWiki(List<GearLoadout> wikiLoadouts)
	{
		List<GearLoadout> merged = GearLoadouts.forMonster(monster, wikiLoadouts);
		if (merged.isEmpty())
		{
			return;
		}
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
