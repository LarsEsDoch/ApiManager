package de.lars.apimanager.apis.prefixAPI;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface IPrefixAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setColor(OfflinePlayer player, NamedTextColor color);

    CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color);

    NamedTextColor getColor(OfflinePlayer player);

    CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player);

    void setDecoration(OfflinePlayer player, TextDecoration decoration);

    CompletableFuture<Void> setDecorationAsync(OfflinePlayer player, TextDecoration decoration);

    void setDecorations(OfflinePlayer player, Set<TextDecoration> decorations);

    CompletableFuture<Void> setDecorationsAsync(OfflinePlayer player, Set<TextDecoration> decorations);

    void addDecoration(OfflinePlayer player, TextDecoration decoration);

    CompletableFuture<Void> addDecorationAsync(OfflinePlayer player, TextDecoration decoration);

    void removeDecoration(OfflinePlayer player, TextDecoration decoration);

    CompletableFuture<Void> removeDecorationAsync(OfflinePlayer player, TextDecoration decoration);

    Set<TextDecoration> getDecorations(OfflinePlayer player);

    CompletableFuture<Set<TextDecoration>> getDecorationsAsync(OfflinePlayer player);
}