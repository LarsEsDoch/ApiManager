package dev.lars.apimanager.apis.playerIdentityAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IPlayerIdentityAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setVanished(OfflinePlayer player, boolean vanished);

    CompletableFuture<Void> setVanishedAsync(OfflinePlayer player, boolean vanished);

    boolean isVanished(OfflinePlayer player);

    CompletableFuture<Boolean> isVanishedAsync(OfflinePlayer player);

    void setNickname(OfflinePlayer player, String nickname);

    CompletableFuture<Void> setNicknameAsync(OfflinePlayer player, String nickname);

    void resetNickname(OfflinePlayer player);

    CompletableFuture<Void> resetNicknameAsync(OfflinePlayer player);

    String getNickname(OfflinePlayer player);

    CompletableFuture<String> getNicknameAsync(OfflinePlayer player);

    void setDisguiseRank(OfflinePlayer player, Integer rankID);

    CompletableFuture<Void> setDisguiseRankAsync(OfflinePlayer player, Integer rankID);

    Integer getDisguiseRank(OfflinePlayer player);

    CompletableFuture<Integer> getDisguiseRankAsync(OfflinePlayer player);
}