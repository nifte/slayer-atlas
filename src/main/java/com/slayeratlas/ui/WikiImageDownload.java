package com.slayeratlas.ui;

final class WikiImageDownload
{
	private final String fileName;
	private final int attempt;
	private final boolean urgent;

	WikiImageDownload(String fileName, int attempt, boolean urgent)
	{
		this.fileName = fileName;
		this.attempt = attempt;
		this.urgent = urgent;
	}

	String fileName()
	{
		return fileName;
	}

	int attempt()
	{
		return attempt;
	}

	boolean urgent()
	{
		return urgent;
	}

	WikiImageDownload nextAttempt()
	{
		return new WikiImageDownload(fileName, attempt + 1, urgent);
	}

	WikiImageDownload asUrgent()
	{
		return urgent ? this : new WikiImageDownload(fileName, attempt, true);
	}
}
