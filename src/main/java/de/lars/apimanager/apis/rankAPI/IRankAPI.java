package de.lars.apimanager.apis.rankAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IRankAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setRank(OfflinePlayer player, int rankId, Integer days);

    CompletableFuture<Void> setRankAsync(OfflinePlayer player, int rankId, Integer days);

    void increaseRankDays(OfflinePlayer player, int daysToAdd);

    CompletableFuture<Void> increaseRankDaysAsync(OfflinePlayer player, int daysToAdd);

    void decreaseRankDays(OfflinePlayer player, int daysToRemove);

    CompletableFuture<Void> decreaseRankDaysAsync(OfflinePlayer player, int daysToRemove);

    Integer getRankId(OfflinePlayer player);

    CompletableFuture<Integer> getRankIdAsync(OfflinePlayer player);

    Instant getExpiresAt(OfflinePlayer player);

    CompletableFuture<Instant> getExpiresAtAsync(OfflinePlayer player);
}