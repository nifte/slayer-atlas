package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import org.junit.Test;

public class BackButtonTest
{
	@Test
	public void isFullWidthWithLabelAndArrow()
	{
		BackButton button = new BackButton(() ->
		{
		});
		assertEquals(PanelCopy.BACK_TO_LIST, button.getText());
		assertNotNull(button.getIcon());
		assertEquals(Integer.MAX_VALUE, button.getMaximumSize().width);
		Dimension preferred = button.getPreferredSize();
		assertTrue(preferred.width > preferred.height);
	}
}
