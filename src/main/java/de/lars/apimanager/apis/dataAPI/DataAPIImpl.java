package de.lars.apimanager.apis.dataAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public class DataAPIImpl implements IDataAPI {
    private final DatabaseManager db;

    public DataAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS server_settings (
                id INT PRIMARY KEY,
                real_time_enabled BOOLEAN DEFAULT TRUE,
                real_weather_enabled BOOLEAN DEFAULT TRUE,
                maintenance_active BOOLEAN DEFAULT FALSE,
                maintenance_reason VARCHAR(255) DEFAULT '',
                maintenance_end TIMESTAMP DEFAULT NULL,
                max_players INT DEFAULT 100,
                server_name VARCHAR(255) DEFAULT 'A Minecraft Server',
                server_version VARCHAR(50) DEFAULT '1.21.10',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);

        if (countRowsInTable() < 1) {
            db.update("""
                INSERT INTO server_settings
                (id, real_time_enabled, real_weather_enabled, maintenance_active, maintenance_reason, maintenance_end, max_players, server_name, server_version)
                VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?)
            """, true, true, false, "", null, 1000000000, "A Server", "1.21.10");
        }
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
    public void setRealTimeEnabled(boolean enabled) {
        db.update("""
            UPDATE server_settings
            SET real_time_enabled = ?
        """, enabled);
    }

    @Override
    public CompletableFuture<Void> setRealTimeEnabledAsync(boolean enabled) {
        return db.updateAsync("""
            UPDATE server_settings
            SET real_time_enabled = ?
        """, enabled);
    }

    @Override
    public boolean isRealTimeActivated() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT real_time_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("real_time_enabled");
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isRealTimeActivatedAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT real_time_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("real_time_enabled");
                }
            }
        });
    }

    @Override
    public void setRealWeatherEnabled(boolean enabled) {
        db.update("""
            UPDATE server_settings
            SET real_weather_enabled = ?
        """, enabled);
    }

    @Override
    public CompletableFuture<Void> setRealWeatherEnabledAsync(boolean enabled) {
        return db.updateAsync("""
            UPDATE server_settings
            SET real_weather_enabled = ?
        """, enabled);
    }

    @Override
    public boolean isRealWeatherActivated() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT real_weather_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("real_weather_enabled");
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isRealWeatherActivatedAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT real_weather_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("real_weather_enabled");
                }
            }
        });
    }

    @Override
    public void activateMaintenance(String reason, Timestamp endTime) {
        db.update("""
            UPDATE server_settings
            SET maintenance_active = ?,
                maintenance_reason = ?,
                maintenance_end = ?
        """, true, reason, endTime);
    }

    @Override
    public CompletableFuture<Void> activateMaintenanceAsync(String reason, Timestamp endTime) {
        return db.updateAsync("""
            UPDATE server_settings
            SET maintenance_active = ?,
                maintenance_reason = ?,
                maintenance_end = ?
        """, true, reason, endTime);
    }

    @Override
    public void deactivateMaintenance() {
        db.update("""
            UPDATE server_settings
            SET maintenance_active = ?,
                maintenance_reason = ?,
                maintenance_end = ?
        """, false, "", null);
    }

    @Override
    public CompletableFuture<Void> deactivateMaintenanceAsync() {
        return db.updateAsync("""
            UPDATE server_settings
            SET maintenance_active = ?,
                maintenance_reason = ?,
                maintenance_end = ?
        """, false, "", null);
    }

    @Override
    public boolean isMaintenanceActive() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_active FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("maintenance_active");
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isMaintenanceActiveAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_active FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("maintenance_active");
                }
            }
        });
    }

    @Override
    public void setMaintenanceReason(String reason) {
        db.update("""
            UPDATE server_settings
            SET maintenance_reason = ?
        """, reason);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceReasonAsync(String reason) {
        return db.updateAsync("""
            UPDATE server_settings
            SET maintenance_reason = ?
        """, reason);
    }

    @Override
    public String getMaintenanceReason() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_reason FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("maintenance_reason");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getMaintenanceReasonAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_reason FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("maintenance_reason");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setMaintenanceEnd(Timestamp endTime) {
        db.update("""
            UPDATE server_settings
            SET maintenance_end = ?
        """, endTime);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceEndAsync(Timestamp endTime) {
        return db.updateAsync("""
            UPDATE server_settings
            SET maintenance_end = ?
        """, endTime);
    }

    @Override
    public Timestamp getMaintenanceEnd() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_end FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("maintenance_end");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getMaintenanceEndAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_end FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("maintenance_end");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        db.update("""
            UPDATE server_settings
            SET max_players = ?
        """, maxPlayers);
    }

    @Override
    public CompletableFuture<Void> setMaxPlayersAsync(int maxPlayers) {
        return db.updateAsync("""
            UPDATE server_settings
            SET max_players = ?
        """, maxPlayers);
    }

    @Override
    public int getMaxPlayers() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT max_players FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("max_players");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getMaxPlayersAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT max_players FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("max_players");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setServerName(String serverName) {
        db.update("""
            UPDATE server_settings
            SET server_name = ?
        """, serverName);
    }

    @Override
    public CompletableFuture<Void> setServerNameAsync(String serverName) {
        return db.updateAsync("""
            UPDATE server_settings
            SET server_name = ?
        """, serverName);
    }

    @Override
    public String getServerName() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT server_name FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("server_name");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getServerNameAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT server_name FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("server_name");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setServerVersion(String serverVersion) {
        db.update("""
            UPDATE server_settings
            SET server_version = ?
        """, serverVersion);
    }

    @Override
    public CompletableFuture<Void> setServerVersionAsync(String serverVersion) {
        return db.updateAsync("""
            UPDATE server_settings
            SET server_version = ?
        """, serverVersion);
    }

    @Override
    public String getServerVersion() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT server_version FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("server_version");
                    else return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getServerVersionAsync() {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT server_version FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("server_version");
                    else return null;
                }
            }
        });
    }

    public int countRowsInTable() {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS row_count FROM server_settings WHERE id = 1");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("row_count");
            }
            return 0;
        });
    }
}