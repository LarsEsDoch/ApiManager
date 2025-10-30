package de.lars.apimanager.apis.limitAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class LimitAPIImpl implements ILimitAPI {
    private final DatabaseManager db;

    public LimitAPIImpl() {
        this.db = ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_limits (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                slots INT DEFAULT 9,
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
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_limits WHERE uuid = ? LIMIT 1"
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
                     "SELECT created_at FROM player_limits WHERE uuid = ? LIMIT 1"
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
                     "SELECT updated_at FROM player_limits WHERE uuid = ? LIMIT 1"
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
                     "SELECT updated_at FROM player_limits WHERE uuid = ? LIMIT 1"
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
    public void setSlots(OfflinePlayer player, int slots) {
        ValidateParameter.validatePlayer(player);
        if (slots < 0) slots = 0;
        db.update("UPDATE player_limits SET slots = ? WHERE uuid = ? LIMIT 1",
                slots, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setSlotsAsync(OfflinePlayer player, int slots) {
        ValidateParameter.validatePlayer(player);
        if (slots < 0) slots = 0;
        return db.updateAsync("UPDATE player_limits SET slots = ? WHERE uuid = ? LIMIT 1",
                        slots, player.getUniqueId().toString());
    }

    @Override
    public void increaseSlots(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getSlots(player);
        if (current == null) current = 0;
        setSlots(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseSlotsAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getSlotsAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setSlotsAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseSlots(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getSlots(player);
        if (current == null) current = 0;
        setSlots(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseSlotsAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getSlotsAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setSlotsAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getSlots(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT slots FROM player_limits WHERE uuid = ? LIMIT 1")) {
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
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT slots FROM player_limits WHERE uuid = ? LIMIT 1")) {
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
        ValidateParameter.validatePlayer(player);
        db.update("""
            UPDATE player_limits
            SET chunk_limit = ?
            WHERE uuid = ?
        """, chunk_limit, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setChunkLimitAsync(OfflinePlayer player, Integer chunk_limit) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("""
            UPDATE player_limits
            SET chunk_limit = ?
            WHERE uuid = ?
        """, chunk_limit, player.getUniqueId().toString());
    }

    @Override
    public void increaseChunkLimit(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getChunkLimit(player);
        if (current == null) current = 0;
        setChunkLimit(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseChunkLimitAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getChunkLimitAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setChunkLimitAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseChunkLimit(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getChunkLimit(player);
        if (current == null) current = 0;
        setChunkLimit(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseChunkLimitAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getChunkLimitAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setChunkLimitAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getChunkLimit(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT chunk_limit FROM player_limits WHERE uuid = ? LIMIT 1")) {
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
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT chunk_limit FROM player_limits WHERE uuid = ? LIMIT 1")) {
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
        ValidateParameter.validatePlayer(player);
        db.update("""
            UPDATE player_limits
            SET home_limit = ?
            WHERE uuid = ?
        """, home_limit, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setHomeLimitAsync(OfflinePlayer player, Integer home_limit) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("""
            UPDATE player_limits
            SET home_limit = ?
            WHERE uuid = ?
        """, home_limit, player.getUniqueId().toString());
    }

    @Override
    public void increaseHomeLimit(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getHomeLimit(player);
        if (current == null) return;
        setHomeLimit(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseHomeLimitAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getHomeLimitAsync(player).thenCompose(current -> {
            if (current == null) return CompletableFuture.completedFuture(null);
            return setHomeLimitAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseHomeLimit(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getHomeLimit(player);
        if (current == null) return;
        setHomeLimit(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseHomeLimitAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getHomeLimitAsync(player).thenCompose(current -> {
            if (current == null) return CompletableFuture.completedFuture(null);
            return setHomeLimitAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getHomeLimit(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT home_limit FROM player_limits WHERE uuid = ? LIMIT 1")) {
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
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT home_limit FROM player_limits WHERE uuid = ? LIMIT 1")) {
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