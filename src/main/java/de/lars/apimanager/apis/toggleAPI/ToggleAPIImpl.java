package de.lars.apimanager.apis.toggleAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public class ToggleAPIImpl implements IToggleAPI {
    private final DatabaseManager db;

    public ToggleAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_toggles (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                bed_toggle TINYINT(1) DEFAULT 1,
                scoreboard_toggle TINYINT(1) DEFAULT 1,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO player_toggles (uuid, bed_toggle, scoreboard_toggle)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), 1, 1);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_toggles WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_toggles WHERE uuid = ?")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_toggles WHERE uuid = ?")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_toggles WHERE uuid = ?")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_toggles WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public void setBedToggle(OfflinePlayer player, boolean toggle) {
        db.update("UPDATE player_toggles SET bed_toggle=? WHERE uuid=?", toggle ? 1 : 0, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBedToggleAsync(OfflinePlayer player, boolean toggle) {
        return db.updateAsync("UPDATE player_toggles SET bed_toggle=? WHERE uuid=?", toggle ? 1 : 0, player.getUniqueId().toString());
    }

    @Override
    public boolean getBedToggle(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT bed_toggle FROM player_toggles WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("bed_toggle") == 1;
                }
            }
            return true; // Default true
        });
    }

    @Override
    public CompletableFuture<Boolean> getBedToggleAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT bed_toggle FROM player_toggles WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("bed_toggle") == 1;
                }
            }
            return true;
        });
    }

    @Override
    public void setScoreboardToggle(OfflinePlayer player, boolean toggle) {
        db.update("UPDATE player_toggles SET scoreboard_toggle=? WHERE uuid=?", toggle ? 1 : 0, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setScoreboardToggleAsync(OfflinePlayer player, boolean toggle) {
        return db.updateAsync("UPDATE player_toggles SET scoreboard_toggle=? WHERE uuid=?", toggle ? 1 : 0, player.getUniqueId().toString());
    }

    @Override
    public boolean getScoreboardToggle(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT scoreboard_toggle FROM player_toggles WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("scoreboard_toggle") == 1;
                }
            }
            return true; // Default true
        });
    }

    @Override
    public CompletableFuture<Boolean> getScoreboardToggleAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT scoreboard_toggle FROM player_toggles WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("scoreboard_toggle") == 1;
                }
            }
            return true;
        });
    }
}