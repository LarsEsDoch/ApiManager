package de.lars.apimanager.apis.statusAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.TextFormation;
import de.lars.apimanager.utils.ValidateParameter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class StatusAPIImpl implements IStatusAPI {
    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_status (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                status VARCHAR(255) DEFAULT '',
                color INT DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_status (uuid, status, color)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), "", 0);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_status WHERE uuid = ? LIMIT 1")) {
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
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_status WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp("created_at");
                        if (ts != null) {
                            return ts.toInstant();
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_status WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp("created_at");
                        if (ts != null) {
                            return ts.toInstant();
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_status WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp("updated_at");
                        if (ts != null) {
                            return ts.toInstant();
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_status WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp("updated_at");
                        if (ts != null) {
                            return ts.toInstant();
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public void setStatus(OfflinePlayer player, String status) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateStatus(status);
        db().update("UPDATE player_status SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                status, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setStatusAsync(OfflinePlayer player, String status) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateStatus(status);
        return db().updateAsync("UPDATE player_status SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                status, player.getUniqueId().toString());
    }

    @Override
    public String getStatus(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT status FROM player_status WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("status");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getStatusAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT status FROM player_status WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("status");
                    return null;
                }
            }
        });
    }

    @Override
    public void setColor(OfflinePlayer player, NamedTextColor color) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNamedTextColor(color);
        db().update("UPDATE player_status SET color = ? WHERE uuid = ? LIMIT 1",
                TextFormation.getColorId(color), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNamedTextColor(color);
        return db().updateAsync("UPDATE player_status SET color = ? WHERE uuid = ? LIMIT 1",
                        TextFormation.getColorId(color), player.getUniqueId().toString());
    }

    @Override
    public NamedTextColor getColor(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return TextFormation.getNamedTextColor(Objects.requireNonNull(db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT color FROM player_status WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int color = rs.getInt("color");
                        return rs.wasNull() ? null : color;
                    } else {
                        return null;
                    }
                }
            }
        })));
    }

    @Override
    public CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT color FROM player_status WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int color = rs.getInt("color");
                        return rs.wasNull() ? null : color;
                    }
                    return null;
                }
            }
        }).thenApply(prefixID -> prefixID != null ? TextFormation.getNamedTextColor(prefixID) : null);
    }
}