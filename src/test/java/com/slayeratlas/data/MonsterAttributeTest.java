package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MonsterAttributeTest
{
	@Test
	public void usesTheWikiAttributeName()
	{
		assertEquals("draconic", MonsterAttribute.display("Draconic"));
		assertEquals("draconic", MonsterAttribute.display("Dragon"));
		assertEquals("undead", MonsterAttribute.display("Undead"));
		assertEquals("demon", MonsterAttribute.display("Demon"));
		assertEquals("", MonsterAttribute.display(null));
		assertEquals("", MonsterAttribute.display(""));
	}
}
