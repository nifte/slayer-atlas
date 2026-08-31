package com.slayeratlas.ui;

import java.util.List;

final class WikiImageDownload
{
	private final String fileName;
	private final String fetchName;
	private final int attempt;
	private final boolean urgent;

	WikiImageDownload(String fileName, int attempt, boolean urgent)
	{
		this(fileName, fileName, attempt, urgent);
	}

	WikiImageDownload(String fileName, String fetchName, int attempt, boolean urgent)
	{
		this.fileName = fileName;
		this.fetchName = fetchName == null || fetchName.isEmpty() ? fileName : fetchName;
		this.attempt = attempt;
		this.urgent = urgent;
	}

	String fileName()
	{
		return fileName;
	}

	String fetchName()
	{
		return fetchName;
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
		return new WikiImageDownload(fileName, fetchName, attempt + 1, urgent);
	}

	WikiImageDownload asUrgent()
	{
		return urgent ? this : new WikiImageDownload(fileName, fetchName, attempt, true);
	}

	WikiImageDownload firstVariant()
	{
		String variant = WikiImageUrl.firstVariant(fetchName);
		if (variant.isEmpty())
		{
			return null;
		}
		return new WikiImageDownload(fileName, variant, 1, urgent);
	}

	WikiImageDownload nextFallback()
	{
		List<String> names = WikiImageUrl.fetchNames(fileName);
		int index = names.indexOf(fetchName);
		if (index < 0 || index + 1 >= names.size())
		{
			return null;
		}
		return new WikiImageDownload(fileName, names.get(index + 1), 1, urgent);
	}
}
