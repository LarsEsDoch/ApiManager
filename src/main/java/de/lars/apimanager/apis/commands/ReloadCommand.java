package de.lars.apimanager.apis.commands;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.ConnectDatabase;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements BasicCommand {
    private final ApiManager plugin;
    private final ConnectDatabase connectDatabase;

    public ReloadCommand(ApiManager plugin, ConnectDatabase connectDatabase) {
        this.plugin = plugin;
        this.connectDatabase = connectDatabase;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        plugin.reloadConfig();

        boolean success = connectDatabase.loadDatabaseConfig();

        if (success) {
            plugin.reinitializeApisAfterDbReconnect();

            stack.getSender().sendMessage(Component.text("§aDatabase configuration successfully reloaded!"));
        } else {
            stack.getSender().sendMessage(Component.text("§cError: Invalid database configuration!"));
        }
    }
}