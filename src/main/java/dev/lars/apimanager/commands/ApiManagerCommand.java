package dev.lars.apimanager.commands;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.ConnectDatabase;
import dev.lars.apimanager.database.DatabaseManager;
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

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] args) {
        var sender = stack.getSender();

        boolean allowed = sender.isOp() || sender.hasPermission("apimanager.reload") || sender instanceof org.bukkit.command.ConsoleCommandSender;
        if (!allowed) {
            sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("You aren't allowed to execute this command!", NamedTextColor.RED)));
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String sub = args[0];
        if (sub.equalsIgnoreCase("test") || sub.equalsIgnoreCase("t")) {
            handleTest(sender);
            return;
        } else if (sub.equalsIgnoreCase("reload") || sub.equalsIgnoreCase("rl")) {
            plugin.reloadConfig();

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
            if (args.length < 2) {
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Invalid action. Use: enable, disable, or status", NamedTextColor.RED)));
                return;
            }
            String action = args[1].toLowerCase();
            handleLogging(sender, action);
            return;
        } else if (sub.equalsIgnoreCase("version") || sub.equalsIgnoreCase("v")) {
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
        } else if(sub.equalsIgnoreCase("status") || sub.equalsIgnoreCase("s")) {
            handleStatus(sender);
            return;
        }
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Unknown command!", NamedTextColor.RED)));
        sendUsage(sender);
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

            long updateStart = System.currentTimeMillis();
            dbm.update("""
                INSERT INTO apimanager_test (id, test_value) VALUES (1, ?)
                ON DUPLICATE KEY UPDATE test_value = VALUES(test_value)
            """, "Test successful at " + Instant.now());
            long updateTime = System.currentTimeMillis() - updateStart;

            long queryStart = System.currentTimeMillis();
            String result = dbm.query(conn -> {
                try (var ps = conn.prepareStatement("SELECT test_value FROM apimanager_test WHERE id = 1")) {
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("test_value");
                        }
                        return null;
                    }
                }
            });
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

    private void handleLogging(CommandSender sender, String action) {
        switch (action) {
            case "enable" -> {
                ApiManager.getInstance().getDatabaseManager().setSqlLogging(true);
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("SQL query logging enabled", NamedTextColor.GREEN)));
            }
            case "disable" -> {
                ApiManager.getInstance().getDatabaseManager().setSqlLogging(false);
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("SQL query logging disabled", NamedTextColor.GRAY)));
            }
            case "status" -> {
                boolean enabled = ApiManager.getInstance().getDatabaseManager().isSqlLoggingEnabled();
                String status = enabled ? "enabled" : "disabled";
                NamedTextColor color = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("SQL query logging is currently " + status, color)));
            }
            default -> {
                sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("Invalid action. Use: enable, disable, or status", NamedTextColor.RED)));
            }
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
            sqlLogging = real.isSqlLoggingEnabled();

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
            .append(Component.text(sqlLogging ? "Enabled" : "Disabled",
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
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager logging enable", NamedTextColor.GOLD))
            .append(Component.text(" - Enable SQL query logging", NamedTextColor.GRAY)));
        sender.sendMessage(ApiManagerStatements.getPrefix().append(Component.text("/apimanager logging disable", NamedTextColor.GOLD))
            .append(Component.text(" - Disable SQL query logging", NamedTextColor.GRAY)));
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
        if (args.length == 2 && args[0].equalsIgnoreCase("logging") || args.length == 3 && args[0].equalsIgnoreCase("logging")
        || args.length == 2 && args[0].equalsIgnoreCase("l") || args.length == 3 && args[0].equalsIgnoreCase("l")) {
            return List.of("enable", "disable", "status");
        }
        return Collections.emptyList();
    }
}