package de.lars.apimanager.apis.limitAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface ILimitAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setSlots(OfflinePlayer player, int slots);

    CompletableFuture<Void> setSlotsAsync(OfflinePlayer player, int slots);

    void increaseSlots(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseSlotsAsync(OfflinePlayer player, int amount);

    void decreaseSlots(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseSlotsAsync(OfflinePlayer player, int amount);

    Integer getSlots(OfflinePlayer player);

    CompletableFuture<Integer> getSlotsAsync(OfflinePlayer player);

    void setChunkLimit(OfflinePlayer player, Integer chunk_limit);

    CompletableFuture<Void> setChunkLimitAsync(OfflinePlayer player, Integer chunk_limit);

    void increaseChunkLimit(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseChunkLimitAsync(OfflinePlayer player, int amount);

    void decreaseChunkLimit(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseChunkLimitAsync(OfflinePlayer player, int amount);

    Integer getChunkLimit(OfflinePlayer player);

    CompletableFuture<Integer> getChunkLimitAsync(OfflinePlayer player);

    void setHomeLimit(OfflinePlayer player, Integer home_limit);

    CompletableFuture<Void> setHomeLimitAsync(OfflinePlayer player, Integer home_limit);

    void increaseHomeLimit(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseHomeLimitAsync(OfflinePlayer player, int amount);

    void decreaseHomeLimit(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseHomeLimitAsync(OfflinePlayer player, int amount);

    Integer getHomeLimit(OfflinePlayer player);

    CompletableFuture<Integer> getHomeLimitAsync(OfflinePlayer player);
}