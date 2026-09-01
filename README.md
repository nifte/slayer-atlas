# Slayer Atlas

A [RuneLite](https://runelite.net) plugin that puts a full slayer task database at your fingertips.

When you receive a task, it shows information on that monster in the side panel so you can see where to kill it, how to get there, weaknesses, required items, recommended gear/inventory setups, and typical prayers. If you also have the [Shortest Path](https://runelite.net/plugin-hub/show/shortest-path) plugin, you can get a route to its location with one click.

## Features

- **Every standard slayer task** in a searchable side panel. Mark a task as favorite to pin it to the top of the list.
- **Open the panel on a new task** and select the matching monster when you receive a new assignment from a slayer master.
- **Search tasks** by monster name, alias, or combat style.
- **Travel directions** for each location: fairy rings, teleports, jewelry, etc.
- **Recommended gear, potions, required items, and attack styles** so you can easily prepare for your task. Save custom loadouts per task so you don't have to remember your preferred setups.
- **Bank tab button** to quickly show recommended gear/items for your current task.
- **Shortest Path integration** so you can path to a specific monster location from its atlas page. Optionally auto-path to new tasks (either Konar's assigned area or the nearest location).

## Configuration

Open **Configuration → Slayer Atlas**:

| Setting                         | Default | What it does                                                                                                                                                          |
| ------------------------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Open panel on new task          | On      | Automatically opens your task in the side panel when a new task is assigned.                                                                                          |
| Use Shortest Path plugin        | On      | Shows path buttons for monster locations and talks to Shortest Path when that plugin is installed and enabled.                                                        |
| Auto path on new task           | Off     | Automatically start a Shortest Path route when a new task is assigned: Konar's assigned location if on a Konar task, or the nearest location otherwise.               |
| Only recommend owned equipment  | On      | Only recommend equipment you have in the bank/inventory. Recommends the best owned alternative in each slot from wiki/BIS lists. Must open bank at least once to use. |
| Only recommend unlocked prayers | On      | Only recommend prayers you have unlocked. If certain prayers are unavailable, it will recommend lower level alternatives.                                             |

If Shortest Path is not installed and enabled, path buttons/features are disabled.

## Using it in-game

1. Enable **Slayer Atlas** (and optionally **Shortest Path**) in the plugin list.
2. Click the Slayer Atlas icon on the sidebar.
3. Get a task, or search for a monster.
