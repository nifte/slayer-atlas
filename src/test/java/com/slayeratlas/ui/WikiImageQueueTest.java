package com.slayeratlas.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.Test;

public class WikiImageQueueTest
{
	@Test
	public void newWorkStaysInListOrder()
	{
		Deque<WikiImageDownload> urgent = new ArrayDeque<>();
		Deque<WikiImageDownload> background = new ArrayDeque<>();

		WikiImageQueue.enqueueNew(urgent, background, new WikiImageDownload("Ankou.png", 1, false));
		WikiImageQueue.enqueueNew(urgent, background, new WikiImageDownload("Aquanite.png", 1, false));
		WikiImageQueue.promote(urgent, background.removeFirst());
		WikiImageQueue.promote(urgent, background.removeFirst());
		WikiImageQueue.enqueueNew(urgent, background, new WikiImageDownload("Aviansie.png", 1, true));

		assertEquals("Ankou.png", urgent.removeFirst().fileName());
		assertEquals("Aquanite.png", urgent.removeFirst().fileName());
		assertEquals("Aviansie.png", urgent.removeFirst().fileName());
		assertTrue(background.isEmpty());
	}

	@Test
	public void retriesJumpTheQueueWithoutReversingOtherWork()
	{
		Deque<WikiImageDownload> urgent = new ArrayDeque<>();
		Deque<WikiImageDownload> background = new ArrayDeque<>();
		WikiImageDownload retry = new WikiImageDownload("Ankou.png", 2, false);

		WikiImageQueue.enqueueNew(urgent, background, new WikiImageDownload("Aquanite.png", 1, false));
		WikiImageQueue.requeueRetry(urgent, background, retry);

		assertEquals("Ankou.png", background.removeFirst().fileName());
		assertEquals("Aquanite.png", background.removeFirst().fileName());
		assertFalse(retry.urgent());
	}
}
