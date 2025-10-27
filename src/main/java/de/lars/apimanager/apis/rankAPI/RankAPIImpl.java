package de.lars.apimanager.apis.rankAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

public class RankAPIImpl implements IRankAPI {
    private final DatabaseManager db;

    public RankAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_ranks (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                rank_id INT DEFAULT 0,
                expires_at TIMESTAMP NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO player_ranks (uuid, rank_id, expires_at)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), 0, null);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_ranks WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public Timestamp getCreatedAt(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at");
                    return null;
                }
            }
        });
    }

    @Override
    public Timestamp getUpdatedAt(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public void setRank(OfflinePlayer player, Integer rankId, Integer days) {
        if (player == null || rankId == null) return;

        Instant expires = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;

        db.update("""
            UPDATE player_ranks
            SET rank_id = ?,
                expires_at = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """, rankId, expires == null ? null : Timestamp.from(expires), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setRankAsync(OfflinePlayer player, Integer rankId, Integer days) {
        if (player == null || rankId == null) return CompletableFuture.completedFuture(null);

        Instant expires = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;

        return db.updateAsync("""
            UPDATE player_ranks
            SET rank_id = ?,
                expires_at = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """, rankId, expires == null ? null : Timestamp.from(expires), player.getUniqueId().toString());
    }

    @Override
    public void addRankDays(OfflinePlayer player, int daysToAdd) {
        if (daysToAdd > 0) shiftRankDays(player, daysToAdd);
    }

    @Override
    public CompletableFuture<Void> addRankDaysAsync(OfflinePlayer player, int daysToAdd) {
        if (daysToAdd <= 0) return CompletableFuture.completedFuture(null);
        return shiftRankDaysAsync(player, daysToAdd);
    }

    @Override
    public void removeRankDays(OfflinePlayer player, int daysToRemove) {
        if (daysToRemove > 0) shiftRankDays(player, -daysToRemove);
    }

    @Override
    public CompletableFuture<Void> removeRankDaysAsync(OfflinePlayer player, int daysToRemove) {
        if (daysToRemove <= 0) return CompletableFuture.completedFuture(null);
        return shiftRankDaysAsync(player, -daysToRemove);
    }

    @Override
    public Integer getRankId(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT rank_id FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int v = rs.getInt("rank_id");
                        return rs.wasNull() ? null : v;
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getRankIdAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT rank_id FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int v = rs.getInt("rank_id");
                        return rs.wasNull() ? null : v;
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public Timestamp getExpiresAt(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT expires_at FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("expires_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getExpiresAtAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT expires_at FROM player_ranks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("expires_at");
                    return null;
                }
            }
        });
    }

    private void shiftRankDays(OfflinePlayer player, int days) {
        if (days == 0) return;

        Timestamp current = db.query(conn -> {
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

        db.update("""
            UPDATE player_ranks
            SET expires_at = ?, updated_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """, Timestamp.from(newExpires), player.getUniqueId().toString());
    }

    private CompletableFuture<Void> shiftRankDaysAsync(OfflinePlayer player, int days) {
        if (days == 0) return CompletableFuture.completedFuture(null);

        return db.queryAsync(conn -> {
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
            return db.updateAsync("""
                UPDATE player_ranks
                SET expires_at = ?, updated_at = CURRENT_TIMESTAMP
                WHERE uuid = ?
            """, Timestamp.from(newExpires), player.getUniqueId().toString());
        });
    }
}