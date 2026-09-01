package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearLoadouts;
import com.slayeratlas.data.GearRecommendationService;
import com.slayeratlas.data.SlayerMonster;
import java.util.List;
import net.runelite.client.game.SpriteManager;

public class PraySection extends ViewportWidthPanel
{
	private final List<ProtectionPrayer> protections;
	private final SpriteManager sprites;
	private final GearRecommendationService recommendations;
	private CombatStyle style;
	private List<String> savedPrayers = List.of();

	public PraySection(SlayerMonster monster, SpriteManager sprites)
	{
		this(monster, sprites, null);
	}

	public PraySection(SlayerMonster monster, SpriteManager sprites, GearRecommendationService recommendations)
	{
		this.protections = ProtectionPrayer.parse(monster == null ? null : monster.getProtectionPrayer());
		this.sprites = sprites;
		this.recommendations = recommendations;
		this.style = GearLoadouts.forMonster(monster, List.of()).get(0).getStyle();
		setName("pray-section");
		add(PanelWidgets.sectionHeading("Recommended Prayers"));
		rebuild();
	}

	public void setStyle(CombatStyle style)
	{
		showPrayers(style, List.of());
	}

	public void showPrayers(CombatStyle style, List<String> savedPrayers)
	{
		CombatStyle nextStyle = style == null ? this.style : style;
		List<String> nextPrayers = savedPrayers == null ? List.of() : List.copyOf(savedPrayers);
		if (nextStyle == this.style && nextPrayers.equals(this.savedPrayers))
		{
			return;
		}
		this.style = nextStyle;
		this.savedPrayers = nextPrayers;
		rebuild();
		revalidate();
		repaint();
	}

	public void refreshRecommendations()
	{
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
		if (!savedPrayers.isEmpty())
		{
			add(PrayIcons.saved(savedPrayers, sprites));
			return;
		}
		boolean onlyUnlocked = recommendations != null && recommendations.onlyUnlockedPrayers();
		add(new PrayIcons(
			ProtectionPrayer.recommended(
				protections,
				onlyUnlocked,
				recommendations == null ? null : recommendations.unlockedPrayers()),
			CombatPrayer.recommended(
				style,
				onlyUnlocked,
				recommendations == null ? null : recommendations.unlockedPrayers()),
			sprites));
	}
}
