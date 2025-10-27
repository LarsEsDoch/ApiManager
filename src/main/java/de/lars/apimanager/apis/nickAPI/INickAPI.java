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

    String getNickname(OfflinePlayer player);

    CompletableFuture<String> getNicknameAsync(OfflinePlayer player);

    void setFakeRank(OfflinePlayer player, String prefix);

    CompletableFuture<Void> setFakeRankAsync(OfflinePlayer player, String prefix);

    String getFakeRank(OfflinePlayer player);

    CompletableFuture<String> getFakeRankAsync(OfflinePlayer player);
}
