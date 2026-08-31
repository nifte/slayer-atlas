package com.slayeratlas.ui;

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
		assertTrue(WikiImageFetchPolicy.isTransient(429));
		assertTrue(WikiImageFetchPolicy.isTransient(503));
		assertFalse(WikiImageFetchPolicy.isTransient(404));
	}

	@Test
	public void backsOffOnRateLimits()
	{
		assertEquals(500, WikiImageFetchPolicy.retryDelayMs(429, 1));
		assertEquals(1000, WikiImageFetchPolicy.retryDelayMs(429, 2));
		assertEquals(0, WikiImageFetchPolicy.retryDelayMs(404, 1));
	}
}
