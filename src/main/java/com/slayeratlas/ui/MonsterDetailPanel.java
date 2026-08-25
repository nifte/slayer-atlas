package com.slayeratlas.ui;

import com.slayeratlas.data.AlternativeMonsters;
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
	public interface Actions
	{
		void pathTo(MonsterLocation location);

		void pathToNearest(SlayerMonster monster);

		void openWiki(SlayerMonster monster);

		void openDps(SlayerMonster monster);

		void openMonster(SlayerMonster monster);

		boolean canPath();

		String pathUnavailableReason();
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
		this(monster, locations, actions, sprites, images, wiki, null);
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
		setBorder(new EmptyBorder(0, 0, 8, 0));

		addLocations(monster, locations, actions);
		addSection("Required items", SkillRequirement.items(monster.getRequiredItems()));
		addTextSection("Weaknesses", monster.getWeakness());
		PraySection prayers = new PraySection(monster, sprites);
		add(prayers);
		add(new GearSection(monster, images, wiki, prayers::setStyle));
		addNotes(monster);
		addAlternatives(monster, actions, images, database);
		addWikiAndDps(monster, actions);
	}

	private void addLocations(
		SlayerMonster monster,
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
			JButton path = PanelWidgets.button("Path here");
			configurePathButton(path, actions, () -> actions.pathTo(location));
			section.add(new LocationCard(location, path));
			section.add(Box.createVerticalStrut(6));
		}

		JButton nearest = PanelWidgets.button("Path to nearest location");
		nearest.setName("path-to-nearest");
		configurePathButton(nearest, actions, () -> actions.pathToNearest(monster));
		section.add(nearest);
		add(section);
	}

	private void configurePathButton(JButton button, Actions actions, Runnable onClick)
	{
		if (actions.canPath())
		{
			button.addActionListener(e -> onClick.run());
			return;
		}
		button.setEnabled(false);
		button.setToolTipText(actions.pathUnavailableReason());
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

	private void addWikiAndDps(SlayerMonster monster, Actions actions)
	{
		add(Box.createVerticalStrut(8));
		String dpsUrl = DpsCalculatorUrl.fromMonster(monster);
		add(new HeaderActionButtons(
			() -> actions.openWiki(monster),
			dpsUrl.isEmpty() ? null : () -> actions.openDps(monster)));
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
