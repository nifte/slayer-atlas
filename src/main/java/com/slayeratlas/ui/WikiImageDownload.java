package com.slayeratlas.ui;

import java.util.List;

final class WikiImageDownload
{
	private final String fileName;
	private final String fetchName;
	private final int attempt;
	private final boolean urgent;
	private final boolean refresh;

	WikiImageDownload(String fileName, int attempt, boolean urgent)
	{
		this(fileName, fileName, attempt, urgent, false);
	}

	WikiImageDownload(String fileName, String fetchName, int attempt, boolean urgent)
	{
		this(fileName, fetchName, attempt, urgent, false);
	}

	WikiImageDownload(String fileName, String fetchName, int attempt, boolean urgent, boolean refresh)
	{
		this.fileName = fileName;
		this.fetchName = fetchName == null || fetchName.isEmpty() ? fileName : fetchName;
		this.attempt = attempt;
		this.urgent = urgent;
		this.refresh = refresh;
	}

	static WikiImageDownload refresh(String fileName)
	{
		return new WikiImageDownload(fileName, fileName, 1, false, true);
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

	boolean isRefresh()
	{
		return refresh;
	}

	WikiImageDownload nextAttempt()
	{
		return new WikiImageDownload(fileName, fetchName, attempt + 1, urgent, refresh);
	}

	WikiImageDownload asUrgent()
	{
		return urgent ? this : new WikiImageDownload(fileName, fetchName, attempt, true, refresh);
	}

	WikiImageDownload firstVariant()
	{
		String variant = WikiImageUrl.firstVariant(fetchName);
		if (variant.isEmpty())
		{
			return null;
		}
		return new WikiImageDownload(fileName, variant, 1, urgent, refresh);
	}

	WikiImageDownload nextFallback()
	{
		List<String> names = WikiImageUrl.fetchNames(fileName);
		int index = names.indexOf(fetchName);
		if (index < 0 || index + 1 >= names.size())
		{
			return null;
		}
		return new WikiImageDownload(fileName, names.get(index + 1), 1, urgent, refresh);
	}
}
