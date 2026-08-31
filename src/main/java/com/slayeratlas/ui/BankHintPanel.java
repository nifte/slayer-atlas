package com.slayeratlas.ui;

import javax.swing.JTextArea;

public class BankHintPanel extends ViewportWidthPanel
{
	public BankHintPanel()
	{
		setName("bank-hint");
		JTextArea text = PanelWidgets.wrapped(PanelCopy.OPEN_BANK_HINT);
		text.setName("bank-hint-text");
		add(text);
	}
}
