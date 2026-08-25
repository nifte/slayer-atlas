package com.slayeratlas.ui;

import com.slayeratlas.data.CombatStyle;
import com.slayeratlas.data.GearLoadouts;
import com.slayeratlas.data.SlayerMonster;
import java.util.List;
import net.runelite.client.game.SpriteManager;

public class PraySection extends ViewportWidthPanel
{
	private final List<ProtectionPrayer> protections;
	private final SpriteManager sprites;
	private CombatStyle style;

	public PraySection(SlayerMonster monster, SpriteManager sprites)
	{
		this.protections = ProtectionPrayer.parse(monster == null ? null : monster.getProtectionPrayer());
		this.sprites = sprites;
		this.style = GearLoadouts.forMonster(monster, List.of()).get(0).getStyle();
		setName("pray-section");
		add(PanelWidgets.sectionHeading("Recommended Prayers"));
		rebuild();
	}

	public void setStyle(CombatStyle style)
	{
		if (style == null || style == this.style)
		{
			return;
		}
		this.style = style;
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
		add(new PrayIcons(protections, CombatPrayer.forStyle(style), sprites));
	}
}
