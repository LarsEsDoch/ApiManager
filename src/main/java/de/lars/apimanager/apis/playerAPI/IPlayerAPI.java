package de.lars.apimanager.apis.playerAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IPlayerAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    String getName(OfflinePlayer player);

    CompletableFuture<String> getNameAsync(OfflinePlayer player);

    void setPlaytime(OfflinePlayer player, int playtime);

    CompletableFuture<Void> setPlaytimeAsync(OfflinePlayer player, int playtime);

    Integer getPlaytime(OfflinePlayer player);

    CompletableFuture<Integer> getPlaytimeAsync(OfflinePlayer player);

    void setOnline(OfflinePlayer player, boolean online);

    CompletableFuture<Void> setOnlineAsync(OfflinePlayer player, boolean online);

    boolean isOnline(OfflinePlayer player);

    CompletableFuture<Boolean> isOnlineAsync(OfflinePlayer player);

    Instant getLastSeen(OfflinePlayer player);

    CompletableFuture<Instant> getLastSeenAsync(OfflinePlayer player);
}