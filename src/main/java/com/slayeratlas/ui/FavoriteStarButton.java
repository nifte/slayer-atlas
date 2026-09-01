package com.slayeratlas.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;

public class FavoriteStarButton extends JToggleButton
{
	private final Consumer<Boolean> onToggle;

	public FavoriteStarButton(String monsterId, boolean favorite, Consumer<Boolean> onToggle)
	{
		super(StarIcon.off());
		this.onToggle = onToggle;
		setName("favorite-" + monsterId);
		setSelectedIcon(StarIcon.on());
		setFocusable(false);
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setPreferredSize(new Dimension(20, 20));
		setMinimumSize(new Dimension(20, 20));
		setMaximumSize(new Dimension(20, Integer.MAX_VALUE));
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
		setToolTipText(isSelected() ? PanelCopy.UNPIN_TASK : PanelCopy.PIN_TASK);
	}
}
