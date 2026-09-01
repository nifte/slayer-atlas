package com.slayeratlas.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.RuneLite;

@Singleton
public final class BankSnapshotStore
{
	private final Path directory;
	private final Gson gson;

	@Inject
	public BankSnapshotStore(Gson gson)
	{
		this(RuneLite.RUNELITE_DIR.toPath().resolve("slayer-atlas"), gson);
	}

	public BankSnapshotStore(Path directory, Gson gson)
	{
		this.directory = directory;
		this.gson = gson;
	}

	public void save(long accountHash, List<Integer> itemIds)
	{
		save(accountHash, itemIds, List.of());
	}

	public void save(long accountHash, List<Integer> itemIds, List<Integer> potionIds)
	{
		if (directory == null || gson == null)
		{
			return;
		}
		try
		{
			Files.createDirectories(directory);
			Snapshot snapshot = new Snapshot();
			snapshot.accountHash = accountHash;
			snapshot.itemIds = itemIds == null ? List.of() : new ArrayList<>(itemIds);
			snapshot.potionIds = potionIds == null ? List.of() : new ArrayList<>(potionIds);
			Files.writeString(file(accountHash), gson.toJson(snapshot), StandardCharsets.UTF_8);
		}
		catch (IOException ignored)
		{
		}
	}

	public List<Integer> load(long accountHash)
	{
		Snapshot snapshot = snapshot(accountHash);
		return snapshot == null || snapshot.itemIds == null ? null : new ArrayList<>(snapshot.itemIds);
	}

	public List<Integer> loadPotions(long accountHash)
	{
		Snapshot snapshot = snapshot(accountHash);
		if (snapshot == null || snapshot.potionIds == null)
		{
			return List.of();
		}
		return new ArrayList<>(snapshot.potionIds);
	}

	private Snapshot snapshot(long accountHash)
	{
		Path file = file(accountHash);
		if (file == null || !Files.isRegularFile(file))
		{
			return null;
		}
		try
		{
			Snapshot snapshot = gson.fromJson(Files.readString(file, StandardCharsets.UTF_8), Snapshot.class);
			if (snapshot == null || snapshot.accountHash != accountHash)
			{
				return null;
			}
			return snapshot;
		}
		catch (IOException ignored)
		{
			return null;
		}
	}

	public boolean exists(long accountHash)
	{
		Path file = file(accountHash);
		return file != null && Files.isRegularFile(file);
	}

	private Path file(long accountHash)
	{
		if (directory == null)
		{
			return null;
		}
		return directory.resolve("bank-" + accountHash + ".json");
	}

	private static final class Snapshot
	{
		@SerializedName("accountHash")
		private long accountHash;
		@SerializedName("itemIds")
		private List<Integer> itemIds;
		@SerializedName("potionIds")
		private List<Integer> potionIds;
	}
}
