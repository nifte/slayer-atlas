# Slayer Atlas

A [RuneLite](https://runelite.net) plugin that puts a full Old School RuneScape Slayer assignment database in the client sidebar.

When you receive a task, the panel can jump to that monster automatically. Each entry covers where to kill it, how to get there, weaknesses, required items, recommended gear and potions, and typical protection. If you also run [Shortest Path](https://runelite.net/plugin-hub/show/shortest-path), you can send a route to a location with one click.

## Features

- **Every standard Slayer assignment** in a searchable side panel (low-level Turael tasks through hydras, kraken, smoke devils, and newer Varlamore / Sailing creatures).
- **Auto-select the current task** when RuneLite's Slayer plugin updates your assignment, including Konar's location.
- **Manual search** by monster name, alias (for example `nechs`, `abby demons`, `steel dragons`), or combat style.
- **Travel directions** for each spot: fairy rings, slayer rings, glory, Xeric's talisman, burning amulet, and similar.
- **Gear, potions, required items, and attack style** so you can bank before you leave.
- **Shortest Path integration** via the public `PluginMessage` API (no reflection). Path to a specific cave, or to the nearest listed location. Konar's assigned area is preferred when present.

## Configuration

Open **Configuration → Slayer Guide**:

| Setting | Default | What it does |
| --- | --- | --- |
| Auto-select current task | On | Selects the matching monster when your task changes or you log in. |
| Open panel on new task | On | Opens the side panel when a task is auto-selected. |
| Use Shortest Path plugin | On | Shows Path buttons and talks to Shortest Path when that plugin is installed and enabled. |
| Path on new task | Off | Also starts a route when a new task is auto-selected. |

If Shortest Path is not installed, Path buttons stay disabled and explain why.

## Running locally

You need **JDK 11+** (the plugin is compiled to Java 11). Then:

```bash
./gradlew run
```

That launches RuneLite in developer mode with this plugin loaded. If you use a Jagex account, follow [Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

Useful Gradle tasks:

```bash
./gradlew test          # unit tests for the monster database
./gradlew shadowJar     # fat jar for a local sidecar client
```

Rebuild the bundled database and icons after editing `scripts/generate_monsters.py`:

```bash
python3 scripts/generate_monsters.py
```

## Using it in-game

1. Enable **Slayer Guide** (and optionally **Shortest Path**) in the plugin list.
2. Click the horned-helm icon on the sidebar.
3. Get a task, or search for a monster.
4. Open a location card and press **Path here** if Shortest Path is running.

Check your task with a slayer gem, helm, or by talking to a master so RuneLite can sync the assignment.

## Tests to try

- Search `dust`, `nech`, `steel dragon`, and `wyrm` and confirm the right monsters appear.
- With auto-select on, take a new task and confirm the panel switches (and opens, if that option is on).
- Turn auto-select off, take a new task, and confirm the panel does not change by itself.
- With Shortest Path installed, path to Catacombs of Kourend from Aberrant spectres.
- Disable Shortest Path integration and confirm Path buttons disable instead of sending a route.
- Finish or skip a task and confirm the banner returns to "No Slayer task".

## Project layout

- `src/main/java/com/slayerguide` — plugin, config, and side panel
- `src/main/java/com/slayerguide/data` — monster database and task matching
- `src/main/java/com/slayerguide/path` — Shortest Path `PluginMessage` helper
- `src/main/resources/com/slayerguide/data/monsters.json` — generated assignment data
- `scripts/generate_monsters.py` — regenerates JSON and icons

Coordinates and travel notes follow the [OSRS Wiki](https://oldschool.runescape.wiki/) and may lag behind brand-new landings. Wiki buttons on each monster stay up to date.
