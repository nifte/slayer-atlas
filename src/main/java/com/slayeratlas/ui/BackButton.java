package com.slayeratlas.ui;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class BackButton extends JButton
{
	public BackButton(Runnable onBack)
	{
		super(PanelCopy.BACK_TO_LIST, BackArrowIcon.icon());
		PanelWidgets.styleButton(this);
		setHorizontalAlignment(SwingConstants.CENTER);
		setIconTextGap(8);
		setAlignmentX(LEFT_ALIGNMENT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		addActionListener(event -> onBack.run());
	}
}
