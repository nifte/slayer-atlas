package com.slayeratlas.ui;

public final class WikiImageFetchPolicy
{
	public static final int SOURCE_WIDTH = 96;
	public static final int MAX_CONCURRENT = 6;
	public static final int MAX_ATTEMPTS = 5;
	public static final int EXHAUSTED_DELAY_MS = 4_000;

	private WikiImageFetchPolicy()
	{
	}

	public static boolean isTransient(int statusCode)
	{
		return statusCode <= 0 || statusCode == 429 || statusCode >= 500;
	}

	public static boolean shouldRetry(int statusCode, int attempt)
	{
		if (attempt >= MAX_ATTEMPTS)
		{
			return false;
		}
		return isTransient(statusCode);
	}

	public static int retryDelayMs(int statusCode, int attempt)
	{
		if (!shouldRetry(statusCode, attempt))
		{
			return 0;
		}
		int shift = Math.max(0, attempt - 1);
		int base = statusCode == 429 ? 500 : 150;
		return Math.min(4_000, base << shift);
	}
}
