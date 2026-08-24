package com.slayerguide.ui;

final class WikiImageDownload
{
	private final String fileName;
	private final int attempt;

	WikiImageDownload(String fileName, int attempt)
	{
		this.fileName = fileName;
		this.attempt = attempt;
	}

	String fileName()
	{
		return fileName;
	}

	int attempt()
	{
		return attempt;
	}

	WikiImageDownload nextAttempt()
	{
		return new WikiImageDownload(fileName, attempt + 1);
	}
}
