package de.lars.apimanager.apis.nickAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
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
                fake_rank INT(16) DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_nicknames (uuid, nickname, fake_rank)
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
            new String[]{"nickname", "fake_rank"},
            new Object[]{null, null},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetNicknameAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"nickname", "fake_rank"},
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
    public void setFakeRank(OfflinePlayer player, Integer rankId) {
        repo().updateColumn(TABLE, "fake_rank", rankId, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setFakeRankAsync(OfflinePlayer player, Integer rankId) {
        return repo().updateColumnAsync(TABLE, "fake_rank", rankId, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getFakeRank(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "fake_rank", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getFakeRankAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "fake_rank", "uuid = ?", player.getUniqueId().toString());
    }
}