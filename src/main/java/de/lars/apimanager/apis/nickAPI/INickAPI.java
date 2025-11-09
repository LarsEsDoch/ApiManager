package de.lars.apimanager.apis.nickAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface INickAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

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