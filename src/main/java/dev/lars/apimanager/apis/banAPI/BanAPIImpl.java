package dev.lars.apimanager.apis.banAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BanAPIImpl implements IBanAPI {
    private static final String TABLE = "player_bans";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_bans (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                is_banned BOOLEAN NOT NULL DEFAULT FALSE,
                reason VARCHAR(255) NOT NULL DEFAULT "",
                banned_at TIMESTAMP DEFAULT NULL,
                expires_at TIMESTAMP DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_bans (uuid, is_banned, reason, banned_at, expires_at)
            VALUES (?, ?, ?, ?, ?)
        """, player.getUniqueId().toString(), false, "", null, null);
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
    public void setBanned(OfflinePlayer player, String reason, Instant expiresAt) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateReason(reason);
        ApiManagerValidateParameter.validateExpiresAt(expiresAt);
        Instant bannedAt = Instant.now();

        repo().updateColumns(TABLE,
            new String[]{"reason", "expires_at", "is_banned", "banned_at"},
            new Object[]{reason, expiresAt, true, bannedAt},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBannedAsync(OfflinePlayer player, String reason, Instant expiresAt) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateReason(reason);
        ApiManagerValidateParameter.validateExpiresAt(expiresAt);
        Instant bannedAt = Instant.now();

        return repo().updateColumnsAsync(TABLE,
            new String[]{"reason", "expires_at", "is_banned", "banned_at"},
            new Object[]{reason, expiresAt, true, bannedAt},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setUnBanned(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumns(TABLE,
            new String[]{"is_banned", "banned_at", "expires_at", "reason"},
            new Object[]{false, null, null, ""},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setUnBannedAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"is_banned", "banned_at", "expires_at", "reason"},
            new Object[]{false, null, null, ""},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setReason(OfflinePlayer player, String reason) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateReason(reason);
        repo().updateColumn(TABLE, "reason", reason, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setReasonAsync(OfflinePlayer player, String reason) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateReason(reason);
        return repo().updateColumnAsync(TABLE, "reason", reason, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getReason(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "reason", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getReasonAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "reason", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setExpiresAt(OfflinePlayer player, Instant expiresAt) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateExpiresAt(expiresAt);
        repo().updateColumn(TABLE, "expires_at", expiresAt, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setExpiresAtAsync(OfflinePlayer player, Instant expiresAt) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateExpiresAt(expiresAt);
        return repo().updateColumnAsync(TABLE, "expires_at", expiresAt, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getExpiration(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "expires_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getExpirationAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "expires_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean isBanned(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "is_banned", "uuid = ?", player.getUniqueId().toString());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isBannedAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "is_banned", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result != null && result);
    }

    @Override
    public List<String> getBannedPlayers() {
        return repo().getStringList(TABLE, "uuid", "is_banned = TRUE");
    }

    @Override
    public CompletableFuture<List<String>> getBannedPlayersAsync() {
        return repo().getStringListAsync(TABLE, "uuid", "is_banned = TRUE");
    }
}