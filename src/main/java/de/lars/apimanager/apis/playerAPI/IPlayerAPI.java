package de.lars.apimanager.apis.playerAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface IPlayerAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    String getName(OfflinePlayer player);

    CompletableFuture<String> getNameAsync(OfflinePlayer player);

    void setPlaytime(OfflinePlayer player, long playtime);

    CompletableFuture<Void> setPlaytimeAsync(OfflinePlayer player, long playtime);

    Long getPlaytime(OfflinePlayer player);

    CompletableFuture<Long> getPlaytimeAsync(OfflinePlayer player);

    void setOnline(OfflinePlayer player, boolean online);

    CompletableFuture<Void> setOnlineAsync(OfflinePlayer player, boolean online);

    boolean isOnline(OfflinePlayer player);

    CompletableFuture<Boolean> isOnlineAsync(OfflinePlayer player);

    Timestamp getLastSeen(OfflinePlayer player);

    CompletableFuture<Timestamp> getLastSeenAsync(OfflinePlayer player);
}