package de.lars.apimanager.apis.coinAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ICoinAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setCoins(OfflinePlayer player, int amount);

    CompletableFuture<Void> setCoinsAsync(OfflinePlayer player, int amount);

    void addCoins(OfflinePlayer player, int amount);

    CompletableFuture<Void> addCoinsAsync(OfflinePlayer player, int amount);

    void removeCoins(OfflinePlayer player, int amount);

    CompletableFuture<Void> removeCoinsAsync(OfflinePlayer player, int amount);

    Integer getCoins(OfflinePlayer player);

    CompletableFuture<Integer> getCoinsAsync(OfflinePlayer player);

    void addGift(OfflinePlayer player, int gift);

    CompletableFuture<Void> addGiftAsync(OfflinePlayer player, int gift);

    void removeGift(OfflinePlayer player, int gift);

    CompletableFuture<Void> removeGiftAsync(OfflinePlayer player, int gift);

    List<Integer> getGifts(OfflinePlayer player);

    CompletableFuture<List<Integer>> getGiftsAsync(OfflinePlayer player);
}