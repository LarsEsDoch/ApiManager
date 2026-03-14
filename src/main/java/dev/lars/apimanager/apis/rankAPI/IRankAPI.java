package dev.lars.apimanager.apis.rankAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IRankAPI {
    void initPlayer(UUID uuid);

    boolean doesUserExist(OfflinePlayer player);

    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setRank(OfflinePlayer player, int rankId, Integer days);

    CompletableFuture<Void> setRankAsync(OfflinePlayer player, int rankId, Integer days);

    void setRank(OfflinePlayer player, int rankId, Instant expiresAt);

    CompletableFuture<Void> setRankAsync(OfflinePlayer player, int rankId, Instant expiresAt);

    void setRankDays(OfflinePlayer player, Integer days);

    CompletableFuture<Void> setRankDaysAsync(OfflinePlayer player, Integer days);

    void increaseRankDays(OfflinePlayer player, int daysToAdd);

    CompletableFuture<Void> increaseRankDaysAsync(OfflinePlayer player, int daysToAdd);

    void decreaseRankDays(OfflinePlayer player, int daysToRemove);

    CompletableFuture<Void> decreaseRankDaysAsync(OfflinePlayer player, int daysToRemove);

    Integer getRankId(OfflinePlayer player);

    CompletableFuture<Integer> getRankIdAsync(OfflinePlayer player);

    void setExpiresAt(OfflinePlayer player, Instant expiresAt);

    CompletableFuture<Void> setExpiresAtAsync(OfflinePlayer player, Instant expiresAt);

    Instant getExpiresAt(OfflinePlayer player);

    CompletableFuture<Instant> getExpiresAtAsync(OfflinePlayer player);

    long getDaysRemaining(OfflinePlayer player);

    CompletableFuture<Long> getDaysRemainingAsync(OfflinePlayer player);
}