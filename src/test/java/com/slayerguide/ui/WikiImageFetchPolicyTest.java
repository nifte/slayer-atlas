package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WikiImageFetchPolicyTest
{
	@Test
	public void retriesTransientFailuresUntilTheLimit()
	{
		assertTrue(WikiImageFetchPolicy.shouldRetry(0, 1));
		assertTrue(WikiImageFetchPolicy.shouldRetry(429, 2));
		assertTrue(WikiImageFetchPolicy.shouldRetry(503, 1));
		assertFalse(WikiImageFetchPolicy.shouldRetry(404, 1));
		assertFalse(WikiImageFetchPolicy.shouldRetry(200, 1));
		assertFalse(WikiImageFetchPolicy.shouldRetry(429, WikiImageFetchPolicy.MAX_ATTEMPTS));
	}
}
