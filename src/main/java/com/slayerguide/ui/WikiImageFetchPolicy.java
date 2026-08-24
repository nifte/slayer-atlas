package com.slayerguide.ui;

public final class WikiImageFetchPolicy
{
	public static final int SOURCE_WIDTH = 96;
	public static final int MAX_CONCURRENT = 3;
	public static final int MAX_ATTEMPTS = 3;

	private WikiImageFetchPolicy()
	{
	}

	public static boolean shouldRetry(int statusCode, int attempt)
	{
		if (attempt >= MAX_ATTEMPTS)
		{
			return false;
		}
		return statusCode <= 0 || statusCode == 429 || statusCode >= 500;
	}
}
