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

    void setFakeRank(OfflinePlayer player, Integer rankID);

    CompletableFuture<Void> setFakeRankAsync(OfflinePlayer player, Integer rankID);

    Integer getFakeRank(OfflinePlayer player);

    CompletableFuture<Integer> getFakeRankAsync(OfflinePlayer player);
}