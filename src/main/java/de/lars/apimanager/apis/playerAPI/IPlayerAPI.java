package de.lars.apimanager.apis.playerAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface IPlayerAPI {
    Timestamp getCreatedAt();

    CompletableFuture<Timestamp> getCreatedAtAsync();

    Timestamp getUpdatedAt();

    CompletableFuture<Timestamp> getUpdatedAtAsync();

    String getName(OfflinePlayer player);

    CompletableFuture<String> getNameAsync(OfflinePlayer player);

    void setPlaytime(OfflinePlayer player, Integer playtime);

    CompletableFuture<Void> setPlaytimeAsync(OfflinePlayer player, Integer playtime);

    Integer getPlaytime(OfflinePlayer player);

    CompletableFuture<Integer> getPlaytimeAsync(OfflinePlayer player);

    void setChunkLimit(OfflinePlayer player, Integer chunk_limit);

    CompletableFuture<Void> setChunkLimitAsync(OfflinePlayer player, Integer chunk_limit);

    Integer getChunkLimit(OfflinePlayer player);

    CompletableFuture<Integer> getChunkLimitAsync(OfflinePlayer player);

    void setHomeLimit(OfflinePlayer player, Integer home_limit);

    void setOnline(OfflinePlayer player, Boolean online);

    CompletableFuture<Void> setHomeLimitAsync(OfflinePlayer player, Integer home_limit);

    CompletableFuture<Void> setOnlineAsync(OfflinePlayer player, Boolean online);

    Integer getHomeLimit(OfflinePlayer player);

    CompletableFuture<Integer> getHomeLimitAsync(OfflinePlayer player);

    boolean isOnline(OfflinePlayer player);

    CompletableFuture<Boolean> isOnlineAsync(OfflinePlayer player);

    Timestamp getLastSeen(OfflinePlayer player);

    CompletableFuture<Timestamp> getLastSeenAsync(OfflinePlayer player);
}