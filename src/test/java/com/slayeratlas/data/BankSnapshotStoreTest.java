package com.slayeratlas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
}
