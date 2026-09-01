package com.slayeratlas.ui;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class WikiImageCacheTest
{
	@Test
	public void savesAndLoadsBytesByWikiFileName() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-images");
		WikiImageCache cache = new WikiImageCache(directory);
		byte[] png = "fake-png".getBytes(StandardCharsets.UTF_8);
		cache.save("Prayer potion(4).png", png);
		assertTrue(cache.contains("Prayer potion(4).png"));
		assertArrayEquals(png, cache.load("Prayer potion(4).png"));
		assertFalse(cache.stale("Prayer potion(4).png"));
	}

	@Test
	public void returnsNullWhenTheFileIsMissing() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-images");
		WikiImageCache cache = new WikiImageCache(directory);
		assertFalse(cache.contains("Bloodveld.png"));
		assertFalse(cache.stale("Bloodveld.png"));
		assertNull(cache.load("Bloodveld.png"));
	}

	@Test
	public void sanitizesUnsafeFileNamesAndRejectsParentPaths()
	{
		assertEquals("Bloodveld.png", WikiImageCache.sanitize("Bloodveld.png"));
		assertEquals("Prayer potion(4).png", WikiImageCache.sanitize("Prayer potion(4).png"));
		assertEquals("Ava's assembler.png", WikiImageCache.sanitize("Ava's assembler.png"));
		assertEquals("foo_bar.png", WikiImageCache.sanitize("foo/bar.png"));
		assertEquals("", WikiImageCache.sanitize("../secret.png"));
		assertEquals("", WikiImageCache.sanitize("foo/../bar.png"));
	}

	@Test
	public void treatsCachedFilesOlderThanAMonthAsStale() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-images");
		WikiImageCache cache = new WikiImageCache(directory);
		cache.save("Bloodveld.png", new byte[] {1, 2, 3});
		Path file = directory.resolve("Bloodveld.png");
		Files.setLastModifiedTime(
			file,
			FileTime.fromMillis(System.currentTimeMillis() - WikiImageCache.MAX_AGE_MS - TimeUnit.DAYS.toMillis(1)));
		assertTrue(cache.contains("Bloodveld.png"));
		assertTrue(cache.stale("Bloodveld.png"));
		assertArrayEquals(new byte[] {1, 2, 3}, cache.load("Bloodveld.png"));
	}

	@Test
	public void deletesCorruptCacheFiles() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-images");
		WikiImageCache cache = new WikiImageCache(directory);
		cache.save("Bloodveld.png", new byte[] {1, 2, 3});
		assertTrue(cache.contains("Bloodveld.png"));
		cache.delete("Bloodveld.png");
		assertFalse(cache.contains("Bloodveld.png"));
	}
}
