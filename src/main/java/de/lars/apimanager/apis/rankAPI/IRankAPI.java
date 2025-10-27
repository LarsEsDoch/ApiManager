package de.lars.apimanager.apis.rankAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface IRankAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setRank(OfflinePlayer player, Integer rankId, Integer days);

    CompletableFuture<Void> setRankAsync(OfflinePlayer player, Integer rankId, Integer days);

    void addRankDays(OfflinePlayer player, int daysToAdd);

    CompletableFuture<Void> addRankDaysAsync(OfflinePlayer player, int daysToAdd);

    void removeRankDays(OfflinePlayer player, int daysToRemove);

    CompletableFuture<Void> removeRankDaysAsync(OfflinePlayer player, int daysToRemove);

    Integer getRankId(OfflinePlayer player);

    CompletableFuture<Integer> getRankIdAsync(OfflinePlayer player);

    Timestamp getExpiresAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getExpiresAtAsync(OfflinePlayer player);
}