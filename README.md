# Slayer Atlas

A [RuneLite](https://runelite.net) plugin that puts a full Old School RuneScape Slayer assignment database in the client sidebar.

When you receive a task, the panel can jump to that monster automatically. Each entry covers where to kill it, how to get there, weaknesses, required items, recommended gear and potions, and typical protection. If you also run [Shortest Path](https://runelite.net/plugin-hub/show/shortest-path), you can send a route to a location with one click.

## Features

- **Every standard Slayer assignment** in a searchable side panel (low-level Turael tasks through hydras, kraken, smoke devils, and newer Varlamore / Sailing creatures).
- **Open the panel on a new task** and select the matching monster when RuneLite's Slayer plugin updates your assignment, including Konar's location.
- **Manual search** by monster name, alias (for example `nechs`, `abby demons`, `steel dragons`), or combat style.
- **Travel directions** for each spot: fairy rings, slayer rings, glory, Xeric's talisman, burning amulet, and similar.
- **Gear, potions, required items, and attack style** so you can bank before you leave.
- **Shortest Path integration** via the public `PluginMessage` API (no reflection). Path to a specific cave, or to the nearest listed location. Konar's assigned area is preferred when present.

## Configuration

Open **Configuration → Slayer Atlas**:

| Setting                         | Default | What it does                                                                                                                                                                                                       |
| ------------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Open panel on new task          | On      | Selects the matching monster and opens the side panel when a new task is assigned.                                                                                                                                 |
| Use Shortest Path plugin        | On      | Shows Path buttons and talks to Shortest Path when that plugin is installed and enabled.                                                                                                                           |
| Path on new task                | Off     | Also starts a route when a new task is assigned.                                                                                                                                                                   |
| Only recommend owned equipment  | On      | After this account has opened a bank at least once, recommend the best owned alternative in each slot from the wiki loadout or the style/attribute BIS ladder. Never recommends an item this account does not own. |
| Only recommend unlocked prayers | On      | Recommend the best combat prayer this account can use. If Rigour, Augury, or Piety is locked, fall back to the next lower prayer of the same style.                                                                |

If Shortest Path is not installed, Path buttons stay disabled and explain why.

## Running locally

You need **JDK 11** (the plugin is compiled to Java 11). Gradle uses JDK 11 even if `JAVA_HOME` is a newer JDK, as long as 11 is installed. Then:

```bash
./gradlew run
```

That launches RuneLite in developer mode with this plugin loaded. If you use a Jagex account, follow [Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

Useful Gradle tasks:

```bash
./gradlew test          # unit tests for the monster database
./gradlew shadowJar     # fat jar for a local sidecar client
```

Rebuild the bundled database after editing `scripts/generate_monsters.py`:

```bash
python3 scripts/generate_monsters.py
```

## Using it in-game

1. Enable **Slayer Atlas** (and optionally **Shortest Path**) in the plugin list.
2. Click the slayer-helm-and-book icon on the sidebar.
3. Get a task, or search for a monster.
4. Open a location card and press **Path here** if Shortest Path is running.

Check your task with a slayer gem, helm, or by talking to a master so RuneLite can sync the assignment.

## Tests to try

- Search `dust`, `nech`, `steel dragon`, and `wyrm` and confirm the right monsters appear.
- With open-panel-on-new-task on, take a new task and confirm the panel opens on that monster.
- Turn open-panel-on-new-task off, take a new task, and confirm the panel does not change by itself.
- With Shortest Path installed, path to Catacombs of Kourend from Aberrant spectres.
- Disable Shortest Path integration and confirm Path buttons disable instead of sending a route.
- Finish or skip a task and confirm the banner returns to "No Slayer task".
- With only-recommend-unlocked-prayers on, confirm a ranged task shows Eagle Eye (or Deadeye) instead of Rigour when the dexterous scroll is not unlocked.

## Project layout

- `src/main/java/com/slayeratlas` — plugin, config, and side panel
- `src/main/java/com/slayeratlas/data` — monster database and task matching
- `src/main/java/com/slayeratlas/path` — Shortest Path `PluginMessage` helper
- `src/main/resources/com/slayeratlas/data/monsters.json` — generated assignment data
- `scripts/generate_monsters.py` — regenerates JSON
- `icon.png` — Plugin Hub icon (also copied to `src/main/resources/com/slayeratlas/icon.png` for the sidebar)

Coordinates and travel notes follow the [OSRS Wiki](https://oldschool.runescape.wiki/) and may lag behind brand-new landings. Wiki buttons on each monster stay up to date.
