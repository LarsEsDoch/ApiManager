package de.lars.apimanager.apis.coinAPI;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ICoinAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setCoins(OfflinePlayer player, int amount);

    CompletableFuture<Void> setCoinsAsync(OfflinePlayer player, int amount);

    void addCoins(OfflinePlayer player, int amount);

    CompletableFuture<Void> addCoinsAsync(OfflinePlayer player, int amount);

    void removeCoins(OfflinePlayer player, int amount);

    CompletableFuture<Void> removeCoinsAsync(OfflinePlayer player, int amount);

    Integer getCoins(OfflinePlayer player);

    CompletableFuture<Integer> getCoinsAsync(OfflinePlayer player);

    List<String> getGifts(OfflinePlayer player);

    void addGift(OfflinePlayer player, String gift);

    CompletableFuture<Void> addGiftAsync(OfflinePlayer player, String gift);

    void removeGift(OfflinePlayer player, String gift);

    CompletableFuture<Void> removeGiftAsync(OfflinePlayer player, String gift);

    CompletableFuture<List<String>> getGiftsAsync(OfflinePlayer player);
}