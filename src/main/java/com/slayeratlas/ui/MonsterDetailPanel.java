package com.slayeratlas.ui;

import com.slayeratlas.data.AlternativeMonsters;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.MonsterDatabase;
import com.slayeratlas.data.MonsterLocation;
import com.slayeratlas.data.SkillRequirement;
import com.slayeratlas.data.SlayerMonster;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.SpriteManager;

public class MonsterDetailPanel extends ViewportWidthPanel
{
	private final GearSection gear;
	private final PraySection prayers;

	public interface Actions
	{
		void pathTo(MonsterLocation location);

		void showOnMap(MonsterLocation location);

		void openWiki(SlayerMonster monster);

		void openDps(SlayerMonster monster);

		void openMonster(SlayerMonster monster);

		boolean canPath();
	}

	public MonsterDetailPanel(
		SlayerMonster monster,
		List<MonsterLocation> locations,
		Actions actions)
	{
		this(monster, locations, actions, (MonsterDatabase) null);
	}

	public MonsterDetailPanel(
		SlayerMonster monster,
		List<MonsterLocation> locations,
		Actions actions,
		MonsterDatabase database)
	{
		this(monster, locations, actions, null, MonsterImageLoader.none(), WikiLoadoutClient.none(), database);
	}

	public MonsterDetailPanel(
		SlayerMonster monster,
		List<MonsterLocation> locations,
		Actions actions,
		SpriteManager sprites)
	{
		this(monster, locations, actions, sprites, MonsterImageLoader.none(), WikiLoadoutClient.none(), null);
	}

	public MonsterDetailPanel(
		SlayerMonster monster,
		List<MonsterLocation> locations,
		Actions actions,
		SpriteManager sprites,
		MonsterImageLoader images,
		WikiLoadoutClient wiki)
	{
		this(monster, locations, actions, sprites, images, wiki, WikiInventoryClient.none(), null, null);
	}

	public MonsterDetailPanel(
		SlayerMonster monster,
		List<MonsterLocation> locations,
		Actions actions,
		SpriteManager sprites,
		MonsterImageLoader images,
		WikiLoadoutClient wiki,
		MonsterDatabase database)
	{
		this(monster, locations, actions, sprites, images, wiki, WikiInventoryClient.none(), null, database);
	}

	public MonsterDetailPanel(
		SlayerMonster monster,
		List<MonsterLocation> locations,
		Actions actions,
		SpriteManager sprites,
		MonsterImageLoader images,
		WikiLoadoutClient wiki,
		WikiInventoryClient inventory,
		GearRecommendationService recommendations,
		MonsterDatabase database)
	{
		setBorder(new EmptyBorder(0, 0, 8, 0));

		addLocations(locations, actions);
		addSection("Required items", SkillRequirement.items(monster.getRequiredItems()));
		addTextSection("Weaknesses", monster.getWeakness());
		prayers = new PraySection(monster, sprites, recommendations);
		add(prayers);
		gear = new GearSection(monster, images, wiki, inventory, recommendations, prayers::setStyle);
		add(gear);
		addNotes(monster);
		addAlternatives(monster, actions, images, database);
	}

	public void refreshGear()
	{
		gear.refreshRecommendations();
	}

	public void refreshPrayers()
	{
		prayers.refreshRecommendations();
	}

	private void addLocations(
		List<MonsterLocation> locations,
		Actions actions)
	{
		JPanel section = PanelWidgets.section("Locations", 0);
		if (locations.isEmpty())
		{
			section.add(PanelWidgets.muted("No locations recorded."));
			add(section);
			return;
		}

		for (MonsterLocation location : locations)
		{
			JButton map = PanelWidgets.button(PanelCopy.SHOW_ON_MAP);
			map.setName("show-on-map");
			map.setToolTipText("Open this location on the world map");
			map.addActionListener(event -> actions.showOnMap(location));
			JButton path = pathButton(location, actions);
			section.add(new LocationCard(location, new LocationActionButtons(map, path)));
			section.add(Box.createVerticalStrut(6));
		}
		add(section);
	}

	private static JButton pathButton(MonsterLocation location, Actions actions)
	{
		if (!actions.canPath())
		{
			return null;
		}
		JButton path = PanelWidgets.button(PanelCopy.PATH_HERE);
		path.setName("path-here");
		path.addActionListener(event -> actions.pathTo(location));
		return path;
	}

	private void addSection(String heading, List<String> items)
	{
		if (items == null || items.isEmpty())
		{
			return;
		}
		JPanel section = PanelWidgets.section(heading);
		PanelWidgets.addBullets(section, items);
		add(section);
	}

	private void addNotes(SlayerMonster monster)
	{
		String notes = MonsterNotesText.display(monster);
		if (notes.isEmpty())
		{
			return;
		}
		JPanel section = PanelWidgets.section("Notes");
		section.setName("notes-section");
		section.add(PanelWidgets.wrapped(notes));
		add(section);
	}

	private void addAlternatives(
		SlayerMonster monster,
		Actions actions,
		MonsterImageLoader images,
		MonsterDatabase database)
	{
		List<String> alternatives = monster.getAlternatives();
		if (alternatives == null || alternatives.isEmpty())
		{
			return;
		}
		JPanel section = PanelWidgets.section("Alternatives");
		section.setName("alternatives-section");
		for (String alternative : alternatives)
		{
			SlayerMonster resolved = AlternativeMonsters.resolve(database, alternative, monster);
			section.add(new AlternativeItem(
				alternative,
				resolved,
				images,
				() -> actions.openMonster(resolved)));
			section.add(Box.createVerticalStrut(4));
		}
		add(section);
	}

	private void addTextSection(String heading, String text)
	{
		if (text == null || text.isEmpty())
		{
			return;
		}
		JPanel section = PanelWidgets.section(heading);
		section.add(PanelWidgets.wrapped(text));
		add(section);
	}
}
