package com.slayeratlas.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.RuneLite;

@Singleton
public final class WikiImageCache
{
	static final long MAX_AGE_MS = TimeUnit.DAYS.toMillis(30);

	private final Path directory;

	@Inject
	public WikiImageCache()
	{
		this(RuneLite.RUNELITE_DIR.toPath().resolve("slayer-atlas").resolve("images"));
	}

	public WikiImageCache(Path directory)
	{
		this.directory = directory;
	}

	public boolean contains(String fileName)
	{
		Path file = file(fileName);
		return file != null && Files.isRegularFile(file);
	}

	public boolean stale(String fileName)
	{
		Path file = file(fileName);
		if (file == null || !Files.isRegularFile(file))
		{
			return false;
		}
		try
		{
			long ageMs = System.currentTimeMillis() - Files.getLastModifiedTime(file).toMillis();
			return ageMs >= MAX_AGE_MS;
		}
		catch (IOException ignored)
		{
			return true;
		}
	}

	public byte[] load(String fileName)
	{
		Path file = file(fileName);
		if (file == null || !Files.isRegularFile(file))
		{
			return null;
		}
		try
		{
			return Files.readAllBytes(file);
		}
		catch (IOException ignored)
		{
			return null;
		}
	}

	public void save(String fileName, byte[] bytes)
	{
		if (bytes == null || bytes.length == 0)
		{
			return;
		}
		Path file = file(fileName);
		if (file == null)
		{
			return;
		}
		try
		{
			Files.createDirectories(file.getParent());
			Files.write(file, bytes);
		}
		catch (IOException ignored)
		{
		}
	}

	public void delete(String fileName)
	{
		Path file = file(fileName);
		if (file == null)
		{
			return;
		}
		try
		{
			Files.deleteIfExists(file);
		}
		catch (IOException ignored)
		{
		}
	}

	static String sanitize(String fileName)
	{
		if (fileName == null)
		{
			return "";
		}
		String trimmed = fileName.trim();
		if (trimmed.isEmpty() || trimmed.contains(".."))
		{
			return "";
		}
		StringBuilder safe = new StringBuilder();
		for (int index = 0; index < trimmed.length(); index++)
		{
			char character = trimmed.charAt(index);
			if (character == '/' || character == '\\' || character == ':' || character == '*'
				|| character == '?' || character == '"' || character == '<' || character == '>'
				|| character == '|')
			{
				safe.append('_');
			}
			else if (character >= 32)
			{
				safe.append(character);
			}
		}
		return safe.toString();
	}

	private Path file(String fileName)
	{
		if (directory == null)
		{
			return null;
		}
		String safe = sanitize(fileName);
		if (safe.isEmpty())
		{
			return null;
		}
		Path root = directory.toAbsolutePath().normalize();
		Path resolved = root.resolve(safe).normalize();
		if (!resolved.startsWith(root))
		{
			return null;
		}
		return resolved;
	}
}
