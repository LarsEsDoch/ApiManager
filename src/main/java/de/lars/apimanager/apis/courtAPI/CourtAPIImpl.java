package de.lars.apimanager.apis.courtAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class CourtAPIImpl implements ICourtAPI {
    private static final String TABLE = "player_court";

    public static final int STATUS_FREE = 0;
    public static final int STATUS_REPORTED = 1;
    public static final int STATUS_WAITING = 2;
    public static final int STATUS_COURT = 3;
    public static final int STATUS_LOCKED = 4;
    public static final int STATUS_JAILED = 5;

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_court (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                prosecutor_uuid CHAR(36) DEFAULT NULL,
                status INT DEFAULT 0,
                reason VARCHAR(255) DEFAULT NULL,
                time INT DEFAULT 0,
                cell INT DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_court (uuid, prosecutor_uuid, status, reason, time, cell)
            VALUES (?, NULL, ?, NULL, ?, ?)
        """, player.getUniqueId().toString(), STATUS_FREE, 0, 0);
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
    public void report(OfflinePlayer player, OfflinePlayer prosecutor_uuid, String reason) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateReason(reason);
        repo().updateColumns(TABLE,
            new String[]{"prosecutor_uuid", "reason", "status"},
            new Object[]{prosecutor_uuid != null ? prosecutor_uuid.getUniqueId().toString() : null, reason, STATUS_REPORTED},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> reportAsync(OfflinePlayer player, OfflinePlayer prosecutor_uuid, String reason) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateReason(reason);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"prosecutor_uuid", "reason", "status"},
            new Object[]{prosecutor_uuid != null ? prosecutor_uuid.getUniqueId().toString() : null, reason, STATUS_REPORTED},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setStatus(OfflinePlayer player, int status) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "status", status, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setStatusAsync(OfflinePlayer player, int status) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "status", status, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getStatus(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "status", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getStatusAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "status", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setReason(OfflinePlayer player, String reason) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateReason(reason);
        repo().updateColumn(TABLE, "reason", reason, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setReasonAsync(OfflinePlayer player, String reason) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateReason(reason);
        return repo().updateColumnAsync(TABLE, "reason", reason, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getReason(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "reason", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getReasonAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "reason", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setProsecutor(OfflinePlayer player, OfflinePlayer prosecutor_uuid) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "prosecutor_uuid",
            prosecutor_uuid != null ? prosecutor_uuid.getUniqueId().toString() : null,
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setProsecutorAsync(OfflinePlayer player, OfflinePlayer prosecutor_uuid) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "prosecutor_uuid",
            prosecutor_uuid != null ? prosecutor_uuid.getUniqueId().toString() : null,
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getProsecutor(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "prosecutor_uuid", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getProsecutorAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "prosecutor_uuid", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setTime(OfflinePlayer player, int time) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "time", time, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setTimeAsync(OfflinePlayer player, int time) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "time", time, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getTime(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "time", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getTimeAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "time", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setCell(OfflinePlayer player, int cell) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "cell", cell, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setCellAsync(OfflinePlayer player, int cell) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "cell", cell, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getCell(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "cell", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getCellAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "cell", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void resetPlayer(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumns(TABLE,
            new String[]{"status", "reason", "prosecutor_uuid", "time", "cell"},
            new Object[]{0, null, null, 0, 0},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetPlayerAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"status", "reason", "prosecutor_uuid", "time", "cell"},
            new Object[]{0, null, null, 0, 0},
            "uuid = ?", player.getUniqueId().toString());
    }
}