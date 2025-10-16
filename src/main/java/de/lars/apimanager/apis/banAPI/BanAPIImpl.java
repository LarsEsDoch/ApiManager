package de.lars.apimanager.apis.banAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

public class BanAPIImpl implements IBanAPI {

    private final DatabaseManager db;

    public BanAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_bans (
                uuid CHAR(36) NOT NULL FOREIGN KEY REFERENCES players(uuid) ON DELETE CASCADE,
                reason VARCHAR DEFAULT '',
                banned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                expires_at TIMESTAMP NULL,
                active BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO player_bans (uuid, reason, active)
            VALUES (?, '', FALSE)
        """, player.getUniqueId().toString());
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_bans WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public void setBanned(OfflinePlayer player, String reason, Integer days) {
        UUID uuid = player.getUniqueId();
        Instant expiresAt = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;

        db.update("""
            INSERT INTO player_bans (uuid, reason, expires_at, active, banned_at)
            VALUES (?, ?, ?, TRUE, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                reason = VALUES(reason),
                expires_at = VALUES(expires_at),
                active = TRUE,
                banned_at = CURRENT_TIMESTAMP
        """, uuid.toString(), reason, expiresAt != null ? Timestamp.from(expiresAt) : null);
    }

    public CompletableFuture<Void> setBannedAsync(OfflinePlayer player, String reason, Integer days) {
        UUID uuid = player.getUniqueId();
        Instant expiresAt = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;

        return db.updateAsync("""
            INSERT INTO player_bans (uuid, reason, expires_at, active, banned_at)
            VALUES (?, ?, ?, TRUE, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                reason = VALUES(reason),
                expires_at = VALUES(expires_at),
                active = TRUE,
                banned_at = CURRENT_TIMESTAMP
        """, uuid.toString(), reason, expiresAt != null ? Timestamp.from(expiresAt) : null);
    }

    @Override
    public void setUnBanned(OfflinePlayer player) {
        db.update("""
            UPDATE player_bans
            SET active = FALSE, expires_at = CURRENT_TIMESTAMP, reason = ""
            WHERE uuid = ?
        """, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setUnBannedAsync(OfflinePlayer player) {
        return db.updateAsync("""
            UPDATE player_bans
            SET active = FALSE, expires_at = CURRENT_TIMESTAMP, reason = ""
            WHERE uuid = ?
        """, player.getUniqueId().toString());
    }

    @Override
    public boolean isBanned(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT active, expires_at FROM player_bans WHERE uuid = ? AND active = TRUE")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean active = rs.getBoolean("active");
                        Timestamp expiresAt = rs.getTimestamp("expires_at");

                        if (!active) return false;
                        if (expiresAt == null) return true;
                        return true;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isBannedAsync(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT active, expires_at FROM player_bans WHERE uuid = ? AND active = TRUE")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean active = rs.getBoolean("active");
                        Timestamp expiresAt = rs.getTimestamp("expires_at");

                        if (!active) return false;
                        if (expiresAt == null) return true;
                        return true;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public void setReason(OfflinePlayer player, String reason) {
        UUID uuid = player.getUniqueId();

        db.update("""
            INSERT INTO player_bans (uuid, reason)
            VALUES (?, ?, ?, TRUE, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                reason = VALUES(reason),
        """, uuid.toString(), reason);
    }

    @Override
    public CompletableFuture<Void> setReasonAsync(OfflinePlayer player, String reason) {
        UUID uuid = player.getUniqueId();

        return db.updateAsync("""
            INSERT INTO player_bans (uuid, reason)
            VALUES (?, ?, ?, TRUE, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                reason = VALUES(reason),
        """, uuid.toString(), reason);
    }

    @Override
    public String getReason(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT reason FROM player_bans WHERE uuid = ? AND active = TRUE")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("reason");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getReasonAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT reason FROM player_bans WHERE uuid = ? AND active = TRUE")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("reason");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setDays(OfflinePlayer player, Integer days) {
        UUID uuid = player.getUniqueId();
        Instant expiresAt = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;

        db.update("""
            INSERT INTO player_bans (uuid, expires_at)
            VALUES (?, ?, ?, TRUE, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                expires_at = VALUES(expires_at),
        """, uuid.toString(), expiresAt != null ? Timestamp.from(expiresAt) : null);
    }

    @Override
    public CompletableFuture<Void> setDaysAsync(OfflinePlayer player, Integer days) {
        UUID uuid = player.getUniqueId();
        Instant expiresAt = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;

        return db.updateAsync("""
            INSERT INTO player_bans (uuid, expires_at)
            VALUES (?, ?, ?, TRUE, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                expires_at = VALUES(expires_at),
        """, uuid.toString(), expiresAt != null ? Timestamp.from(expiresAt) : null);
    }

    @Override
    public Timestamp getDays(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT reason FROM player_bans WHERE uuid = ? AND active = TRUE")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("expires_at");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getDaysAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT reason FROM player_bans WHERE uuid = ? AND active = TRUE")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("expires_at");
                    else return null;
                }
            }
        });
    }
}