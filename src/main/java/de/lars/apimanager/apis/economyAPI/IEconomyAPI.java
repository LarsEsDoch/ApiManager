package de.lars.apimanager.apis.economyAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IEconomyAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setBalance(OfflinePlayer player, int amount);

    CompletableFuture<Void> setBalanceAsync(OfflinePlayer player, int amount);

    void increaseBalance(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseBalanceAsync(OfflinePlayer player, int amount);

    void decreaseBalance(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseBalanceAsync(OfflinePlayer player, int amount);

    Integer getBalance(OfflinePlayer player);

    CompletableFuture<Integer> getBalanceAsync(OfflinePlayer player);

    void addGift(OfflinePlayer player, int gift);

    CompletableFuture<Void> addGiftAsync(OfflinePlayer player, int gift);

    void removeGift(OfflinePlayer player, int gift);

    CompletableFuture<Void> removeGiftAsync(OfflinePlayer player, int gift);

    List<Integer> getGifts(OfflinePlayer player);

    CompletableFuture<List<Integer>> getGiftsAsync(OfflinePlayer player);
}