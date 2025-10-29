package de.lars.apimanager.apis.nickAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface INickAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

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