package de.lars.apimanager.apis.serverSettingsAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.FormatLocation;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ServerSettingsAPIImpl implements IServerSettingsAPI {
    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS server_settings (
                id INT PRIMARY KEY CHECK (id = 1),
                is_server_online BOOLEAN DEFAULT FALSE,
                real_time_enabled BOOLEAN DEFAULT FALSE,
                real_weather_enabled BOOLEAN DEFAULT FALSE,
                maintenance_enabled BOOLEAN DEFAULT FALSE,
                maintenance_reason VARCHAR(255) DEFAULT '',
                maintenance_start TIMESTAMP DEFAULT NULL,
                maintenance_end TIMESTAMP DEFAULT NULL,
                maintenance_max_end TIMESTAMP DEFAULT NULL,
                max_players INT DEFAULT 100,
                server_name VARCHAR(255) DEFAULT 'A Minecraft Server',
                server_version VARCHAR(50) DEFAULT '1.21.10',
                spawn_location VARCHAR(255) DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);

        if (countRowsInTable() < 1) {
            db().update("""
                INSERT INTO server_settings
                (id, is_server_online, real_time_enabled, real_weather_enabled, maintenance_enabled, maintenance_reason, maintenance_end, max_players, server_name, server_version, spawn_location)
                VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, false, false, false, false, "", null, 1000000000, "A Minecraft Server", "1.21.10", null);
        }
    }

    @Override
    public Instant getCreatedAt() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM server_settings WHERE id = 1"
                 );
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("created_at").toInstant();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM server_settings WHERE id = 1"
                 );
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("created_at").toInstant();
                return null;
            }
        });
    }

    @Override
    public Instant getUpdatedAt() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM server_settings WHERE id = 1"
                 );
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM server_settings WHERE id = 1"
                 );
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                return null;
            }
        });
    }

    @Override
    public void setServerOnline(boolean online) {
        db().update("""
            UPDATE server_settings
            SET is_server_online = ?
            WHERE id = 1
        """, online);
    }

    @Override
    public CompletableFuture<Void> setServerOnlineAsync(boolean online) {
        return db().updateAsync("""
            UPDATE server_settings
            SET is_server_online = ?
            WHERE id = 1
        """, online);
    }

    @Override
    public boolean isServerOnline() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT is_server_online FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("is_server_online");
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isServerOnlineAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT is_server_online FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("is_server_online");
                }
            }
        });
    }

    @Override
    public void setRealTimeEnabled(boolean enabled) {
        db().update("""
            UPDATE server_settings
            SET real_time_enabled = ?
            WHERE id = 1
        """, enabled);
    }

    @Override
    public CompletableFuture<Void> setRealTimeEnabledAsync(boolean enabled) {
        return db().updateAsync("""
            UPDATE server_settings
            SET real_time_enabled = ?
            WHERE id = 1
        """, enabled);
    }

    @Override
    public boolean isRealTimeEnabled() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT real_time_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("real_time_enabled");
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isRealTimeEnabledAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT real_time_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("real_time_enabled");
                }
            }
        });
    }

    @Override
    public void setRealWeatherEnabled(boolean enabled) {
        db().update("""
            UPDATE server_settings
            SET real_weather_enabled = ?
            WHERE id = 1
        """, enabled);
    }

    @Override
    public CompletableFuture<Void> setRealWeatherEnabledAsync(boolean enabled) {
        return db().updateAsync("""
            UPDATE server_settings
            SET real_weather_enabled = ?
            WHERE id = 1
        """, enabled);
    }

    @Override
    public boolean isRealWeatherEnabled() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT real_weather_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("real_weather_enabled");
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isRealWeatherEnabledAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT real_weather_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("real_weather_enabled");
                }
            }
        });
    }

    @Override
    public void enableMaintenance(String reason, Instant start, Instant endTime, Instant maxEndTime) {
        db().update("""
            UPDATE server_settings
            SET maintenance_enabled = ?,
                maintenance_reason = ?,
                maintenance_start = ?,
                maintenance_end = ?,
                maintenance_max_end = ?
            WHERE id = 1
        """, true, reason, Timestamp.from(start), Timestamp.from(endTime), Timestamp.from(maxEndTime));
    }

    @Override
    public CompletableFuture<Void> enableMaintenanceAsync(String reason, Instant start, Instant endTime, Instant maxEndTime) {
        return db().updateAsync("""
            UPDATE server_settings
            SET maintenance_enabled = ?,
                maintenance_reason = ?,
                maintenance_start = ?,
                maintenance_end = ?,
                maintenance_max_end = ?
            WHERE id = 1
        """, true, reason, Timestamp.from(start), Timestamp.from(endTime), Timestamp.from(maxEndTime));
    }

    @Override
    public void disableMaintenance() {
        db().update("""
            UPDATE server_settings
            SET maintenance_enabled = ?,
                maintenance_reason = ?,
                maintenance_start = ?,
                maintenance_end = ?,
                maintenance_max_end = ?
            WHERE id = 1
        """, false, "", null, null, null);
    }

    @Override
    public CompletableFuture<Void> disableMaintenanceAsync() {
        return db().updateAsync("""
            UPDATE server_settings
            SET maintenance_enabled = ?,
                maintenance_reason = ?,
                maintenance_start = ?,
                maintenance_end = ?,
                maintenance_max_end = ?
            WHERE id = 1
        """, false, "", null, null, null);
    }

    @Override
    public boolean isMaintenanceEnabled() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("maintenance_enabled");
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isMaintenanceEnabledAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_enabled FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean("maintenance_enabled");
                }
            }
        });
    }

    @Override
    public void setMaintenanceReason(String reason) {
        ValidateParameter.validateReason(reason);
        db().update("""
            UPDATE server_settings
            SET maintenance_reason = ?
            WHERE id = 1
        """, reason);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceReasonAsync(String reason) {
        ValidateParameter.validateReason(reason);
        return db().updateAsync("""
            UPDATE server_settings
            SET maintenance_reason = ?
            WHERE id = 1
        """, reason);
    }

    @Override
    public String getMaintenanceReason() {
        return db().query(conn -> {
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
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT maintenance_reason FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("maintenance_reason");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setMaintenanceStart(Instant startTime) {
        ValidateParameter.validateInstant(startTime);
        db().update("""
            UPDATE server_settings
            SET maintenance_start = ?
            WHERE id = 1
        """, Timestamp.from(startTime));
    }

    @Override
    public CompletableFuture<Void> setMaintenanceStartAsync(Instant startTime) {
        ValidateParameter.validateInstant(startTime);
        return db().updateAsync("""
            UPDATE server_settings
            SET maintenance_start = ?
            WHERE id = 1
        """, Timestamp.from(startTime));
    }

    @Override
    public Instant getMaintenanceStart() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT maintenance_start FROM server_settings WHERE id = 1"
            );
            ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("maintenance_start");
                    return ts != null ? ts.toInstant() : null;
                }
                return null;
            }
        });
    }


    @Override
    public CompletableFuture<Instant> getMaintenanceStartAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT maintenance_start FROM server_settings WHERE id = 1"
            );
            ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("maintenance_start");
                    return ts != null ? ts.toInstant() : null;
                }
                return null;
            }
        });
    }

    @Override
    public void setMaintenanceEnd(Instant endTime) {
        ValidateParameter.validateInstant(endTime);
        db().update("""
            UPDATE server_settings
            SET maintenance_end = ?
            WHERE id = 1
        """, Timestamp.from(endTime));
    }

    @Override
    public CompletableFuture<Void> setMaintenanceEndAsync(Instant endTime) {
        ValidateParameter.validateInstant(endTime);
        return db().updateAsync("""
            UPDATE server_settings
            SET maintenance_end = ?
            WHERE id = 1
        """, Timestamp.from(endTime));
    }

    @Override
    public Instant getMaintenanceEnd() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT maintenance_end FROM server_settings WHERE id = 1"
            );
            ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("maintenance_end");
                    return ts != null ? ts.toInstant() : null;
                }
                return null;
            }
        });
    }


    @Override
    public CompletableFuture<Instant> getMaintenanceEndAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT maintenance_end FROM server_settings WHERE id = 1"
            );
            ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("maintenance_end");
                    return ts != null ? ts.toInstant() : null;
                }
                return null;
            }
        });
    }

    @Override
    public void setMaintenanceMaxEnd(Instant maxEndTime) {
        ValidateParameter.validateInstant(maxEndTime);
        db().update("""
            UPDATE server_settings
            SET maintenance_max_end = ?
            WHERE id = 1
        """, Timestamp.from(maxEndTime));
    }

    @Override
    public CompletableFuture<Void> setMaintenanceMaxEndAsync(Instant maxEndTime) {
        ValidateParameter.validateInstant(maxEndTime);
        return db().updateAsync("""
            UPDATE server_settings
            SET maintenance_max_end = ?
            WHERE id = 1
        """, Timestamp.from(maxEndTime));
    }

    @Override
    public Instant getMaintenanceMaxEnd() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT maintenance_max_end FROM server_settings WHERE id = 1"
            );
            ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("maintenance_max_end");
                    return ts != null ? ts.toInstant() : null;
                }
                return null;
            }
        });
    }


    @Override
    public CompletableFuture<Instant> getMaintenanceMaxEndAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT maintenance_max_end FROM server_settings WHERE id = 1"
            );
            ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("maintenance_max_end");
                    return ts != null ? ts.toInstant() : null;
                }
                return null;
            }
        });
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        db().update("""
            UPDATE server_settings
            SET max_players = ?
            WHERE id = 1
        """, maxPlayers);
    }

    @Override
    public CompletableFuture<Void> setMaxPlayersAsync(int maxPlayers) {
        return db().updateAsync("""
            UPDATE server_settings
            SET max_players = ?
            WHERE id = 1
        """, maxPlayers);
    }

    @Override
    public Integer getMaxPlayers() {
        return db().query(conn -> {
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
        return db().queryAsync(conn -> {
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
        ValidateParameter.validateServerName(serverName);
        db().update("""
            UPDATE server_settings
            SET server_name = ?
            WHERE id = 1
        """, serverName);
    }

    @Override
    public CompletableFuture<Void> setServerNameAsync(String serverName) {
        ValidateParameter.validateServerName(serverName);
        return db().updateAsync("""
            UPDATE server_settings
            SET server_name = ?
            WHERE id = 1
        """, serverName);
    }

    @Override
    public String getServerName() {
        return db().query(conn -> {
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
        return db().queryAsync(conn -> {
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
       ValidateParameter.validateServerVersion(serverVersion);
        db().update("""
            UPDATE server_settings
            SET server_version = ?
            WHERE id = 1
        """, serverVersion);
    }

    @Override
    public CompletableFuture<Void> setServerVersionAsync(String serverVersion) {
        ValidateParameter.validateServerVersion(serverVersion);
        return db().updateAsync("""
            UPDATE server_settings
            SET server_version = ?
            WHERE id = 1
        """, serverVersion);
    }

    @Override
    public String getServerVersion() {
        return db().query(conn -> {
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
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT server_version FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("server_version");
                    else return null;
                }
            }
        });
    }

    @Override
    public void setSpawnLocation(Location location) {
        ValidateParameter.validateLocation(location);
        db().update("UPDATE server_settings SET spawn_location = ? WHERE id = 1", FormatLocation.serializeLocation(location));
    }

    @Override
    public CompletableFuture<Void> setSpawnLocationAsync(Location location) {
        ValidateParameter.validateLocation(location);
        return db().updateAsync("UPDATE server_settings SET spawn_location = ? WHERE id = 1", FormatLocation.serializeLocation(location));
    }

    @Override
    public Location getSpawnLocation() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT spawn_location FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String locData = rs.getString("spawn_location");
                        return FormatLocation.deserializeLocation(locData);
                    }
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Location> getSpawnLocationAsync() {
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT spawn_location FROM server_settings WHERE id = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String locData = rs.getString("spawn_location");
                        return FormatLocation.deserializeLocation(locData);
                    }
                }
            }
            return null;
        });
    }

    public int countRowsInTable() {
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS row_count FROM server_settings WHERE id = 1");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("row_count");
            }
            return 0;
        });
    }
}