package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WikiImageDownloadTest
{
	@Test
	public void retriesKeepPriority()
	{
		WikiImageDownload background = new WikiImageDownload("Jelly.png", 1, false);
		assertFalse(background.nextAttempt().urgent());
		assertTrue(background.asUrgent().urgent());

		WikiImageDownload urgent = new WikiImageDownload("Abyssal_whip.png", 2, true);
		assertTrue(urgent.nextAttempt().urgent());
		assertSame(urgent, urgent.asUrgent());
	}

	@Test
	public void firstVariantKeepsTheOriginalCacheKey()
	{
		WikiImageDownload download = new WikiImageDownload("Baby blue dragon.png", 3, true).firstVariant();
		assertEquals("Baby blue dragon.png", download.fileName());
		assertEquals("Baby blue dragon (1).png", download.fetchName());
		assertEquals(1, download.attempt());
		assertTrue(download.urgent());
	}

	@Test
	public void refreshDownloadsStayInTheBackground()
	{
		WikiImageDownload refresh = WikiImageDownload.refresh("Bloodveld.png");
		assertTrue(refresh.isRefresh());
		assertFalse(refresh.urgent());
		assertEquals(1, refresh.attempt());
		assertTrue(refresh.nextAttempt().isRefresh());
		assertTrue(refresh.asUrgent().isRefresh());
		assertTrue(refresh.asUrgent().urgent());
	}
}
