package de.lars.apimanager.apis.limitAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface ILimitAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setBackpackSlots(OfflinePlayer player, int backpackSlots);

    CompletableFuture<Void> setBackpackSlotsAsync(OfflinePlayer player, int backpackSlots);

    void increaseBackpackSlots(OfflinePlayer player, int backpackSlots);

    CompletableFuture<Void> increaseBackpackSlotsAsync(OfflinePlayer player, int backpackSlots);

    void decreaseBackpackSlots(OfflinePlayer player, int backpackSlots);

    CompletableFuture<Void> decreaseBackpackSlotsAsync(OfflinePlayer player, int backpackSlots);

    Integer getBackpackSlots(OfflinePlayer player);

    CompletableFuture<Integer> getBackpackSlotsAsync(OfflinePlayer player);

    void setMaxChunks(OfflinePlayer player, Integer max_chunks);

    CompletableFuture<Void> setMaxChunksAsync(OfflinePlayer player, Integer max_chunks);

    void increaseMaxChunks(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseMaxChunksAsync(OfflinePlayer player, int amount);

    void decreaseMaxChunks(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseMaxChunksAsync(OfflinePlayer player, int amount);

    Integer getMaxChunks(OfflinePlayer player);

    CompletableFuture<Integer> getMaxChunksAsync(OfflinePlayer player);

    void setMaxHomes(OfflinePlayer player, Integer home_limit);

    CompletableFuture<Void> setMaxHomesAsync(OfflinePlayer player, Integer home_limit);

    void increaseMaxHomes(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseMaxHomesAsync(OfflinePlayer player, int amount);

    void decreaseMaxHomes(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseMaxHomesAsync(OfflinePlayer player, int amount);

    Integer getMaxHomes(OfflinePlayer player);

    CompletableFuture<Integer> getMaxHomesAsync(OfflinePlayer player);
}