package dev.lars.apimanager.commands;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.ConnectDatabase;
import dev.lars.apimanager.database.DatabaseManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerStatements;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public record ApiManagerCommand(ApiManager plugin, ConnectDatabase connectDatabase) implements BasicCommand {

    private static final String TEST_TABLE = "apimanager_test";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] args) {
        var sender = stack.getSender();

        if (args.length == 0) {
            if (hasNotPermission(sender, "apimanager.status")) {
                sendNoPermission(sender, "apimanager.status");
                return;
            }
            sendUsage(sender);
            return;
        }

        String sub = args[0];
        if (sub.equalsIgnoreCase("test") || sub.equalsIgnoreCase("t")) {
            if (hasNotPermission(sender, "apimanager.test")) {
                sendNoPermission(sender, "apimanager.test");
                return;
            }
            handleTest(sender);
            return;
        } else if (sub.equalsIgnoreCase("reload") || sub.equalsIgnoreCase("rl")) {
            if (hasNotPermission(sender, "apimanager.reload")) {
                sendNoPermission(sender, "apimanager.reload");
                return;
            }

            boolean success;
            try {
                success = connectDatabase.loadDatabaseConfig();
            } catch (Exception e) {
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Error while loading DB config: " + e.getMessage(), NamedTextColor.RED)));
                return;
            }

            if (!success) {
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Error: Invalid database configuration!", NamedTextColor.RED)));
                return;
            }

            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Connecting to database...", NamedTextColor.GRAY)));

            IDatabaseManager dbm = ApiManager.getInstance().getDatabaseManager();
            if (dbm instanceof DatabaseManager real) {
                real.readyFuture().thenRun(() -> Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        ApiManager.getInstance().createAllTables();
                        ApiManager.getInstance().onApisReady();
                        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Database configuration successfully reloaded!", NamedTextColor.GREEN)));
                    } catch (Exception e) {
                        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Failed to reinitialize APIs: " + e.getMessage(), NamedTextColor.RED)));
                    }
                }));
            } else {
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("No real database connected; running in safe mode.", NamedTextColor.GOLD)));
            }
            return;
        } else if (sub.equalsIgnoreCase("logging") || sub.equalsIgnoreCase("l")) {
            if (hasNotPermission(sender, "apimanager.log")) {
                sendNoPermission(sender, "apimanager.log");
                return;
            }

            if (args.length < 2) {
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Invalid action. Usage: /am logging enable (<duration in ms>), disable, status", NamedTextColor.RED)));
                return;
            }
            handleLogging(sender, args);
            return;
        } else if (sub.equalsIgnoreCase("version") || sub.equalsIgnoreCase("v")) {
            if (hasNotPermission(sender, "apimanager.version")) {
                sendNoPermission(sender, "apimanager.version");
                return;
            }

            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("=== " + plugin.getName() + " Version ===", NamedTextColor.AQUA)));
            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Version ", NamedTextColor.GRAY))
                .append(Component.text("v" + plugin.getVersion(), NamedTextColor.GOLD)));
            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Api Version ", NamedTextColor.GRAY))
                .append(Component.text(plugin.getApiVersion(), NamedTextColor.GOLD)));
            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Developer ", NamedTextColor.GRAY))
                .append(Component.text(String.join(", ", plugin.getDevelopers()), NamedTextColor.GOLD)));
            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Website ", NamedTextColor.GRAY))
                .append(Component.text(plugin.getWebsite(), NamedTextColor.GOLD)));
            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Command ", NamedTextColor.GRAY))
                .append(Component.text("/" + plugin.getName() + " /am", NamedTextColor.GOLD)));
            return;
        } else if (sub.equalsIgnoreCase("status") || sub.equalsIgnoreCase("s")) {
            if (hasNotPermission(sender, "apimanager.status")) {
                sendNoPermission(sender, "apimanager.status");
                return;
            }
            handleStatus(sender);
            return;
        }

        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Unknown command!", NamedTextColor.RED)));
        sendUsage(sender);
    }

    private boolean hasNotPermission(CommandSender sender, String permission) {
        return !(sender instanceof org.bukkit.command.ConsoleCommandSender)
                && !sender.isOp()
                && !sender.hasPermission(permission)
                && !sender.hasPermission("apimanager.*");
    }

    private void sendNoPermission(CommandSender sender, String permission) {
        sender.sendMessage(ApiManagerStatements.getPrefix().append(
            Component.text("You aren't allowed to execute this command! Missing permission: " + permission, NamedTextColor.RED)
        ));
    }

    private void handleTest(CommandSender sender) {
        IDatabaseManager dbm = ApiManager.getInstance().getDatabaseManager();

        if (!(dbm instanceof DatabaseManager)) {
            sender.sendMessage(ApiManagerStatements.getPrefix()
                .append(Component.text("Database is not connected. Cannot perform test.", NamedTextColor.RED)));
            return;
        }

        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("Running database test...", NamedTextColor.GRAY)));

        long startTime = System.currentTimeMillis();

        try {
            dbm.update("""
                CREATE TABLE IF NOT EXISTS apimanager_test (
                    id INT PRIMARY KEY,
                    test_value VARCHAR(255),
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """);

            String testValue = "Test successful at " + Instant.now();

            long updateStart = System.currentTimeMillis();
            repo().insertIgnore(TEST_TABLE,
                new String[]{"id", "test_value"},
                1, testValue);
            repo().updateColumn(TEST_TABLE, "test_value", testValue, "id = ?", 1);
            long updateTime = System.currentTimeMillis() - updateStart;

            long queryStart = System.currentTimeMillis();
            String result = repo().getString(TEST_TABLE, "test_value", "id = ?", 1);
            long queryTime = System.currentTimeMillis() - queryStart;

            long totalTime = System.currentTimeMillis() - startTime;

            sender.sendMessage(ApiManagerStatements.getPrefix()
                .append(Component.text("=== Database Test Results ===", NamedTextColor.AQUA)));
            sender.sendMessage(ApiManagerStatements.getPrefix()
                .append(Component.text("Status: ", NamedTextColor.GRAY))
                .append(Component.text("PASSED", NamedTextColor.GREEN)));
            sender.sendMessage(ApiManagerStatements.getPrefix()
                .append(Component.text("Update time: ", NamedTextColor.GRAY))
                .append(Component.text(updateTime + "ms", NamedTextColor.GOLD)));
            sender.sendMessage(ApiManagerStatements.getPrefix()
                .append(Component.text("Query time: ", NamedTextColor.GRAY))
                .append(Component.text(queryTime + "ms", NamedTextColor.GOLD)));
            sender.sendMessage(ApiManagerStatements.getPrefix()
                .append(Component.text("Total time: ", NamedTextColor.GRAY))
                .append(Component.text(totalTime + "ms", NamedTextColor.GOLD)));

            if (result != null) {
                sender.sendMessage(ApiManagerStatements.getPrefix()
                    .append(Component.text("Retrieved value: ", NamedTextColor.GRAY))
                    .append(Component.text(result, NamedTextColor.YELLOW)));
            }

        } catch (Exception e) {
            sender.sendMessage(ApiManagerStatements.getPrefix()
                .append(Component.text("Database test FAILED!", NamedTextColor.RED)));
            sender.sendMessage(ApiManagerStatements.getPrefix()
                .append(Component.text("Error: " + e.getMessage(), NamedTextColor.RED)));
            ApiManagerStatements.logToConsole("Database test failed: " + e.getMessage(), NamedTextColor.RED);
        }
    }

    private void handleLogging(CommandSender sender, String[] args) {
        String action = args[1].toLowerCase();

        switch (action) {
            case "enable" -> {
                long durationMs = 0;
                if (args.length >= 3) {
                    try {
                        durationMs = Long.parseLong(args[2]);
                        if (durationMs < 0) {
                            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Duration must be positive!", NamedTextColor.RED)));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Invalid duration: " + args[2], NamedTextColor.RED)));
                        return;
                    }
                }
                ApiManager.getInstance().getDatabaseManager().enableSqlLogging(sender, durationMs);
                if (durationMs > 0) {
                    sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("SQL logging enabled for " + durationMs + " ms!", NamedTextColor.GREEN)));
                } else {
                    sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("SQL query logging enabled", NamedTextColor.GREEN)));
                }
            }
            case "disable" -> {
                ApiManager.getInstance().getDatabaseManager().disableSqlLogging(sender);
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("SQL query logging disabled", NamedTextColor.GRAY)));
            }
            case "status" -> {
                boolean enabled = ApiManager.getInstance().getDatabaseManager().isSqlLoggingEnabled(sender);
                long remaining = ApiManager.getInstance().getDatabaseManager().getSqlLoggingTimeRemaining(sender);

                String status = enabled ? "enabled" : "disabled";
                NamedTextColor color = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;

                sender.sendMessage(ApiManagerStatements.getPrefix()
                    .append(Component.text("SQL query logging is currently " + status, color)));

                if (enabled && remaining > 0) {
                    long seconds = remaining / 1000;
                    sender.sendMessage(ApiManagerStatements.getPrefix()
                        .append(Component.text("Time remaining: " + seconds + " seconds", NamedTextColor.GRAY)));
                }
            }
            default -> sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Invalid action. Use: enable, disable, or status", NamedTextColor.RED)));
        }
    }

    private void handleStatus(CommandSender sender) {
        IDatabaseManager dbm = ApiManager.getInstance().getDatabaseManager();

        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("=== " + plugin.getName() + " Database Status ===", NamedTextColor.AQUA)));

        boolean connected = false;
        boolean sqlLogging = false;
        String jdbcUrl = "N/A";
        String username = "N/A";
        String poolSize = "N/A";
        String active = "N/A";
        String idle = "N/A";
        String waiting = "N/A";
        double[] qps = new double[] {0, 0};

        if (dbm instanceof DatabaseManager real) {
            connected = real.isReady();
            sqlLogging = real.isSqlLoggingEnabled(sender);

            if (connected) {
                var ds = real.getDataSource();
                jdbcUrl = ds.getJdbcUrl();
                username = ds.getUsername();
                poolSize = String.valueOf(ds.getMaximumPoolSize());

                var mx = ds.getHikariPoolMXBean();
                if (mx != null) {
                    active = mx.getActiveConnections() + "/" + mx.getTotalConnections();
                    idle = String.valueOf(mx.getIdleConnections());
                    waiting = String.valueOf(mx.getThreadsAwaitingConnection());
                }
                qps = real.getSmoothedQps();
            }
        }

        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("Connection: ", NamedTextColor.GRAY))
            .append(Component.text(connected ? "Connected" : "Not connected",
                connected ? NamedTextColor.GREEN : NamedTextColor.RED)));

        if (!connected) {
            sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("No active database connection. Check your credentials or network.",
                NamedTextColor.GRAY)));
            return;
        }

        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("JDBC URL: ", NamedTextColor.GRAY))
            .append(Component.text(jdbcUrl, NamedTextColor.GOLD)));
        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("User: ", NamedTextColor.GRAY))
            .append(Component.text(username, NamedTextColor.GOLD)));
        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("Max pool size: ", NamedTextColor.GRAY))
            .append(Component.text(poolSize, NamedTextColor.GOLD)));
        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("Active connections: ", NamedTextColor.GRAY))
            .append(Component.text(active, NamedTextColor.GOLD)));
        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("Idle connections: ", NamedTextColor.GRAY))
            .append(Component.text(idle, NamedTextColor.GOLD)));
        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("Threads waiting: ", NamedTextColor.GRAY))
            .append(Component.text(waiting, NamedTextColor.GOLD)));
        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("Queries/sec: ", NamedTextColor.GRAY))
            .append(Component.text(String.format("%.2f", qps[0]), NamedTextColor.GOLD))
            .append(Component.text(" | Updates/sec: ", NamedTextColor.GRAY))
            .append(Component.text(String.format("%.2f", qps[1]), NamedTextColor.GOLD)));

        sender.sendMessage(ApiManagerStatements.getPrefix()
            .append(Component.text("SQL Logging: ", NamedTextColor.GRAY))
            .append(Component.text(sqlLogging ? "Enabled (for you)" : "Disabled",
                sqlLogging ? NamedTextColor.GREEN : NamedTextColor.RED)));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("=== " + plugin.getName() + " Commands ===", NamedTextColor.AQUA)));
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager test", NamedTextColor.GOLD))
            .append(Component.text(" - Test database connection with query and update", NamedTextColor.GRAY)));
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager reload", NamedTextColor.GOLD))
            .append(Component.text(" - Reload configuration", NamedTextColor.GRAY)));
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager status", NamedTextColor.GOLD))
            .append(Component.text(" - Shows connection status of database", NamedTextColor.GRAY)));
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager logging enable (<duration in ms>)", NamedTextColor.GOLD))
            .append(Component.text(" - Enable SQL query and update logging", NamedTextColor.GRAY)));
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager logging disable", NamedTextColor.GOLD))
            .append(Component.text(" - Disable SQL query and update logging", NamedTextColor.GRAY)));
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager logging status", NamedTextColor.GOLD))
            .append(Component.text(" - Check logging status", NamedTextColor.GRAY)));
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager version", NamedTextColor.GOLD))
            .append(Component.text(" - Shows plugin version and development info", NamedTextColor.GRAY)));
    }

    @Override
    public @NotNull Collection<String> suggest(final @NotNull CommandSourceStack commandSourceStack, final String[] args) {
        if (args.length <= 1) {
            return List.of("test", "t", "reload", "rl", "logging", "l", "version", "v", "status", "s");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("logging")
        || args.length == 2 && args[0].equalsIgnoreCase("l")) {
            return List.of("enable", "disable", "status");
        }
        return Collections.emptyList();
    }
}