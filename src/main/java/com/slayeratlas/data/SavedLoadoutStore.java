package com.slayeratlas.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.RuneLite;

@Singleton
public final class SavedLoadoutStore
{
	private final Path directory;
	private final Gson gson;

	@Inject
	public SavedLoadoutStore(Gson gson)
	{
		this(RuneLite.RUNELITE_DIR.toPath().resolve("slayer-atlas"), gson);
	}

	public SavedLoadoutStore(Path directory, Gson gson)
	{
		this.directory = directory;
		this.gson = gson;
	}

	public synchronized void save(long accountHash, String monsterId, GearLoadout loadout)
	{
		if (monsterId == null || monsterId.isEmpty() || loadout == null || gson == null)
		{
			return;
		}
		AccountLoadouts account = read(accountHash);
		account.accountHash = accountHash;
		account.loadouts.put(monsterId, record(loadout));
		write(accountHash, account);
	}

	public synchronized GearLoadout load(long accountHash, String monsterId)
	{
		if (monsterId == null || monsterId.isEmpty())
		{
			return null;
		}
		return fromRecord(read(accountHash).loadouts.get(monsterId));
	}

	public synchronized void clear(long accountHash, String monsterId)
	{
		if (monsterId == null || monsterId.isEmpty())
		{
			return;
		}
		AccountLoadouts account = read(accountHash);
		if (account.loadouts.remove(monsterId) == null)
		{
			return;
		}
		if (account.loadouts.isEmpty())
		{
			delete(accountHash);
			return;
		}
		write(accountHash, account);
	}

	public synchronized boolean exists(long accountHash, String monsterId)
	{
		return load(accountHash, monsterId) != null;
	}

	private AccountLoadouts read(long accountHash)
	{
		Path file = file(accountHash);
		AccountLoadouts empty = new AccountLoadouts();
		empty.accountHash = accountHash;
		empty.loadouts = new LinkedHashMap<>();
		if (file == null || !Files.isRegularFile(file) || gson == null)
		{
			return empty;
		}
		try
		{
			AccountLoadouts parsed = gson.fromJson(Files.readString(file, StandardCharsets.UTF_8), AccountLoadouts.class);
			if (parsed == null || parsed.accountHash != accountHash)
			{
				return empty;
			}
			if (parsed.loadouts == null)
			{
				parsed.loadouts = new LinkedHashMap<>();
			}
			return parsed;
		}
		catch (RuntimeException | IOException ignored)
		{
			return empty;
		}
	}

	private void write(long accountHash, AccountLoadouts account)
	{
		if (directory == null || gson == null)
		{
			return;
		}
		try
		{
			Files.createDirectories(directory);
			Files.writeString(file(accountHash), gson.toJson(account), StandardCharsets.UTF_8);
		}
		catch (IOException ignored)
		{
		}
	}

	private void delete(long accountHash)
	{
		Path file = file(accountHash);
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

	private Path file(long accountHash)
	{
		if (directory == null)
		{
			return null;
		}
		return directory.resolve("loadouts-" + accountHash + ".json");
	}

	private static SavedLoadout record(GearLoadout loadout)
	{
		SavedLoadout record = new SavedLoadout();
		record.style = loadout.getStyle() == null ? CombatStyle.MELEE.name() : loadout.getStyle().name();
		record.worn = new LinkedHashMap<>();
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if (!slot.onWornGrid())
			{
				continue;
			}
			GearItem item = loadout.worn(slot);
			if (item != null && item.getName() != null && !item.getName().isEmpty())
			{
				record.worn.put(slot.name(), item.getName());
			}
		}
		record.inventory = new ArrayList<>();
		if (loadout.getInventory() != null)
		{
			for (GearItem item : loadout.getInventory())
			{
				record.inventory.add(item == null || item.getName() == null || item.getName().isEmpty()
					? null
					: item.getName());
			}
		}
		record.prayers = new ArrayList<>();
		if (loadout.getPrayers() != null)
		{
			for (String prayer : loadout.getPrayers())
			{
				if (prayer != null && !prayer.isEmpty())
				{
					record.prayers.add(prayer);
				}
			}
		}
		return record;
	}

	private static GearLoadout fromRecord(SavedLoadout record)
	{
		if (record == null)
		{
			return null;
		}
		CombatStyle style = CombatStyle.MELEE;
		if (record.style != null)
		{
			try
			{
				style = CombatStyle.valueOf(record.style);
			}
			catch (IllegalArgumentException ignored)
			{
				style = CombatStyle.MELEE;
			}
		}
		Map<EquipmentSlot, String> worn = new EnumMap<>(EquipmentSlot.class);
		if (record.worn != null)
		{
			for (Map.Entry<String, String> entry : record.worn.entrySet())
			{
				EquipmentSlot slot = slotNamed(entry.getKey());
				if (slot != null)
				{
					worn.put(slot, entry.getValue());
				}
			}
		}
		return PlayerLoadouts.named(style, worn, record.inventory, record.prayers);
	}

	private static EquipmentSlot slotNamed(String name)
	{
		if (name == null || name.isEmpty())
		{
			return null;
		}
		try
		{
			return EquipmentSlot.valueOf(name);
		}
		catch (IllegalArgumentException ignored)
		{
			return null;
		}
	}

	private static final class AccountLoadouts
	{
		@SerializedName("accountHash")
		private long accountHash;
		@SerializedName("loadouts")
		private Map<String, SavedLoadout> loadouts;
	}

	private static final class SavedLoadout
	{
		@SerializedName("style")
		private String style;
		@SerializedName("worn")
		private Map<String, String> worn;
		@SerializedName("inventory")
		private List<String> inventory;
		@SerializedName("prayers")
		private List<String> prayers;
	}
}
