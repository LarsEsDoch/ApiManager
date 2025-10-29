package de.lars.apimanager.apis.banAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

public class BanAPIImpl implements IBanAPI {
    private final DatabaseManager db;

    public BanAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_bans (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                is_banned BOOLEAN DEFAULT FALSE,
                reason VARCHAR(255) DEFAULT '',
                banned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                expires_at TIMESTAMP NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO player_bans (uuid, is_banned, reason, banned_at, expires_at)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), "", null, null, false);
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
    public Timestamp getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return getTimestamp(player, "created_at");
    }

    @Override
    public CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return getTimestampAsync(player, "created_at");
    }

    @Override
    public Timestamp getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return getTimestamp(player, "updated_at");
    }

    @Override
    public CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return getTimestampAsync(player, "updated_at");
    }

    private Timestamp getTimestamp(OfflinePlayer player, String column) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM player_bans WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getTimestamp(column) : null;
                }
            }
        });
    }


    @Override
    public void setBanned(OfflinePlayer player, String reason, Integer days) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateReason(reason);
        Instant expiresAt = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;

        db.update("""
            UPDATE player_bans
            SET reason = ?, expires_at = ?, is_banned = TRUE, banned_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """, reason, expiresAt != null ? Timestamp.from(expiresAt) : null, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBannedAsync(OfflinePlayer player, String reason, Integer days) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateReason(reason);
        Instant expiresAt = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;

        return db.updateAsync("""
            UPDATE player_bans
            SET reason = ?, expires_at = ?, is_banned = TRUE, banned_at = CURRENT_TIMESTAMP
            WHERE uuid = ?
        """, reason, expiresAt != null ? Timestamp.from(expiresAt) : null, player.getUniqueId().toString());
    }

    @Override
    public void setUnBanned(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_bans SET is_banned = FALSE, expires_at = NULL, reason = '' WHERE uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setUnBannedAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_bans SET is_banned = FALSE, expires_at = NULL, reason = '' WHERE uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setReason(OfflinePlayer player, String reason) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateReason(reason);
        db.update("UPDATE player_bans SET reason = ? WHERE uuid = ?", reason, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setReasonAsync(OfflinePlayer player, String reason) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateReason(reason);
        return db.updateAsync("UPDATE player_bans SET reason = ? WHERE uuid = ?", reason, player.getUniqueId().toString());
    }

    @Override
    public String getReason(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return getString(player, "reason");
    }

    @Override
    public CompletableFuture<String> getReasonAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return getStringAsync(player, "reason");
    }

    @Override
    public void setDays(OfflinePlayer player, Integer days) {
        ValidateParameter.validatePlayer(player);
        Instant expiresAt = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;
        db.update("UPDATE player_bans SET expires_at = ? WHERE uuid = ?", expiresAt != null ? Timestamp.from(expiresAt) : null, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setDaysAsync(OfflinePlayer player, Integer days) {
        ValidateParameter.validatePlayer(player);
        Instant expiresAt = (days != null && days > 0)
                ? Instant.now().plus(days, ChronoUnit.DAYS)
                : null;
        return db.updateAsync("UPDATE player_bans SET expires_at = ? WHERE uuid = ?", expiresAt != null ? Timestamp.from(expiresAt) : null, player.getUniqueId().toString());
    }

    @Override
    public Timestamp getDays(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return getTimestamp(player, "expires_at");
    }

    @Override
    public CompletableFuture<Timestamp> getDaysAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return getTimestampAsync(player, "expires_at");
    }

    @Override
    public boolean isBanned(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Boolean result = db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT is_banned FROM player_bans WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("is_banned");
                    return false;
                }
            }
        });
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isBannedAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT is_banned FROM player_bans WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("is_banned");
                    return false;
                }
            }
        });
    }

    private String getString(OfflinePlayer player, String column) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM player_bans WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getString(column) : null;
                }
            }
        });
    }

    private CompletableFuture<String> getStringAsync(OfflinePlayer player, String column) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM player_bans WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getString(column) : null;
                }
            }
        });
    }

    private CompletableFuture<Timestamp> getTimestampAsync(OfflinePlayer player, String column) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM player_bans WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getTimestamp(column) : null;
                }
            }
        });
    }
}