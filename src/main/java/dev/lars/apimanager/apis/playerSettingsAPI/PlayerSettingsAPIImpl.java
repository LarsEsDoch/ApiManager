package dev.lars.apimanager.apis.playerSettingsAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class PlayerSettingsAPIImpl implements IPlayerSettingsAPI {
    private static final String TABLE = "player_settings";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update(String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                respawn_target ENUM('BED','HOME','SPAWN') NOT NULL DEFAULT 'BED',
                respawn_home_name VARCHAR(255) DEFAULT NULL,
                join_target ENUM('LAST_LOCATION','HOME','SPAWN','BED') NOT NULL DEFAULT 'LAST_LOCATION',
                join_home_name VARCHAR(255) DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, TABLE));
    }

    public void initPlayer(OfflinePlayer player) {
        repo().insertIgnore(TABLE, new String[]{"uuid"}, player.getUniqueId().toString());
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
    public void setRespawnTarget(OfflinePlayer player, RespawnTarget target) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateRespawnTarget(target);
        repo().updateColumn(TABLE, "respawn_target", target.name(), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setRespawnTargetAsync(OfflinePlayer player, RespawnTarget target) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateRespawnTarget(target);
        return repo().updateColumnAsync(TABLE, "respawn_target", target.name(), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public RespawnTarget getRespawnTarget(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return RespawnTarget.valueOf(repo().getString(TABLE, "respawn_target", "uuid = ?", player.getUniqueId().toString()));
    }

    @Override
    public CompletableFuture<RespawnTarget> getRespawnTargetAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "respawn_target", "uuid = ?", player.getUniqueId().toString())
            .thenApply(RespawnTarget::valueOf);
    }

    @Override
    public void setRespawnHomeName(OfflinePlayer player, String homeName) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "respawn_home_name", homeName, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setRespawnHomeNameAsync(OfflinePlayer player, String homeName) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "respawn_home_name", homeName, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getRespawnHomeName(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "respawn_home_name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getRespawnHomeNameAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "respawn_home_name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setJoinTarget(OfflinePlayer player, JoinTarget target) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateJoinTarget(target);
        repo().updateColumn(TABLE, "join_target", target.name(), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setJoinTargetAsync(OfflinePlayer player, JoinTarget target) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateJoinTarget(target);
        return repo().updateColumnAsync(TABLE, "join_target", target.name(), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public JoinTarget getJoinTarget(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return JoinTarget.valueOf(repo().getString(TABLE, "join_target", "uuid = ?", player.getUniqueId().toString()));
    }

    @Override
    public CompletableFuture<JoinTarget> getJoinTargetAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "join_target", "uuid = ?", player.getUniqueId().toString())
            .thenApply(JoinTarget::valueOf);
    }

    @Override
    public void setJoinHomeName(OfflinePlayer player, String homeName) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "join_home_name", homeName, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setJoinHomeNameAsync(OfflinePlayer player, String homeName) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "join_home_name", homeName, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getJoinHomeName(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "join_home_name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getJoinHomeNameAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "join_home_name", "uuid = ?", player.getUniqueId().toString());
    }
}