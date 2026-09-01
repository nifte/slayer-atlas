package com.slayeratlas.data;

import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

@Singleton
public final class SavedTaskLoadouts implements TaskLoadouts
{
	private final SavedLoadoutStore store;
	private final Client client;
	private final ClientThread clientThread;
	private final ItemManager itemManager;

	@Inject
	public SavedTaskLoadouts(
		SavedLoadoutStore store,
		Client client,
		ClientThread clientThread,
		ItemManager itemManager)
	{
		this.store = store;
		this.client = client;
		this.clientThread = clientThread;
		this.itemManager = itemManager;
	}

	@Override
	public GearLoadout load(String monsterId)
	{
		return store == null ? null : store.load(accountHash(), monsterId);
	}

	@Override
	public void save(String monsterId, GearLoadout loadout)
	{
		if (store != null)
		{
			store.save(accountHash(), monsterId, loadout);
		}
	}

	@Override
	public void clear(String monsterId)
	{
		if (store != null)
		{
			store.clear(accountHash(), monsterId);
		}
	}

	@Override
	public void captureCurrent(CombatStyle style, Consumer<GearLoadout> onCaptured)
	{
		if (onCaptured == null)
		{
			return;
		}
		Runnable publish = () ->
		{
			GearLoadout loadout = PlayerLoadouts.fromClient(client, itemManager, style);
			if (SwingUtilities.isEventDispatchThread())
			{
				onCaptured.accept(loadout);
				return;
			}
			SwingUtilities.invokeLater(() -> onCaptured.accept(loadout));
		};
		if (clientThread == null)
		{
			publish.run();
			return;
		}
		clientThread.invoke(publish);
	}

	private long accountHash()
	{
		return client == null ? 0L : client.getAccountHash();
	}
}
