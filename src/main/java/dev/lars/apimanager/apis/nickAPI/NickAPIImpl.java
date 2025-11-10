package dev.lars.apimanager.apis.nickAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class NickAPIImpl implements INickAPI {
    private static final String TABLE = "player_nicknames";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_nicknames (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
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
            INSERT IGNORE INTO player_nicknames (uuid, nickname, disguise_rank_id)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), null, null);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return repo().exists(TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setNickname(OfflinePlayer player, String nickname) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNickName(nickname);
        repo().updateColumn(TABLE, "nickname", nickname, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setNicknameAsync(OfflinePlayer player, String nickname) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNickName(nickname);
        return repo().updateColumnAsync(TABLE, "nickname", nickname, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void resetNickname(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumns(TABLE,
            new String[]{"nickname", "disguise_rank_id"},
            new Object[]{null, null},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetNicknameAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"nickname", "disguise_rank_id"},
            new Object[]{null, null},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getNickname(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "nickname", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getNicknameAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
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
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "disguise_rank_id", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getDisguiseRankAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "disguise_rank_id", "uuid = ?", player.getUniqueId().toString());
    }
}