package de.lars.apimanager.commands;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.ConnectDatabase;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.Statements;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
            sender.sendMessage(Statements.getPrefix().append(Component.text("ApiManager commands:", NamedTextColor.AQUA)));
            sender.sendMessage(Statements.getPrefix().append(Component.text("/apimanager reload", NamedTextColor.GRAY)));
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
        } else {
            sender.sendMessage(Statements.getPrefix().append(Component.text("Unknown command.", NamedTextColor.YELLOW)));
            sender.sendMessage(Statements.getPrefix().append(Component.text("ApiManager commands:", NamedTextColor.AQUA)));
            sender.sendMessage(Statements.getPrefix().append(Component.text("/apimanager reload", NamedTextColor.GRAY)));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(final @NotNull CommandSourceStack commandSourceStack, final String[] args) {
        if (args.length <= 1) {
            return List.of("reload", "rl");
        }
        return Collections.emptyList();
    }
}