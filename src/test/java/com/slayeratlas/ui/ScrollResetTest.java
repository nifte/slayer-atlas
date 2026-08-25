package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.junit.Test;

public class ScrollResetTest
{
	@Test
	public void movesViewportToTheTop()
	{
		JPanel view = new JPanel();
		view.setPreferredSize(new Dimension(100, 2000));
		JScrollPane scroll = new JScrollPane(view);
		scroll.setSize(100, 200);
		scroll.doLayout();
		scroll.getViewport().setViewSize(view.getPreferredSize());
		scroll.getVerticalScrollBar().setValue(250);

		ScrollReset.toTop(scroll);

		assertEquals(0, scroll.getVerticalScrollBar().getValue());
		assertEquals(0, scroll.getViewport().getViewPosition().y);
		assertEquals(0, scroll.getHorizontalScrollBar().getValue());
	}
}
