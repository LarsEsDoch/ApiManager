package dev.lars.apimanager.commands;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.ConnectDatabase;
import dev.lars.apimanager.database.DatabaseManager;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.Statements;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public record ApiManagerCommand(ApiManager plugin, ConnectDatabase connectDatabase) implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] args) {
        var sender = stack.getSender();

        boolean allowed = sender.isOp() || sender.hasPermission("apimanager.reload") || sender instanceof org.bukkit.command.ConsoleCommandSender;
        if (!allowed) {
            sender.sendMessage(Statements.getPrefix().append(Component.text("You aren't allowed to execute this command!", NamedTextColor.RED)));
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String sub = args[0];
        if (sub.equalsIgnoreCase("reload") || sub.equalsIgnoreCase("rl")) {
            plugin.reloadConfig();

            boolean success;
            try {
                success = connectDatabase.loadDatabaseConfig();
            } catch (Exception e) {
                sender.sendMessage(Statements.getPrefix().append(Component.text("Error while loading DB config: " + e.getMessage(), NamedTextColor.RED)));
                return;
            }

            if (!success) {
                sender.sendMessage(Statements.getPrefix().append(Component.text("Error: Invalid database configuration!", NamedTextColor.RED)));
                return;
            }

            sender.sendMessage(Statements.getPrefix().append(Component.text("Connecting to database...", NamedTextColor.GRAY)));

            IDatabaseManager dbm = ApiManager.getInstance().getDatabaseManager();
            if (dbm instanceof DatabaseManager real) {
                real.readyFuture().thenRun(() -> Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        sender.sendMessage(Statements.getPrefix().append(Component.text("Database configuration successfully reloaded!", NamedTextColor.GREEN)));
                    } catch (Exception e) {
                        sender.sendMessage(Statements.getPrefix().append(Component.text("Failed to reinitialize APIs: " + e.getMessage(), NamedTextColor.RED)));
                    }
                }));
            } else {
                sender.sendMessage(Statements.getPrefix().append(Component.text("No real database connected; running in safe mode.", NamedTextColor.GOLD)));
            }
            return;
        } else if (sub.equalsIgnoreCase("logging") || sub.equalsIgnoreCase("log")) {
            if (args.length < 2) {
                sender.sendMessage(Statements.getPrefix().append(Component.text("Invalid action. Use: enable, disable, or status", NamedTextColor.RED)));
                return;
            }
            String action = args[1].toLowerCase();
            handleLogging(sender, action);
            return;
        } else if (sub.equalsIgnoreCase("version") || sub.equalsIgnoreCase("v")) {
            sender.sendMessage(Statements.getPrefix().append(Component.text("=== " + plugin.getName() + " Version ===", NamedTextColor.AQUA)));
            sender.sendMessage(Statements.getPrefix().append(Component.text("Version ", NamedTextColor.GRAY))
                .append(Component.text("v" + plugin.getVersion(), NamedTextColor.GOLD)));
            sender.sendMessage(Statements.getPrefix().append(Component.text("Api Version ", NamedTextColor.GRAY))
                .append(Component.text(plugin.getApiVersion(), NamedTextColor.GOLD)));
            sender.sendMessage(Statements.getPrefix().append(Component.text("Developer ", NamedTextColor.GRAY))
                .append(Component.text(String.join(", ", plugin.getDevelopers()), NamedTextColor.GOLD)));
            sender.sendMessage(Statements.getPrefix().append(Component.text("Website ", NamedTextColor.GRAY))
                .append(Component.text(plugin.getWebsite(), NamedTextColor.GOLD)));
            sender.sendMessage(Statements.getPrefix().append(Component.text("Command ", NamedTextColor.GRAY))
                .append(Component.text("/" + plugin.getName() + "/am", NamedTextColor.GOLD)));
        }
        sender.sendMessage(Statements.getPrefix().append(Component.text("Unknown command!", NamedTextColor.RED)));
        sendUsage(sender);
    }

    private void handleLogging(CommandSender sender, String action) {
        switch (action) {
            case "enable" -> {
                ApiManager.getInstance().getDatabaseManager().setSqlLogging(true);
                sender.sendMessage(Statements.getPrefix().append(Component.text("SQL query logging enabled", NamedTextColor.GREEN)));
            }
            case "disable" -> {
                ApiManager.getInstance().getDatabaseManager().setSqlLogging(false);
                sender.sendMessage(Statements.getPrefix().append(Component.text("SQL query logging disabled", NamedTextColor.GRAY)));
            }
            case "status" -> {
                boolean enabled = ApiManager.getInstance().getDatabaseManager().isSqlLoggingEnabled();
                String status = enabled ? "enabled" : "disabled";
                NamedTextColor color = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
                sender.sendMessage(Statements.getPrefix().append(Component.text("SQL query logging is currently " + status, color)));
            }
            default -> {
                sender.sendMessage(Statements.getPrefix().append(Component.text("Invalid action. Use: enable, disable, or status", NamedTextColor.RED)));
            }
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Statements.getPrefix().append(Component.text("=== ApiManager Commands ===", NamedTextColor.AQUA)));
        sender.sendMessage(Statements.getPrefix().append(Component.text("/apimanager logging enable", NamedTextColor.GOLD))
            .append(Component.text(" - Enable SQL query logging", NamedTextColor.GRAY)));
        sender.sendMessage(Statements.getPrefix().append(Component.text("/apimanager logging disable", NamedTextColor.GOLD))
            .append(Component.text(" - Disable SQL query logging", NamedTextColor.GRAY)));
        sender.sendMessage(Statements.getPrefix().append(Component.text("/apimanager logging status", NamedTextColor.GOLD))
            .append(Component.text(" - Check logging status", NamedTextColor.GRAY)));
        sender.sendMessage(Statements.getPrefix().append(Component.text("/apimanager reload", NamedTextColor.GOLD))
            .append(Component.text(" - Reload configuration", NamedTextColor.GRAY)));
    }

    @Override
    public @NotNull Collection<String> suggest(final @NotNull CommandSourceStack commandSourceStack, final String[] args) {
        if (args.length <= 1) {
            return List.of("reload", "rl", "logging", "l", "version", "v");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("logging") || args.length == 3 && args[0].equalsIgnoreCase("logging")
        || args.length == 2 && args[0].equalsIgnoreCase("l") || args.length == 3 && args[0].equalsIgnoreCase("l")) {
            return List.of("enable", "disable", "status");
        }
        return Collections.emptyList();
    }
}