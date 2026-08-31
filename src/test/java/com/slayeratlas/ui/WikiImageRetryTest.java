package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class WikiImageRetryTest
{
	@Test
	public void missingWikiFilesTryTheNumberedVariant()
	{
		WikiImageDownload download = new WikiImageDownload("Baby blue dragon.png", 1, false);
		WikiImageDownload variant = WikiImageRetry.next(download, 404, true);

		assertEquals("Baby blue dragon.png", variant.fileName());
		assertEquals("Baby blue dragon (1).png", variant.fetchName());
		assertEquals(1, variant.attempt());
		assertEquals(0, WikiImageRetry.delayMs(download, 404));
	}

	@Test
	public void missingItemIconsTryProperNounCapitalization()
	{
		WikiImageDownload download = new WikiImageDownload(
			"Bow of faerdhinen.png",
			"Bow of faerdhinen (1).png",
			1,
			true);
		WikiImageDownload variant = WikiImageRetry.next(download, 404, true);

		assertEquals("Bow of faerdhinen.png", variant.fileName());
		assertEquals("Bow of Faerdhinen.png", variant.fetchName());
	}

	@Test
	public void numberedFilesStopAfterFallbacksAreExhausted()
	{
		WikiImageDownload download = new WikiImageDownload(
			"Jelly.png",
			"Jelly (1).png",
			1,
			true);
		assertNull(WikiImageRetry.next(download, 404, true));
	}

	@Test
	public void keepsRetryingTransientFailuresWhileSomeoneIsWaiting()
	{
		WikiImageDownload download = new WikiImageDownload("Jelly.png", WikiImageFetchPolicy.MAX_ATTEMPTS, false);
		WikiImageDownload next = WikiImageRetry.next(download, 429, true);

		assertEquals(WikiImageFetchPolicy.MAX_ATTEMPTS + 1, next.attempt());
		assertEquals(WikiImageFetchPolicy.EXHAUSTED_DELAY_MS, WikiImageRetry.delayMs(download, 429));
	}

	@Test
	public void givesUpTransientFailuresWhenNobodyIsWaiting()
	{
		WikiImageDownload download = new WikiImageDownload("Jelly.png", WikiImageFetchPolicy.MAX_ATTEMPTS, false);
		assertNull(WikiImageRetry.next(download, 429, false));
	}
}
