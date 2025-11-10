package dev.lars.apimanager.apis.statusAPI;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IStatusAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setStatus(OfflinePlayer player, String status);

    CompletableFuture<Void> setStatusAsync(OfflinePlayer player, String status);

    String getStatus(OfflinePlayer player);

    CompletableFuture<String> getStatusAsync(OfflinePlayer player);

    void setColor(OfflinePlayer player, NamedTextColor color);

    CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color);

    NamedTextColor getColor(OfflinePlayer player);

    CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player);
}