package dev.lars.apimanager.apis.serverFeatureAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ServerFeatureAPIImpl implements IServerFeatureAPI {
    private static final String TABLE = "server_feature";

    private String serverId() {
        return ApiManager.getServerId();
    }

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update(String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                server_id VARCHAR(64) NOT NULL PRIMARY KEY,
                is_real_time_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                is_real_weather_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, TABLE));

        repo().insertIgnore(TABLE, new String[]{"server_id"}, serverId());
    }

    @Override
    public Instant getCreatedAt() {
        return repo().getInstant(TABLE, "created_at", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync() {
        return repo().getInstantAsync(TABLE, "created_at", "server_id = ?", serverId());
    }

    @Override
    public Instant getUpdatedAt() {
        return repo().getInstant(TABLE, "updated_at", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync() {
        return repo().getInstantAsync(TABLE, "updated_at", "server_id = ?", serverId());
    }

    @Override
    public void setRealTimeEnabled(boolean enabled) {
        repo().updateColumn(TABLE, "is_real_time_enabled", enabled, "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Void> setRealTimeEnabledAsync(boolean enabled) {
        return repo().updateColumnAsync(TABLE, "is_real_time_enabled", enabled, "server_id = ?", serverId());
    }

    @Override
    public boolean isRealTimeEnabled() {
        Boolean result = repo().getBoolean(TABLE, "is_real_time_enabled", "server_id = ?", serverId());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isRealTimeEnabledAsync() {
        return repo().getBooleanAsync(TABLE, "is_real_time_enabled", "server_id = ?", serverId())
            .thenApply(result -> result != null && result);
    }

    @Override
    public void setRealWeatherEnabled(boolean enabled) {
        repo().updateColumn(TABLE, "is_real_weather_enabled", enabled, "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Void> setRealWeatherEnabledAsync(boolean enabled) {
        return repo().updateColumnAsync(TABLE, "is_real_weather_enabled", enabled, "server_id = ?", serverId());
    }

    @Override
    public boolean isRealWeatherEnabled() {
        Boolean result = repo().getBoolean(TABLE, "is_real_weather_enabled", "server_id = ?", serverId());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isRealWeatherEnabledAsync() {
        return repo().getBooleanAsync(TABLE, "is_real_weather_enabled", "server_id = ?", serverId())
            .thenApply(result -> result != null && result);
    }
}