package de.lars.apimanager.apis.toggleAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ToggleAPIImpl implements IToggleAPI {
    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_toggles (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                bed_toggle BOOLEAN DEFAULT TRUE,
                scoreboard_toggle BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_toggles (uuid, bed_toggle, scoreboard_toggle)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), true, true);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_toggles WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_toggles WHERE uuid = ? LIMIT 1")) {
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
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_toggles WHERE uuid = ? LIMIT 1")) {
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
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_toggles WHERE uuid = ? LIMIT 1")) {
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
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_toggles WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public void setBedToggle(OfflinePlayer player, boolean toggle) {
        ValidateParameter.validatePlayer(player);
        db().update("UPDATE player_toggles SET bed_toggle=? WHERE uuid=?", toggle, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBedToggleAsync(OfflinePlayer player, boolean toggle) {
        ValidateParameter.validatePlayer(player);
        return db().updateAsync("UPDATE player_toggles SET bed_toggle=? WHERE uuid=?", toggle, player.getUniqueId().toString());
    }

    @Override
    public boolean getBedToggle(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT bed_toggle FROM player_toggles WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("bed_toggle");
                }
            }
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> getBedToggleAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT bed_toggle FROM player_toggles WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("bed_toggle");
                }
            }
            return true;
        });
    }

    @Override
    public void setScoreboardToggle(OfflinePlayer player, boolean toggle) {
        ValidateParameter.validatePlayer(player);
        db().update("UPDATE player_toggles SET scoreboard_toggle=? WHERE uuid=?", toggle, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setScoreboardToggleAsync(OfflinePlayer player, boolean toggle) {
        ValidateParameter.validatePlayer(player);
        return db().updateAsync("UPDATE player_toggles SET scoreboard_toggle=? WHERE uuid=?", toggle, player.getUniqueId().toString());
    }

    @Override
    public boolean getScoreboardToggle(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT scoreboard_toggle FROM player_toggles WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("scoreboard_toggle");
                }
            }
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> getScoreboardToggleAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT scoreboard_toggle FROM player_toggles WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("scoreboard_toggle");
                }
            }
            return true;
        });
    }
}