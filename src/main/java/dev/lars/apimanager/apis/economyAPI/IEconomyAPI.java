package dev.lars.apimanager.apis.economyAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IEconomyAPI {
    void initPlayer(UUID uuid);

    boolean doesUserExist(OfflinePlayer player);

    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    Instant getGiftCreatedAt(int giftId);

    CompletableFuture<Instant> getGiftCreatedAtAsync(int giftId);

    Instant getGiftUpdatedAt(int giftId);

    CompletableFuture<Instant> getGiftUpdatedAtAsync(int giftId);

    void setBalance(OfflinePlayer player, int amount);

    CompletableFuture<Void> setBalanceAsync(OfflinePlayer player, int amount);

    void increaseBalance(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseBalanceAsync(OfflinePlayer player, int amount);

    void decreaseBalance(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseBalanceAsync(OfflinePlayer player, int amount);

    Integer getBalance(OfflinePlayer player);

    CompletableFuture<Integer> getBalanceAsync(OfflinePlayer player);

    void addGift(OfflinePlayer player, String name, int value);

    CompletableFuture<Void> addGiftAsync(OfflinePlayer player, String name, int value);

    void removeGift(OfflinePlayer player, int gift);

    CompletableFuture<Void> removeGiftAsync(OfflinePlayer player, int gift);

    List<Gift> getGifts(OfflinePlayer player);

    CompletableFuture<List<Gift>> getGiftsAsync(OfflinePlayer player);

    Gift getGiftByName(OfflinePlayer player, String name);

    CompletableFuture<Gift> getGiftByNameAsync(OfflinePlayer player, String name);

    boolean hasGift(OfflinePlayer player, int giftId);

    CompletableFuture<Boolean> hasGiftAsync(OfflinePlayer player, int giftId);

    int getGiftCount(OfflinePlayer player);

    CompletableFuture<Integer> getGiftCountAsync(OfflinePlayer player);

    int getTotalGiftValue(OfflinePlayer player);

    CompletableFuture<Integer> getTotalGiftValueAsync(OfflinePlayer player);

    void resetGifts(OfflinePlayer player);

    CompletableFuture<Void> resetGiftsAsync(OfflinePlayer player);
}