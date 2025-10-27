package de.lars.apimanager.apis.limitAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface ILimitAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setSlots(OfflinePlayer player, int slots);

    CompletableFuture<Void> setSlotsAsync(OfflinePlayer player, int slots);

    Integer getSlots(OfflinePlayer player);

    CompletableFuture<Integer> getSlotsAsync(OfflinePlayer player);

    void setChunkLimit(OfflinePlayer player, Integer chunk_limit);

    CompletableFuture<Void> setChunkLimitAsync(OfflinePlayer player, Integer chunk_limit);

    Integer getChunkLimit(OfflinePlayer player);

    CompletableFuture<Integer> getChunkLimitAsync(OfflinePlayer player);

    void setHomeLimit(OfflinePlayer player, Integer home_limit);

    CompletableFuture<Void> setHomeLimitAsync(OfflinePlayer player, Integer home_limit);

    Integer getHomeLimit(OfflinePlayer player);

    CompletableFuture<Integer> getHomeLimitAsync(OfflinePlayer player);
}