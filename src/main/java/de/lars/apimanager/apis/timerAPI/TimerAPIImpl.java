package de.lars.apimanager.apis.timerAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class TimerAPIImpl implements ITimerAPI {
    private final DatabaseManager db;

    public TimerAPIImpl() {
        this.db = ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_timers (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                time INT NOT NULL DEFAULT 0,
                off BOOLEAN NOT NULL DEFAULT 1,
                public_timer BOOLEAN NOT NULL DEFAULT 0,
                running BOOLEAN NOT NULL DEFAULT 0,
                timer_enabled BOOLEAN NOT NULL DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO player_timers (uuid, time, off, public_timer, running, timer_enabled)
            VALUES (?, ?, ?, ?, ?, ?)
        """, player.getUniqueId().toString(), 0, true, false, false, false);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_timers WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_timers WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_timers WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_timers WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public void setTime(OfflinePlayer player, int time) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_timers SET time = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                time, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setTimeAsync(OfflinePlayer player, int time) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_timers SET time = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                time, player.getUniqueId().toString());
    }

    @Override
    public void setOff(OfflinePlayer player, boolean off) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_timers SET off = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                off, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setOffAsync(OfflinePlayer player, boolean off) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_timers SET off = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                off, player.getUniqueId().toString());
    }

    @Override
    public void setRunning(OfflinePlayer player, boolean running) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_timers SET running = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                running, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setRunningAsync(OfflinePlayer player, boolean running) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_timers SET running = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                running, player.getUniqueId().toString());
    }

    @Override
    public void setTimer(OfflinePlayer player, boolean timer) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_timers SET timer_enabled = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                timer, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setTimerAsync(OfflinePlayer player, boolean timer) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_timers SET timer_enabled = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                timer, player.getUniqueId().toString());
    }

    @Override
    public void setPublic(OfflinePlayer player, boolean isPublic) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_timers SET public_timer = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                isPublic, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setPublicAsync(OfflinePlayer player, boolean isPublic) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_timers SET public_timer = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ? LIMIT 1",
                isPublic, player.getUniqueId().toString());
    }

    @Override
    public Integer getTime(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT time FROM player_timers WHERE uuid = ? LIMIT 1")) {
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
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT time FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("time");
                    return null;
                }
            }
        });
    }

    @Override
    public boolean isPublic(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT public_timer FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("public_timer");
                    return false;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isPublicAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT public_timer FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("public_timer");
                    return false;
                }
            }
        });
    }

    @Override
    public boolean isOff(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT off FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("off");
                    return false;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isOffAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT off FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("off");
                    return false;
                }
            }
        });
    }

    @Override
    public boolean isRunning(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT running FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("running");
                    return false;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isRunningAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT running FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("running");
                    return false;
                }
            }
        });
    }

    @Override
    public boolean isTimerEnabled(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT timer_enabled FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("timer_enabled");
                    return false;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isTimerEnabledAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT timer_enabled FROM player_timers WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("timer_enabled");
                    return false;
                }
            }
        });
    }

    @Override
    public boolean publicTimerExists(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM player_timers WHERE uuid = ? AND public_timer = 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("cnt") > 0;
                    return false;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> publicTimerExistsAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM player_timers WHERE uuid = ? AND public_timer = 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("cnt") > 0;
                    return false;
                }
            }
        });
    }
}