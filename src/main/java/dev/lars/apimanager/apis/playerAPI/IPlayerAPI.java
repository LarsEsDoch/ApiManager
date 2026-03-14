package dev.lars.apimanager.apis.playerAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IPlayerAPI {
    void initPlayer(UUID uuid, String name);

    boolean doesUserExist(OfflinePlayer player);

    boolean isFullyRegistered(OfflinePlayer player);

    CompletableFuture<Boolean> isFullyRegisteredAsync(OfflinePlayer player);

    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

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

    String getCurrentServer(OfflinePlayer player);

    CompletableFuture<String> getCurrentServerAsync(OfflinePlayer player);

    Instant getLastSeen(OfflinePlayer player);

    CompletableFuture<Instant> getLastSeenAsync(OfflinePlayer player);
}