package dev.lars.apimanager.apis.courtAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CourtAPIImpl implements ICourtAPI {
    private static final String TABLE = "player_court";

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
                prosecutor_uuid CHAR(36) DEFAULT NULL,
                status INT NOT NULL DEFAULT 0,
                reason VARCHAR(255) NOT NULL DEFAULT '',
                time INT NOT NULL DEFAULT 0,
                cell INT NOT NULL DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, TABLE));
    }

    @Override
    public void initPlayer(UUID uuid) {
        repo().insertIgnore(TABLE, new String[]{"uuid"}, uuid.toString());
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
    public void report(OfflinePlayer player, OfflinePlayer prosecutor, String reason) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateReason(reason);
        repo().updateColumns(TABLE,
            new String[]{"prosecutor_uuid", "reason", "status"},
            new Object[]{prosecutor != null ? prosecutor.getUniqueId().toString() : null, reason, CourtStatus.REPORTED.getId()},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> reportAsync(OfflinePlayer player, OfflinePlayer prosecutor, String reason) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateReason(reason);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"prosecutor_uuid", "reason", "status"},
            new Object[]{prosecutor != null ? prosecutor.getUniqueId().toString() : null, reason, CourtStatus.REPORTED.getId()},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setStatus(OfflinePlayer player, CourtStatus status) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "status", status.getId(), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setStatusAsync(OfflinePlayer player, CourtStatus status) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "status", status.getId(), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CourtStatus getStatus(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer id = repo().getInteger(TABLE, "status", "uuid = ?", player.getUniqueId().toString());
        return CourtStatus.fromId(id != null ? id : 0);
    }

    @Override
    public CompletableFuture<CourtStatus> getStatusAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "status", "uuid = ?", player.getUniqueId().toString())
                .thenApply(id -> CourtStatus.fromId(id != null ? id : 0));
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
    public void setProsecutor(OfflinePlayer player, OfflinePlayer prosecutor) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "prosecutor_uuid",
            prosecutor != null ? prosecutor.getUniqueId().toString() : null,
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setProsecutorAsync(OfflinePlayer player, OfflinePlayer prosecutor) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "prosecutor_uuid",
            prosecutor != null ? prosecutor.getUniqueId().toString() : null,
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getProsecutor(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "prosecutor_uuid", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getProsecutorAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "prosecutor_uuid", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setTime(OfflinePlayer player, int time) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "time", time, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setTimeAsync(OfflinePlayer player, int time) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "time", time, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getTime(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "time", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getTimeAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "time", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setCell(OfflinePlayer player, int cell) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "cell", cell, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setCellAsync(OfflinePlayer player, int cell) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "cell", cell, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getCell(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "cell", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getCellAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "cell", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void resetPlayer(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumns(TABLE,
            new String[]{"status", "reason", "prosecutor_uuid", "time", "cell"},
            new Object[]{0, "", null, 0, 0},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetPlayerAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"status", "reason", "prosecutor_uuid", "time", "cell"},
            new Object[]{0, "", null, 0, 0},
            "uuid = ?", player.getUniqueId().toString());
    }
}