package com.slayeratlas.ui;

import com.slayeratlas.data.SlayerMonster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class MonsterListItem extends JPanel
{
	private final String monsterId;
	private final FavoriteStarButton star;
	private final JLabel name;
	private boolean hovered;
	private boolean previewed;

	public MonsterListItem(
		SlayerMonster monster,
		boolean currentTask,
		boolean favorite,
		MonsterImageLoader images,
		Consumer<Boolean> onFavoriteChanged,
		Runnable onSelect)
	{
		monsterId = monster.getId();
		setName("monster-" + monsterId);
		setLayout(new BorderLayout(8, 0));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

		add(new MonsterPortrait(monster, MonsterImageSizes.LIST, images, false), BorderLayout.WEST);

		name = new JLabel(MonsterName.display(monster.getName()));
		name.setFont(PanelFonts.body());
		add(name, BorderLayout.CENTER);
		setCurrentTask(currentTask);

		PanelWidgets.makeHoverable(this, onSelect, this::setHovered);

		star = new FavoriteStarButton(monsterId, favorite, onFavoriteChanged);
		star.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				setHovered(true);
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				Point point = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), MonsterListItem.this);
				if (!contains(point))
				{
					setHovered(false);
				}
			}
		});
		add(star, BorderLayout.EAST);
	}

	public String getMonsterId()
	{
		return monsterId;
	}

	public void setFavorite(boolean favorite)
	{
		star.setFavorite(favorite);
	}

	public void setCurrentTask(boolean currentTask)
	{
		name.setForeground(currentTask ? ColorScheme.BRAND_ORANGE : Color.WHITE);
	}

	public void setPreviewed(boolean previewed)
	{
		this.previewed = previewed;
		refreshBackground();
	}

	private void setHovered(boolean hovered)
	{
		this.hovered = hovered;
		refreshBackground();
	}

	private void refreshBackground()
	{
		setBackground(hovered || previewed
			? ColorScheme.DARKER_GRAY_HOVER_COLOR
			: ColorScheme.DARKER_GRAY_COLOR);
		repaint();
	}

	public void clearPointerHover()
	{
		hovered = false;
		refreshBackground();
	}
}
