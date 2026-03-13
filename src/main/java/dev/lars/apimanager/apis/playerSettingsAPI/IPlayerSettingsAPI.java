package dev.lars.apimanager.apis.playerSettingsAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IPlayerSettingsAPI {
    void initPlayer(UUID uuid);

    boolean doesUserExist(OfflinePlayer player);

    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setRespawnTarget(OfflinePlayer player, RespawnTarget target);

    CompletableFuture<Void> setRespawnTargetAsync(OfflinePlayer player, RespawnTarget target);

    RespawnTarget getRespawnTarget(OfflinePlayer player);

    CompletableFuture<RespawnTarget> getRespawnTargetAsync(OfflinePlayer player);

    void setRespawnHomeName(OfflinePlayer player, String homeName);

    CompletableFuture<Void> setRespawnHomeNameAsync(OfflinePlayer player, String homeName);

    String getRespawnHomeName(OfflinePlayer player);

    CompletableFuture<String> getRespawnHomeNameAsync(OfflinePlayer player);

    void setJoinTarget(OfflinePlayer player, JoinTarget target);

    CompletableFuture<Void> setJoinTargetAsync(OfflinePlayer player, JoinTarget target);

    JoinTarget getJoinTarget(OfflinePlayer player);

    CompletableFuture<JoinTarget> getJoinTargetAsync(OfflinePlayer player);

    void setJoinHomeName(OfflinePlayer player, String homeName);

    CompletableFuture<Void> setJoinHomeNameAsync(OfflinePlayer player, String homeName);

    String getJoinHomeName(OfflinePlayer player);

    CompletableFuture<String> getJoinHomeNameAsync(OfflinePlayer player);
}