package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WikiParenFileNameTest
{
	@Test
	public void togglesSpaceBeforeLetterTagsOnly()
	{
		assertEquals("Salve amulet(ei).png", WikiParenFileName.alternate("Salve amulet (ei).png"));
		assertEquals("Salve amulet (ei).png", WikiParenFileName.alternate("Salve amulet(ei).png"));
		assertEquals("Salve amulet(ei) (1).png", WikiParenFileName.alternate("Salve amulet (ei) (1).png"));
		assertEquals("", WikiParenFileName.alternate("Prayer potion(4).png"));
		assertEquals("", WikiParenFileName.alternate("Shark.png"));
	}
}
