package de.lars.apimanager.apis.courtAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public class CourtAPIImpl implements ICourtAPI {
    private final DatabaseManager db;

    public static final int STATUS_FREE = 0;
    public static final int STATUS_REPORTED = 1;
    public static final int STATUS_WAITING = 2;
    public static final int STATUS_COURT = 3;
    public static final int STATUS_LOCKED = 4;
    public static final int STATUS_JAILED = 5;

    public CourtAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_court (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                prosecutor CHAR(36) DEFAULT NULL,
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
        db.update("""
            INSERT IGNORE INTO player_court (uuid, prosecutor, status, reason, time, cell)
            VALUES (?, NULL, ?, NULL, ?, ?)
        """, player.getUniqueId().toString(), STATUS_FREE, 0, 0);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM player_court WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_court WHERE uuid = ?"
                 )) {
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
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_court WHERE uuid = ?"
                 )) {
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
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM player_court WHERE uuid = ?"
                 )) {
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
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM player_court WHERE uuid = ?"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public void report(OfflinePlayer target, OfflinePlayer prosecutor, String reason) {
        db.update("""
            UPDATE player_court
            SET prosecutor = ?, reason = ?, status = ?, updated_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """,
        prosecutor != null ? prosecutor.getUniqueId().toString() : null,
        reason,
        STATUS_REPORTED,
        target.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> reportAsync(OfflinePlayer target, OfflinePlayer prosecutor, String reason) {
        return db.updateAsync("""
            UPDATE player_court
            SET prosecutor = ?, reason = ?, status = ?, updated_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """,
        prosecutor != null ? prosecutor.getUniqueId().toString() : null,
        reason,
        STATUS_REPORTED,
        target.getUniqueId().toString());
    }

    @Override
    public void setStatus(OfflinePlayer player, int status) {
        db.update("UPDATE player_court SET status = ? WHERE uuid = ?", status, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setStatusAsync(OfflinePlayer player, int status) {
        return db.updateAsync("UPDATE player_court SET status = ? WHERE uuid = ?", status, player.getUniqueId().toString());
    }

    @Override
    public Integer getStatus(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT status FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("status");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getStatusAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT status FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("status");
                    return null;
                }
            }
        });
    }

    @Override
    public void setReason(OfflinePlayer player, String reason) {
        db.update("UPDATE player_court SET reason = ? WHERE uuid = ?", reason, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setReasonAsync(OfflinePlayer player, String reason) {
        return db.updateAsync("UPDATE player_court SET reason = ? WHERE uuid = ?", reason, player.getUniqueId().toString());
    }

    @Override
    public String getReason(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT reason FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("reason");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getReasonAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT reason FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("reason");
                    return null;
                }
            }
        });
    }

    @Override
    public void setProsecutor(OfflinePlayer player, OfflinePlayer prosecutor) {
        db.update("UPDATE player_court SET prosecutor = ? WHERE uuid = ?",
                prosecutor != null ? prosecutor.getUniqueId().toString() : null,
                player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setProsecutorAsync(OfflinePlayer player, OfflinePlayer prosecutor) {
        return db.updateAsync("UPDATE player_court SET prosecutor = ? WHERE uuid = ?",
                prosecutor != null ? prosecutor.getUniqueId().toString() : null,
                player.getUniqueId().toString());
    }

    @Override
    public String getProsecutor(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT prosecutor FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("prosecutor");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getProsecutorAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT prosecutor FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("prosecutor");
                    return null;
                }
            }
        });
    }

    @Override
    public void setTime(OfflinePlayer player, int time) {
        db.update("UPDATE player_court SET time = ? WHERE uuid = ?", time, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setTimeAsync(OfflinePlayer player, int time) {
        return db.updateAsync("UPDATE player_court SET time = ? WHERE uuid = ?", time, player.getUniqueId().toString());
    }

    @Override
    public Integer getTime(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT time FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("time");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getTimeAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT time FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("time");
                    return null;
                }
            }
        });
    }

    @Override
    public void setCell(OfflinePlayer player, int cell) {
        db.update("UPDATE player_court SET cell = ? WHERE uuid = ?", cell, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setCellAsync(OfflinePlayer player, int cell) {
        return db.updateAsync("UPDATE player_court SET cell = ? WHERE uuid = ?", cell, player.getUniqueId().toString());
    }

    @Override
    public Integer getCell(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT cell FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("cell");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getCellAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT cell FROM player_court WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("cell");
                    return null;
                }
            }
        });
    }

    @Override
    public void resetPlayer(OfflinePlayer player) {
        db.update("""
            UPDATE player_court
            SET status = 0, reason = NULL, prosecutor = NULL, time = 0, cell = 0
            WHERE uuid = ?
        """, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetPlayerAsync(OfflinePlayer player) {
        return db.updateAsync("""
            UPDATE player_court
            SET status = 0, reason = NULL, prosecutor = NULL, time = 0, cell = 0
            WHERE uuid = ?
        """, player.getUniqueId().toString());
    }
}