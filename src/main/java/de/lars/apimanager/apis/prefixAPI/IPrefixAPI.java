package de.lars.apimanager.apis.prefixAPI;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface IPrefixAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setColor(OfflinePlayer player, NamedTextColor color);

    CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color);

    NamedTextColor getColor(OfflinePlayer player);

    CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player);

    void setDecoration(OfflinePlayer player, TextDecoration decoration);

    CompletableFuture<Void> setDecorationAsync(OfflinePlayer player, TextDecoration decoration);

    void setDecoration(OfflinePlayer player, Set<TextDecoration> decorations);

    CompletableFuture<Void> setDecorationAsync(OfflinePlayer player, Set<TextDecoration> decorations);

    void addDecoration(OfflinePlayer player, TextDecoration decoration);

    CompletableFuture<Void> addDecorationAsync(OfflinePlayer player, TextDecoration decoration);

    void removeDecoration(OfflinePlayer player, TextDecoration decoration);

    CompletableFuture<Void> removeDecorationAsync(OfflinePlayer player, TextDecoration decoration);

    Set<TextDecoration> getDecoration(OfflinePlayer player);

    CompletableFuture<Set<TextDecoration>> getDecorationAsync(OfflinePlayer player);
}