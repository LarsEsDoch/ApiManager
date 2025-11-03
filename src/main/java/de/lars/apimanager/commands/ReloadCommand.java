package de.lars.apimanager.commands;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.ConnectDatabase;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.database.IDatabaseManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public record ReloadCommand(ApiManager plugin, ConnectDatabase connectDatabase) implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] args) {
        plugin.reloadConfig();

        boolean success = connectDatabase.loadDatabaseConfig();

        if (!success) {
            stack.getSender().sendMessage(Component.text("§cError: Invalid database configuration!"));
            return;
        }

        stack.getSender().sendMessage(Component.text("§eConnecting to database..."));

        IDatabaseManager dbm = ApiManager.getInstance().getDatabaseManager();
        if (dbm instanceof DatabaseManager real) {
            real.readyFuture().thenRun(() -> Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    plugin.reinitializeApisAfterDbReconnect();
                    stack.getSender().sendMessage(Component.text("§aDatabase configuration successfully reloaded!"));
                } catch (Exception e) {
                    stack.getSender().sendMessage(Component.text("§cFailed to reinitialize APIs: " + e.getMessage()));
                    plugin.getLogger().warning("Failed to reinitialize APIs: " + e.getMessage());
                }
            }));
        } else {
            stack.getSender().sendMessage(Component.text("§cNo real database connected; running in safe mode."));
        }
    }
}