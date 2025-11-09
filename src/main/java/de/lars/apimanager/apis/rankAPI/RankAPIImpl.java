package de.lars.apimanager.apis.rankAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

public class RankAPIImpl implements IRankAPI {
    private static final String TABLE = "player_ranks";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_ranks (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                rank_id INT NOT NULL DEFAULT 0,
                expires_at TIMESTAMP NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_ranks (uuid, rank_id, expires_at)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), 0, null);
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
    public void setRank(OfflinePlayer player, int rankId, Integer days) {
        ValidateParameter.validatePlayer(player);
        Instant expires = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;
        db().update("""
            UPDATE player_ranks
            SET rank_id = ?,
                expires_at = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """, rankId, expires, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setRankAsync(OfflinePlayer player, int rankId, Integer days) {
        ValidateParameter.validatePlayer(player);
        Instant expires = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;
        return db().updateAsync("""
            UPDATE player_ranks
            SET rank_id = ?,
                expires_at = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """, rankId, expires, player.getUniqueId().toString());
    }

    @Override
    public void increaseRankDays(OfflinePlayer player, int daysToAdd) {
        ValidateParameter.validatePlayer(player);
        if (daysToAdd > 0) shiftRankDays(player, daysToAdd);
    }

    @Override
    public CompletableFuture<Void> increaseRankDaysAsync(OfflinePlayer player, int daysToAdd) {
        ValidateParameter.validatePlayer(player);
        if (daysToAdd <= 0) return CompletableFuture.completedFuture(null);
        return shiftRankDaysAsync(player, daysToAdd);
    }

    @Override
    public void decreaseRankDays(OfflinePlayer player, int daysToRemove) {
        ValidateParameter.validatePlayer(player);
        if (daysToRemove > 0) shiftRankDays(player, -daysToRemove);
    }

    @Override
    public CompletableFuture<Void> decreaseRankDaysAsync(OfflinePlayer player, int daysToRemove) {
        ValidateParameter.validatePlayer(player);
        if (daysToRemove <= 0) return CompletableFuture.completedFuture(null);
        return shiftRankDaysAsync(player, -daysToRemove);
    }

    @Override
    public Integer getRankId(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "rank_id", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getRankIdAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "rank_id", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getExpiresAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "expires_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getExpiresAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "expires_at", "uuid = ?", player.getUniqueId().toString());
    }

    private void shiftRankDays(OfflinePlayer player, int days) {
        ValidateParameter.validatePlayer(player);
        if (days == 0) return;

        Timestamp current = db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT expires_at FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("expires_at");
                    return null;
                }
            }
        });

        Instant base = (current == null) ? Instant.now() : current.toInstant();
        Instant newExpires = base.plus(days, ChronoUnit.DAYS);

        repo().updateColumn(TABLE, "expires_at", newExpires, "uuid = ?", player.getUniqueId().toString());
    }

    private CompletableFuture<Void> shiftRankDaysAsync(OfflinePlayer player, int days) {
        ValidateParameter.validatePlayer(player);
        if (days == 0) return CompletableFuture.completedFuture(null);

        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT expires_at FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("expires_at");
                    return null;
                }
            }
        }).thenCompose(current -> {
            Instant base = (current == null) ? Instant.now() : current.toInstant();
            Instant newExpires = base.plus(days, ChronoUnit.DAYS);
            return repo().updateColumnAsync(TABLE, "expires_at", newExpires, "uuid = ?", player.getUniqueId().toString());
        });
    }
}