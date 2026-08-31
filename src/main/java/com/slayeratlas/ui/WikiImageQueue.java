package com.slayeratlas.ui;

import java.util.Deque;

final class WikiImageQueue
{
	private WikiImageQueue()
	{
	}

	static void enqueueNew(Deque<WikiImageDownload> urgent, Deque<WikiImageDownload> background, WikiImageDownload download)
	{
		if (download.urgent())
		{
			urgent.addLast(download);
		}
		else
		{
			background.addLast(download);
		}
	}

	static void promote(Deque<WikiImageDownload> urgent, WikiImageDownload download)
	{
		urgent.addLast(download.asUrgent());
	}

	static void requeueRetry(Deque<WikiImageDownload> urgent, Deque<WikiImageDownload> background, WikiImageDownload download)
	{
		if (download.urgent())
		{
			urgent.addFirst(download);
		}
		else
		{
			background.addFirst(download);
		}
	}
}
