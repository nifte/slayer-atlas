package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SectionHeadingTest
{
	@Test
	public void titleCasesWordsWithoutAllCaps()
	{
		assertEquals("Attack Style", SectionHeading.display("Attack style"));
		assertEquals("Recommended Equipment", SectionHeading.display("Recommended equipment"));
		assertEquals("Locations & Travel", SectionHeading.display("Locations & travel"));
		assertEquals("Notes", SectionHeading.display("Notes"));
		assertEquals("Attack Style", SectionHeading.display("ATTACK STYLE"));
	}

	@Test
	public void emptyWhenMissing()
	{
		assertEquals("", SectionHeading.display(null));
		assertEquals("", SectionHeading.display(""));
	}
}
