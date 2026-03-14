package dev.lars.apimanager.utils;

import dev.lars.apimanager.ApiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

public class ApiManagerStatements {

    public static Component getPrefix() {
        ApiManager inst = ApiManager.getInstance();
        String name = (inst != null) ? inst.getName() : "ApiManager";
        return Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text(name, NamedTextColor.AQUA))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" ", NamedTextColor.GRAY));
    }

    public static void logToConsole(String message, NamedTextColor color) {
        Bukkit.getConsoleSender().sendMessage(
            ApiManagerStatements.getPrefix().append(Component.text(message, color))
        );
    }
}