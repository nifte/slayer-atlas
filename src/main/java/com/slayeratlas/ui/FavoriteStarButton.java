package com.slayeratlas.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;

public class FavoriteStarButton extends JToggleButton
{
	private static final int SIZE = 24;
	private final Consumer<Boolean> onToggle;

	public FavoriteStarButton(String monsterId, boolean favorite, Consumer<Boolean> onToggle)
	{
		super(StarIcon.off());
		this.onToggle = onToggle;
		setName("favorite-" + monsterId);
		setSelectedIcon(StarIcon.on());
		setRolloverIcon(StarIcon.offHover());
		setRolloverSelectedIcon(StarIcon.onHover());
		setRolloverEnabled(true);
		setFocusable(false);
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setPreferredSize(new Dimension(SIZE, SIZE));
		setMinimumSize(new Dimension(SIZE, SIZE));
		setMaximumSize(new Dimension(SIZE, Integer.MAX_VALUE));
		setSelected(favorite);
		refreshTooltip();
		addActionListener(event ->
		{
			refreshTooltip();
			if (this.onToggle != null)
			{
				this.onToggle.accept(isSelected());
			}
		});
	}

	public void setFavorite(boolean favorite)
	{
		if (isSelected() != favorite)
		{
			setSelected(favorite);
		}
		refreshTooltip();
	}

	private void refreshTooltip()
	{
		setToolTipText(isSelected() ? PanelCopy.UNFAVORITE : PanelCopy.MARK_FAVORITE);
	}
}
