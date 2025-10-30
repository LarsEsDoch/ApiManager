package de.lars.apimanager.apis.playerAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class PlayerAPIImpl implements IPlayerAPI {
    private final DatabaseManager db;

    public PlayerAPIImpl() {
        this.db = ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS players (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                name VARCHAR(16) NOT NULL,
                playtime BIGINT DEFAULT 0,
                is_online BOOLEAN DEFAULT FALSE,
                first_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO players (uuid, name, playtime, is_online)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), player.getName(), 0, false);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM players WHERE uuid = ? LIMIT 1"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM players WHERE uuid = ? LIMIT 1"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM players WHERE uuid = ? LIMIT 1"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM players WHERE uuid = ? LIMIT 1"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public String getName(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("name");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getNameAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("name");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setPlaytime(OfflinePlayer player, long playtime) {
        ValidateParameter.validatePlayer(player);
        db.update("""
            UPDATE players
            SET playtime = ?
            WHERE uuid = ?
        """, playtime, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setPlaytimeAsync(OfflinePlayer player, long playtime) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("""
            UPDATE players
            SET playtime = ?
            WHERE uuid = ?
        """, playtime, player.getUniqueId().toString());
    }

    @Override
    public Long getPlaytime(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT playtime FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        long value = rs.getLong("playtime");
                        return rs.wasNull() ? null : value;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public CompletableFuture<Long> getPlaytimeAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT playtime FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        long value = rs.getLong("playtime");
                        return rs.wasNull() ? null : value;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public void setOnline(OfflinePlayer player, boolean online) {
        ValidateParameter.validatePlayer(player);
        db.update("""
            UPDATE players
            SET is_online = ?,
                name = ?
            WHERE uuid = ?
        """, online, player.getName(), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setOnlineAsync(OfflinePlayer player, boolean online) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("""
            UPDATE players
            SET is_online = ?,
                name = ?
            WHERE uuid = ?
        """, online, player.getName(), player.getUniqueId().toString());
    }

    @Override
    public boolean isOnline(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT is_online FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("is_online");
                    else return false;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isOnlineAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT is_online FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("is_online");
                    else return false;
                }
            }
        });
    }

    @Override
    public Instant getLastSeen(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT last_seen FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("last_seen").toInstant();
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getLastSeenAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT last_seen FROM players WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("last_seen").toInstant();
                    else return null;
                }
            }
        });
    }
}