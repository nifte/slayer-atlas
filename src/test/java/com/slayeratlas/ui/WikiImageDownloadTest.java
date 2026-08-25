package com.slayeratlas.ui;

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
}
