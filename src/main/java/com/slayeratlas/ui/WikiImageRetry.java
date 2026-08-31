package com.slayeratlas.ui;

final class WikiImageRetry
{
	private WikiImageRetry()
	{
	}

	static WikiImageDownload next(WikiImageDownload download, int statusCode, boolean waiting)
	{
		if (download == null)
		{
			return null;
		}
		if (statusCode == 404)
		{
			return download.nextFallback();
		}
		if (WikiImageFetchPolicy.shouldRetry(statusCode, download.attempt()))
		{
			return download.nextAttempt();
		}
		if (waiting && WikiImageFetchPolicy.isTransient(statusCode))
		{
			return download.nextAttempt();
		}
		return null;
	}

	static int delayMs(WikiImageDownload failed, int statusCode)
	{
		if (failed == null || statusCode == 404)
		{
			return 0;
		}
		if (WikiImageFetchPolicy.shouldRetry(statusCode, failed.attempt()))
		{
			return WikiImageFetchPolicy.retryDelayMs(statusCode, failed.attempt());
		}
		return WikiImageFetchPolicy.EXHAUSTED_DELAY_MS;
	}
}
