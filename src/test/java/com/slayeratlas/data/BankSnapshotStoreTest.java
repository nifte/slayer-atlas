package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class BankSnapshotStoreTest
{
	@Test
	public void savesAndLoadsASnapshotForOneAccount() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-bank");
		BankSnapshotStore store = new BankSnapshotStore(directory, new Gson());
		store.save(111L, List.of(4151, 11802));
		assertTrue(store.exists(111L));
		assertEquals(List.of(4151, 11802), store.load(111L));
	}

	@Test
	public void doesNotShareSnapshotsAcrossAccounts() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-bank");
		BankSnapshotStore store = new BankSnapshotStore(directory, new Gson());
		store.save(111L, List.of(4151));
		assertNull(store.load(222L));
		assertFalse(store.exists(222L));
		assertEquals(List.of(4151), store.load(111L));
	}

	@Test
	public void savesAndLoadsPotionStorageIdsWithTheBankSnapshot() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-bank");
		BankSnapshotStore store = new BankSnapshotStore(directory, new Gson());
		store.save(111L, List.of(4151), List.of(2434));
		assertEquals(List.of(4151), store.load(111L));
		assertEquals(List.of(2434), store.loadPotions(111L));
	}

	@Test
	public void treatsMissingPotionIdsAsEmpty() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-bank");
		BankSnapshotStore store = new BankSnapshotStore(directory, new Gson());
		store.save(111L, List.of(4151));
		assertEquals(List.of(), store.loadPotions(111L));
	}

	@Test
	public void savesAndLoadsLastEquippedVariantsWithTheBankSnapshot() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-bank");
		BankSnapshotStore store = new BankSnapshotStore(directory, new Gson());
		store.save(
			111L,
			List.of(4151),
			List.of(2434),
			Map.of("slayer helmet (i)", "Black slayer helmet (i)"));
		assertEquals(List.of(4151), store.load(111L));
		assertEquals(List.of(2434), store.loadPotions(111L));
		assertEquals(
			Map.of("slayer helmet (i)", "Black slayer helmet (i)"),
			store.loadLastEquipped(111L));
	}

	@Test
	public void treatsMissingLastEquippedAsEmpty() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-bank");
		BankSnapshotStore store = new BankSnapshotStore(directory, new Gson());
		store.save(111L, List.of(4151));
		assertEquals(Map.of(), store.loadLastEquipped(111L));
	}

	@Test
	public void keepsLastEquippedWhenSavingBankIdsAlone() throws Exception
	{
		Path directory = Files.createTempDirectory("slayer-atlas-bank");
		BankSnapshotStore store = new BankSnapshotStore(directory, new Gson());
		store.save(
			111L,
			List.of(4151),
			List.of(),
			Map.of("imbued god cape", "Imbued Guthix cape"));
		store.save(111L, List.of(4151, 11802));
		assertEquals(List.of(4151, 11802), store.load(111L));
		assertEquals(
			Map.of("imbued god cape", "Imbued Guthix cape"),
			store.loadLastEquipped(111L));
	}
}
