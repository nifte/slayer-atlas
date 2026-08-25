package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SectionHeadingTest
{
	@Test
	public void titleCasesWordsWithoutAllCaps()
	{
		assertEquals("Attack Style", SectionHeading.display("Attack style"));
		assertEquals("Recommended Equipment", SectionHeading.display("Recommended equipment"));
		assertEquals("Locations", SectionHeading.display("Locations"));
		assertEquals("Recommended Prayers", SectionHeading.display("Recommended Prayers"));
		assertEquals("Foo & Bar", SectionHeading.display("Foo & bar"));
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
