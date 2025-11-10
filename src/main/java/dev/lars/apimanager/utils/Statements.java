package dev.lars.apimanager.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

public class Statements {

    public static Component getPrefix() {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text("ApiManager", NamedTextColor.AQUA))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" ", NamedTextColor.GRAY));
    }

    public static void logToConsole(String message, NamedTextColor color) {
        Bukkit.getConsoleSender().sendMessage(
            Statements.getPrefix().append(Component.text(message, color))
        );
    }
}