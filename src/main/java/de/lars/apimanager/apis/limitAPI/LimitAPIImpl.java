package de.lars.apimanager.apis.limitAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public class LimitAPIImpl implements ILimitAPI {
    private final DatabaseManager db;

    public LimitAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_limits (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                slots INT NOT NULL DEFAULT 9,
                chunk_limit INT DEFAULT 32,
                home_limit INT DEFAULT 32,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO player_limits (uuid, slots, chunk_limit, home_limit)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), 9, 32, 32);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_limits WHERE uuid = ? LIMIT 1")) {
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
                     "SELECT created_at FROM player_limits WHERE uuid = ?"
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
                     "SELECT created_at FROM player_limits WHERE uuid = ?"
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
                     "SELECT updated_at FROM player_limits WHERE uuid = ?"
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
                     "SELECT updated_at FROM player_limits WHERE uuid = ?"
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
    public void setSlots(OfflinePlayer player, int slots) {
        if (slots < 0) slots = 0;
        db.update("UPDATE player_limits SET slots = ? WHERE uuid = ?",
                slots, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setSlotsAsync(OfflinePlayer player, int slots) {
        if (slots < 0) slots = 0;
        return db.updateAsync("UPDATE player_limits SET slots = ? WHERE uuid = ?",
                        slots, player.getUniqueId().toString());
    }

    @Override
    public void increaseSlots(OfflinePlayer player, int amount) {
        Integer current = getSlots(player);
        if (current == null) current = 0;
        setSlots(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseSlotsAsync(OfflinePlayer player, int amount) {
        return getSlotsAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setSlotsAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseSlots(OfflinePlayer player, int amount) {
        Integer current = getSlots(player);
        if (current == null) current = 0;
        setSlots(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseSlotsAsync(OfflinePlayer player, int amount) {
        return getSlotsAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setSlotsAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getSlots(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT slots FROM player_limits WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int slots = rs.getInt("slots");
                        return rs.wasNull() ? null : slots;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getSlotsAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT slots FROM player_limits WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int slots = rs.getInt("slots");
                        return rs.wasNull() ? null : slots;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public void setChunkLimit(OfflinePlayer player, Integer chunk_limit) {
        db.update("""
            UPDATE player_limits
            SET chunk_limit = ?
            WHERE uuid = ?
        """, chunk_limit, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setChunkLimitAsync(OfflinePlayer player, Integer chunk_limit) {
        return db.updateAsync("""
            UPDATE player_limits
            SET chunk_limit = ?
            WHERE uuid = ?
        """, chunk_limit, player.getUniqueId().toString());
    }

    @Override
    public void increaseChunkLimit(OfflinePlayer player, int amount) {
        Integer current = getChunkLimit(player);
        if (current == null) current = 0;
        setChunkLimit(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseChunkLimitAsync(OfflinePlayer player, int amount) {
        return getChunkLimitAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setChunkLimitAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseChunkLimit(OfflinePlayer player, int amount) {
        Integer current = getChunkLimit(player);
        if (current == null) current = 0;
        setChunkLimit(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseChunkLimitAsync(OfflinePlayer player, int amount) {
        return getChunkLimitAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setChunkLimitAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getChunkLimit(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT chunk_limit FROM player_limits WHERE uuid = ?")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT chunk_limit FROM player_limits WHERE uuid = ?")) {
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
            UPDATE player_limits
            SET home_limit = ?
            WHERE uuid = ?
        """, home_limit, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setHomeLimitAsync(OfflinePlayer player, Integer home_limit) {
        return db.updateAsync("""
            UPDATE player_limits
            SET home_limit = ?
            WHERE uuid = ?
        """, home_limit, player.getUniqueId().toString());
    }

    @Override
    public void increaseHomeLimit(OfflinePlayer player, int amount) {
        Integer current = getHomeLimit(player);
        if (current == null) current = 0;
        setHomeLimit(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseHomeLimitAsync(OfflinePlayer player, int amount) {
        return getHomeLimitAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setHomeLimitAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseHomeLimit(OfflinePlayer player, int amount) {
        Integer current = getHomeLimit(player);
        if (current == null) current = 0;
        setHomeLimit(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseHomeLimitAsync(OfflinePlayer player, int amount) {
        return getHomeLimitAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setHomeLimitAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getHomeLimit(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT home_limit FROM player_limits WHERE uuid = ?")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT home_limit FROM player_limits WHERE uuid = ?")) {
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
}