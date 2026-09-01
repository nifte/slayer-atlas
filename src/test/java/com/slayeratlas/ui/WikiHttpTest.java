package com.slayeratlas.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WikiHttpTest
{
	@Test
	public void identifiesThePluginWithoutTheBlockedUserAgent()
	{
		assertTrue(WikiHttp.USER_AGENT.startsWith("SlayerAtlas/"));
		assertTrue(WikiHttp.USER_AGENT.contains("https://github.com/nifte/slayer-atlas"));
		assertFalse(WikiHttp.USER_AGENT.contains("SlayerAtlasRuneLitePlugin"));
	}
}
