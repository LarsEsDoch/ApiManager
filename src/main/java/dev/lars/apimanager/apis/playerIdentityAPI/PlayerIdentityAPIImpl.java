package dev.lars.apimanager.apis.playerIdentityAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class PlayerIdentityAPIImpl implements IPlayerIdentityAPI {
    private static final String TABLE = "player_identity";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_identity (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                vanished BOOLEAN NOT NULL DEFAULT FALSE,
                nickname VARCHAR(32) DEFAULT NULL,
                disguise_rank_id INT(16) DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_identity (uuid, vanished, nickname, disguise_rank_id)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), false, null, null);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return repo().exists(TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getCreatedAt(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setVanished(OfflinePlayer player, boolean vanished) {
        repo().updateColumn(TABLE, "vanished", vanished, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setVanishedAsync(OfflinePlayer player, boolean vanished) {
        return repo().updateColumnAsync(TABLE, "vanished", vanished, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean isVanished(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "vanished", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> isVanishedAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "vanished", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setNickname(OfflinePlayer player, String nickname) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateNickName(nickname);
        repo().updateColumn(TABLE, "nickname", nickname, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setNicknameAsync(OfflinePlayer player, String nickname) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateNickName(nickname);
        return repo().updateColumnAsync(TABLE, "nickname", nickname, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void resetNickname(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumns(TABLE,
            new String[]{"nickname", "disguise_rank_id"},
            new Object[]{null, null},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetNicknameAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"nickname", "disguise_rank_id"},
            new Object[]{null, null},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getNickname(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "nickname", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getNicknameAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "nickname", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setDisguiseRank(OfflinePlayer player, Integer rankId) {
        repo().updateColumn(TABLE, "disguise_rank_id", rankId, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setDisguiseRankAsync(OfflinePlayer player, Integer rankId) {
        return repo().updateColumnAsync(TABLE, "disguise_rank_id", rankId, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getDisguiseRank(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "disguise_rank_id", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getDisguiseRankAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "disguise_rank_id", "uuid = ?", player.getUniqueId().toString());
    }
}