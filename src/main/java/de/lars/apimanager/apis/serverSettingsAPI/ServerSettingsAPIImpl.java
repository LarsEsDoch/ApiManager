package de.lars.apimanager.apis.serverSettingsAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.FormatLocation;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.Location;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ServerSettingsAPIImpl implements IServerSettingsAPI {
    private static final String TABLE = "server_settings";
    private static final String WHERE_ID = "id = 1";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS server_settings (
                id INT PRIMARY KEY CHECK (id = 1),
                is_server_online BOOLEAN DEFAULT FALSE,
                is_real_time_enabled BOOLEAN DEFAULT FALSE,
                is_real_weather_enabled BOOLEAN DEFAULT FALSE,
                is_maintenance_enabled BOOLEAN DEFAULT FALSE,
                maintenance_reason VARCHAR(255) DEFAULT NULL,
                maintenance_start TIMESTAMP DEFAULT NULL,
                maintenance_end TIMESTAMP DEFAULT NULL,
                maintenance_max_end TIMESTAMP DEFAULT NULL,
                max_players INT DEFAULT 10,
                server_name VARCHAR(255) DEFAULT 'A Minecraft Server',
                server_version VARCHAR(50) DEFAULT '1.21.10',
                spawn_location VARCHAR(255) DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);

        if (repo().count(TABLE, WHERE_ID) < 1) {
            db().update("""
                INSERT INTO server_settings
                (id, is_server_online, is_real_time_enabled, is_real_weather_enabled, is_maintenance_enabled, maintenance_reason, maintenance_end, max_players, server_name, server_version, spawn_location)
                VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, false, false, false, false, null, null, 10, "A Minecraft Server", "1.21.10", null);
        }
    }

    @Override
    public Instant getCreatedAt() {
        return repo().getInstant(TABLE, "created_at", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync() {
        return repo().getInstantAsync(TABLE, "created_at", WHERE_ID);
    }

    @Override
    public Instant getUpdatedAt() {
        return repo().getInstant(TABLE, "updated_at", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync() {
        return repo().getInstantAsync(TABLE, "updated_at", WHERE_ID);
    }

    @Override
    public void setServerOnline(boolean online) {
        repo().updateColumn(TABLE, "is_server_online", online, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setServerOnlineAsync(boolean online) {
        return repo().updateColumnAsync(TABLE, "is_server_online", online, WHERE_ID);
    }

    @Override
    public boolean isServerOnline() {
        Boolean result = repo().getBoolean(TABLE, "is_server_online", WHERE_ID);
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isServerOnlineAsync() {
        return repo().getBooleanAsync(TABLE, "is_server_online", WHERE_ID)
            .thenApply(result -> result != null && result);
    }

    @Override
    public void setRealTimeEnabled(boolean enabled) {
        repo().updateColumn(TABLE, "is_real_time_enabled", enabled, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setRealTimeEnabledAsync(boolean enabled) {
        return repo().updateColumnAsync(TABLE, "is_real_time_enabled", enabled, WHERE_ID);
    }

    @Override
    public boolean isRealTimeEnabled() {
        Boolean result = repo().getBoolean(TABLE, "is_real_time_enabled", WHERE_ID);
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isRealTimeEnabledAsync() {
        return repo().getBooleanAsync(TABLE, "is_real_time_enabled", WHERE_ID)
            .thenApply(result -> result != null && result);
    }

    @Override
    public void setRealWeatherEnabled(boolean enabled) {
        repo().updateColumn(TABLE, "is_real_weather_enabled", enabled, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setRealWeatherEnabledAsync(boolean enabled) {
        return repo().updateColumnAsync(TABLE, "is_real_weather_enabled", enabled, WHERE_ID);
    }

    @Override
    public boolean isRealWeatherEnabled() {
        Boolean result = repo().getBoolean(TABLE, "is_real_weather_enabled", WHERE_ID);
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isRealWeatherEnabledAsync() {
        return repo().getBooleanAsync(TABLE, "is_real_weather_enabled", WHERE_ID)
            .thenApply(result -> result != null && result);
    }

    @Override
    public void enableMaintenance(String reason, Instant start, Instant endTime, Instant maxEndTime) {
        repo().updateColumns(TABLE,
            new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_end", "maintenance_max_end"},
            new Object[]{true, reason, start, endTime, maxEndTime},
            WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> enableMaintenanceAsync(String reason, Instant start, Instant endTime, Instant maxEndTime) {
        return repo().updateColumnsAsync(TABLE,
            new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_end", "maintenance_max_end"},
            new Object[]{true, reason, start, endTime, maxEndTime},
            WHERE_ID);
    }

    @Override
    public void disableMaintenance() {
        repo().updateColumns(TABLE,
            new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_end", "maintenance_max_end"},
            new Object[]{false, "", null, null, null},
            WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> disableMaintenanceAsync() {
        return repo().updateColumnsAsync(TABLE,
            new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_end", "maintenance_max_end"},
            new Object[]{false, "", null, null, null},
            WHERE_ID);
    }

    @Override
    public boolean isMaintenanceEnabled() {
        Boolean result = repo().getBoolean(TABLE, "is_maintenance_enabled", WHERE_ID);
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isMaintenanceEnabledAsync() {
        return repo().getBooleanAsync(TABLE, "is_maintenance_enabled", WHERE_ID)
            .thenApply(result -> result != null && result);
    }

    @Override
    public void setMaintenanceReason(String reason) {
        ValidateParameter.validateReason(reason);
        repo().updateColumn(TABLE, "maintenance_reason", reason, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceReasonAsync(String reason) {
        ValidateParameter.validateReason(reason);
        return repo().updateColumnAsync(TABLE, "maintenance_reason", reason, WHERE_ID);
    }

    @Override
    public String getMaintenanceReason() {
        return repo().getString(TABLE, "maintenance_reason", WHERE_ID);
    }

    @Override
    public CompletableFuture<String> getMaintenanceReasonAsync() {
        return repo().getStringAsync(TABLE, "maintenance_reason", WHERE_ID);
    }

    @Override
    public void setMaintenanceStart(Instant startTime) {
        ValidateParameter.validateInstant(startTime);
        repo().updateColumn(TABLE, "maintenance_start", startTime, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceStartAsync(Instant startTime) {
        ValidateParameter.validateInstant(startTime);
        return repo().updateColumnAsync(TABLE, "maintenance_start", startTime, WHERE_ID);
    }

    @Override
    public Instant getMaintenanceStart() {
        return repo().getInstant(TABLE, "maintenance_start", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceStartAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_start", WHERE_ID);
    }

    @Override
    public void setMaintenanceEnd(Instant endTime) {
        ValidateParameter.validateInstant(endTime);
        repo().updateColumn(TABLE, "maintenance_end", endTime, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceEndAsync(Instant endTime) {
        ValidateParameter.validateInstant(endTime);
        return repo().updateColumnAsync(TABLE, "maintenance_end", endTime, WHERE_ID);
    }

    @Override
    public Instant getMaintenanceEnd() {
        return repo().getInstant(TABLE, "maintenance_end", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceEndAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_end", WHERE_ID);
    }

    @Override
    public void setMaintenanceMaxEnd(Instant maxEndTime) {
        ValidateParameter.validateInstant(maxEndTime);
        repo().updateColumn(TABLE, "maintenance_max_end", maxEndTime, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceMaxEndAsync(Instant maxEndTime) {
        ValidateParameter.validateInstant(maxEndTime);
        return repo().updateColumnAsync(TABLE, "maintenance_max_end", maxEndTime, WHERE_ID);
    }

    @Override
    public Instant getMaintenanceMaxEnd() {
        return repo().getInstant(TABLE, "maintenance_max_end", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceMaxEndAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_max_end", WHERE_ID);
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        repo().updateColumn(TABLE, "max_players", maxPlayers, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaxPlayersAsync(int maxPlayers) {
        return repo().updateColumnAsync(TABLE, "max_players", maxPlayers, WHERE_ID);
    }

    @Override
    public Integer getMaxPlayers() {
        return repo().getInteger(TABLE, "max_players", WHERE_ID);
    }

    @Override
    public CompletableFuture<Integer> getMaxPlayersAsync() {
        return repo().getIntegerAsync(TABLE, "max_players", WHERE_ID);
    }

    @Override
    public void setServerName(String serverName) {
        ValidateParameter.validateServerName(serverName);
        repo().updateColumn(TABLE, "server_name", serverName, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setServerNameAsync(String serverName) {
        ValidateParameter.validateServerName(serverName);
        return repo().updateColumnAsync(TABLE, "server_name", serverName, WHERE_ID);
    }

    @Override
    public String getServerName() {
        return repo().getString(TABLE, "server_name", WHERE_ID);
    }

    @Override
    public CompletableFuture<String> getServerNameAsync() {
        return repo().getStringAsync(TABLE, "server_name", WHERE_ID);
    }

    @Override
    public void setServerVersion(String serverVersion) {
        ValidateParameter.validateServerVersion(serverVersion);
        repo().updateColumn(TABLE, "server_version", serverVersion, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setServerVersionAsync(String serverVersion) {
        ValidateParameter.validateServerVersion(serverVersion);
        return repo().updateColumnAsync(TABLE, "server_version", serverVersion, WHERE_ID);
    }

    @Override
    public String getServerVersion() {
        return repo().getString(TABLE, "server_version", WHERE_ID);
    }

    @Override
    public CompletableFuture<String> getServerVersionAsync() {
        return repo().getStringAsync(TABLE, "server_version", WHERE_ID);
    }

    @Override
    public void setSpawnLocation(Location location) {
        ValidateParameter.validateLocation(location);
        repo().updateColumn(TABLE, "spawn_location", FormatLocation.serializeLocation(location), WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setSpawnLocationAsync(Location location) {
        ValidateParameter.validateLocation(location);
        return repo().updateColumnAsync(TABLE, "spawn_location", FormatLocation.serializeLocation(location), WHERE_ID);
    }

    @Override
    public Location getSpawnLocation() {
        String locData = repo().getString(TABLE, "spawn_location", WHERE_ID);
        return FormatLocation.deserializeLocation(locData);
    }

    @Override
    public CompletableFuture<Location> getSpawnLocationAsync() {
        return repo().getStringAsync(TABLE, "spawn_location", WHERE_ID)
            .thenApply(FormatLocation::deserializeLocation);
    }
}