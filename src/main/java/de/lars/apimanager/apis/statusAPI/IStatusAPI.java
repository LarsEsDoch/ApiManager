package de.lars.apimanager.apis.statusAPI;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface IStatusAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setStatus(OfflinePlayer player, String status);

    CompletableFuture<Void> setStatusAsync(OfflinePlayer player, String status);

    String getStatus(OfflinePlayer player);

    CompletableFuture<String> getStatusAsync(OfflinePlayer player);

    void setColor(OfflinePlayer player, NamedTextColor color);

    CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color);

    NamedTextColor getColor(OfflinePlayer player);

    CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player);
}
