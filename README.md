# ApiManager

**ApiManager** is a Paper plugin that serves as a shared MariaDB database layer for other plugins on your server. Instead of every plugin managing its own connection pool and database logic, they all depend on ApiManager — which provides a single HikariCP connection pool, a rich `DatabaseRepository` helper API, and 21 pre-built domain-specific APIs (player data, economy, bans, ranks, and more).

> **Version:** 4.6.0 | **Author:** LarsEsDoch | **Paper API:** 1.21.11 | **Java:** 21

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Built-in APIs](#built-in-apis)
- [Commands](#commands)
- [Permissions](#permissions)
- [Safe Mode](#safe-mode)
- [Building](#building)
- [For Developers](#for-developers)

---

## Features

- **Single shared connection pool** via HikariCP — no redundant connections from multiple plugins
- **Async-first design** — every database operation has both a sync and a `CompletableFuture`-based async variant
- **21 domain APIs** — ready-to-use, database-backed APIs for common server mechanics
- **Safe mode** — if the database is unreachable or misconfigured, the plugin starts in a no-op safe mode so dependent plugins don't crash
- **Live SQL logging** — enable per-operator query logging with a configurable duration for debugging
- **QPS monitoring** — smoothed queries-per-second tracking visible in `/apimanager status`
- **SQL injection protection** — all table and column identifiers are validated through `SqlIdentifierValidator`
- **Hot reload** — reconnect to the database at runtime without restarting the server via `/apimanager reload`
- **Shadowed dependencies** — HikariCP and MariaDB client are relocated into the JAR, preventing classpath conflicts with other plugins

---

## Requirements

- **Paper** 1.21.11 or compatible
- **Java** 21+
- A running **MariaDB** (or MySQL) server accessible from the Minecraft server

---

## Installation

1. Download or build `ApiManager.jar` (see [Building](#building)).
2. Place the JAR in your server's `plugins/` folder.
3. Start the server once to generate `plugins/ApiManager/config.yml`.
4. Fill in your database credentials (see [Configuration](#configuration)).
5. Restart the server (or run `/apimanager reload` if credentials were added after first boot).

---

## Configuration

`plugins/ApiManager/config.yml`

```yaml
database:
  host: "127.0.0.1"        # IP or hostname of your MariaDB server
  port: 3306               # Default MariaDB/MySQL port
  database: "my_database"  # Name of the schema/database
  username: "my_user"      # Database user
  password: "my_password"  # Database password
```

If any value is left as a placeholder (or the host is unreachable), the plugin will start in **safe mode** and log a warning to the console. No plugin that depends on ApiManager will crash — all API calls will silently no-op.

---

## Built-in APIs

ApiManager instantiates and manages the lifecycle of 21 domain APIs. Each API creates its own table(s) on startup and exposes sync + async methods. All APIs are accessible statically after `onApisReady()` fires.

| API                     | Static Access                    | Description                                   |
|-------------------------|----------------------------------|-----------------------------------------------|
| `ServerStateAPI`        | `ServerStateAPI.getApi()`        | Tracks whether the server is currently online |
| `ServerFeatureAPI`      | `ServerFeatureAPI.getApi()`      | Feature flag management per server            |
| `MaintenanceAPI`        | `MaintenanceAPI.getApi()`        | Maintenance mode state                        |
| `ProgressionAPI`        | `ProgressionAPI.getApi()`        | Player progression tracking                   |
| `PlayerAPI`             | `PlayerAPI.getApi()`             | Core player registry (UUID-based)             |
| `ChunkAPI`              | `ChunkAPI.getApi()`              | Chunk claim / ownership data                  |
| `HomeAPI`               | `HomeAPI.getApi()`               | Player home locations                         |
| `LanguageAPI`           | `LanguageAPI.getApi()`           | Per-player language preferences               |
| `BackpackAPI`           | `BackpackAPI.getApi()`           | Persistent backpack storage                   |
| `LimitAPI`              | `LimitAPI.getApi()`              | Per-player configurable limits                |
| `BanAPI`                | `BanAPI.getApi()`                | Ban records (reason, expiry, timestamp)       |
| `CourtAPI`              | `CourtAPI.getApi()`              | Court / moderation case management            |
| `RankAPI`               | `RankAPI.getApi()`               | Player rank assignment                        |
| `PrefixAPI`             | `PrefixAPI.getApi()`             | Player chat prefix storage                    |
| `StatusAPI`             | `StatusAPI.getApi()`             | Custom player status messages                 |
| `PlayerIdentityAPI`     | `PlayerIdentityAPI.getApi()`     | Player identity / display name data           |
| `PlayerSettingsAPI`     | `PlayerSettingsAPI.getApi()`     | Persistent per-player settings                |
| `ScoreboardSettingsAPI` | `ScoreboardSettingsAPI.getApi()` | Scoreboard visibility preferences             |
| `EconomyAPI`            | `EconomyAPI.getApi()`            | Player balance and economy operations         |
| `QuestAPI`              | `QuestAPI.getApi()`              | Quest progress and completion tracking        |
| `TimerAPI`              | `TimerAPI.getApi()`              | Server-side timer persistence                 |

---

## Commands

All commands are registered under `/apimanager` and the alias `/am`.

| Command                     | Permission              | Description                                                                         |
|-----------------------------|-------------------------|-------------------------------------------------------------------------------------|
| `/am`                       | `apimanager.status`     | Shows command usage and current status                                              |
| `/am test` / `/am t`        | `apimanager.test`       | Runs a live query and update against a test table to verify the connection          |
| `/am reload` / `/am rl`     | `apimanager.reload`     | Reloads `config.yml`, reconnects to the database, and re-initialises all API tables |
| `/am status` / `/am s`      | `apimanager.status`     | Shows connection state, JDBC URL, pool stats (active/idle/waiting), and QPS         |
| `/am logging enable <ms>`   | `apimanager.log`        | Enables SQL query logging for the given duration in milliseconds                    |
| `/am logging disable`       | `apimanager.log`        | Disables SQL query logging                                                          |
| `/am logging status`        | `apimanager.log`        | Shows whether logging is active and the time remaining                              |
| `/am version` / `/am v`     | `apimanager.version`    | Displays plugin version, author, and website                                        |
| `/am playerinfo` / `/am pi` | `apimanager.playerinfo` | Shows player data and registration status                                           |

---

## Permissions

| Permission               | Default | Description                              |
|--------------------------|---------|------------------------------------------|
| `apimanager.*`           | OP      | Grants all ApiManager permissions        |
| `apimanager.reload`      | OP      | Reload configuration and reconnect to DB |
| `apimanager.test`        | OP      | Run the database connection test         |
| `apimanager.log`         | OP      | Enable/disable/check SQL logging         |
| `apimanager.status`      | OP      | View database connection status          |
| `apimanager.version`     | OP      | View version information                 |
| `apimanager.playerinfo`  | OP      | View player information                  |

---

## Safe Mode

If ApiManager cannot connect to the database on startup (wrong credentials, host unreachable, or placeholder config values), it automatically switches to a **SafeDatabaseManager**. In safe mode:

- All `query()` and `update()` calls are silently swallowed (no exception thrown)
- All async methods return immediately with `null` / `CompletableFuture.completedFuture(null)`
- Console warnings are logged for every skipped operation
- The server and any dependent plugins continue to run without crashing
- You can resolve the configuration and run `/am reload` to reconnect live, without restarting

---

## Building

The project uses **Gradle** with the **Shadow** plugin to produce a fat JAR with all dependencies relocated.

```bash
./gradlew shadowJar
```

The output JAR is placed in `build/libs/`. It already includes and relocates:

| Dependency                                   | Relocated to              |
|----------------------------------------------|---------------------------|
| `com.zaxxer:HikariCP:5.1.0`                  | `de.lars.shadow.hikaricp` |
| `org.mariadb.jdbc:mariadb-java-client:3.4.0` | `de.lars.shadow.mariadb`  |
| `org.slf4j` (transitive)                     | `de.lars.shadow.slf4j`    |

To run a local Paper test server directly:

```bash
./gradlew runServer
```

This spins up Paper 1.21.11 via `jpenilla/run-paper` with the plugin loaded automatically.

---

## For Developers

### Depending on ApiManager

Add ApiManager as a dependency in your own plugin's `paper-plugin.yml`:

```yaml
dependencies:
  server:
    ApiManager:
      load: BEFORE
      required: true
```

### Accessing an API

All APIs are available as static singletons after ApiManager fires `onApisReady()` (which happens asynchronously once the connection pool is ready). Wrap your logic in a check or listen to the server load order:

```java
// Sync example
boolean isBanned = BanAPI.getApi().isBanned(player);

// Async example
BanAPI.getApi().isBannedAsync(player).thenAccept(banned -> {
    if (banned) player.kick(Component.text("You are banned."));
});
```

### Using DatabaseRepository directly

If the built-in APIs don't cover your use case, you can use `DatabaseRepository` directly for type-safe, parameterised queries:

```java
DatabaseRepository repo = new DatabaseRepository();

// Simple lookup
String value = repo.getString("my_table", "my_column", "uuid = ?", player.getUniqueId().toString());

// Async insert
repo.insertAsync("my_table", new String[]{"uuid", "score"}, uuid.toString(), 100)
    .thenRun(() -> getLogger().info("Inserted!"));

// Exists check
boolean exists = repo.exists("my_table", "uuid = ?", uuid.toString());
```

Available `DatabaseRepository` methods include: `getString`, `getInt`, `getLong`, `getDouble`, `getBoolean`, `getInstant`, `getList`, `exists`, `count`, `insert`, `insertIgnore`, `updateColumn`, `updateColumns`, `increaseColumn`, `decreaseColumn`, `delete` — each available in sync and `Async` variants.

### Raw access via IDatabaseManager

For fully custom queries, use the `IDatabaseManager` directly:

```java
IDatabaseManager db = ApiManager.getInstance().getDatabaseManager();

// Raw update
db.update("INSERT INTO my_table (uuid) VALUES (?)", uuid.toString());

// Raw query
String result = db.query(conn -> {
    try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM my_table WHERE uuid = ?")) {
        ps.setString(1, uuid.toString());
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getString("name") : null;
    }
});
```

---

## Links

- **GitHub:** https://github.com/LarsEsDoch/ApiManager
