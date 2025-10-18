package de.lars.apimanager.apis.playerAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public class PlayerAPIImpl implements IPlayerAPI {
    private final DatabaseManager db;

    public PlayerAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS players (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                name VARCHAR(16) NOT NULL,
                playtime BIGINT DEFAULT 0,
                chunk_limit INT DEFAULT 32,
                home_limit INT DEFAULT 32,
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
            INSERT IGNORE INTO players (uuid, name, playtime, chunk_limit, home_limit, is_online)
            VALUES (?, ?, ?, ?, ?, ?)
        """, player.getUniqueId().toString(), player.getName(), 0, 32, 32, true);
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
    public Timestamp getCreatedAt() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM server_settings WHERE id = 1"
                 );
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("created_at");
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getCreatedAtAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM server_settings WHERE id = 1"
                 );
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("created_at");
                return null;
            }
        });
    }

    @Override
    public Timestamp getUpdatedAt() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM server_settings WHERE id = 1"
                 );
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("updated_at");
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getUpdatedAtAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM server_settings WHERE id = 1"
                 );
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("updated_at");
                return null;
            }
        });
    }

    @Override
    public String getName(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM players WHERE uuid = ?")) {
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
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("name");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setPlaytime(OfflinePlayer player, Integer playtime) {
        db.update("""
            UPDATE players
            SET playtime = ?
            WHERE uuid = ?
        """, playtime, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setPlaytimeAsync(OfflinePlayer player, Integer playtime) {
        return db.updateAsync("""
            UPDATE players
            SET playtime = ?
            WHERE uuid = ?
        """, playtime, player.getUniqueId().toString());
    }

    @Override
    public Integer getPlaytime(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT playtime FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    int value = rs.getInt("playtime");
                    return rs.wasNull() ? null : value;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getPlaytimeAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT playtime FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    int value = rs.getInt("playtime");
                    return rs.wasNull() ? null : value;
                }
            }
        });
    }

    @Override
    public void setChunkLimit(OfflinePlayer player, Integer chunk_limit) {
        db.update("""
            UPDATE players
            SET chunk_limit = ?
            WHERE uuid = ?
        """, chunk_limit, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setChunkLimitAsync(OfflinePlayer player, Integer chunk_limit) {
        return db.updateAsync("""
            UPDATE players
            SET chunk_limit = ?
            WHERE uuid = ?
        """, chunk_limit, player.getUniqueId().toString());
    }

    @Override
    public Integer getChunkLimit(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT chunk_limit FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int value = rs.getInt("chunk_limit");
                        return rs.wasNull() ? null : value;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getChunkLimitAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT chunk_limit FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int value = rs.getInt("chunk_limit");
                        return rs.wasNull() ? null : value;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public void setHomeLimit(OfflinePlayer player, Integer home_limit) {
        db.update("""
            UPDATE players
            SET home_limit = ?
            WHERE uuid = ?
        """, home_limit, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setHomeLimitAsync(OfflinePlayer player, Integer home_limit) {
        return db.updateAsync("""
            UPDATE players
            SET home_limit = ?
            WHERE uuid = ?
        """, home_limit, player.getUniqueId().toString());
    }

    @Override
    public Integer getHomeLimit(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT home_limit FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int value = rs.getInt("home_limit");
                        return rs.wasNull() ? null : value;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getHomeLimitAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT home_limit FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int value = rs.getInt("home_limit");
                        return rs.wasNull() ? null : value;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public void setOnline(OfflinePlayer player, Boolean online) {
        db.update("""
            UPDATE players
            SET is_online = ?,
                name = ?
            WHERE uuid = ?
        """, online, player.getName(), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setOnlineAsync(OfflinePlayer player, Boolean online) {
        return db.updateAsync("""
            UPDATE players
            SET is_online = ?,
                name = ?
            WHERE uuid = ?
        """, online, player.getName(), player.getUniqueId().toString());
    }

    @Override
    public boolean isOnline(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT is_online FROM players WHERE uuid = ?")) {
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
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT is_online FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("is_online");
                    else return false;
                }
            }
        });
    }

    @Override
    public Timestamp getLastSeen(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT last_seen FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("last_seen");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getLastSeenAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT last_seen FROM players WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("last_seen");
                    else return null;
                }
            }
        });
    }
}