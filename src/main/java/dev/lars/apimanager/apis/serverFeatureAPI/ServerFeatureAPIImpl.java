package dev.lars.apimanager.apis.serverFeatureAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ServerFeatureAPIImpl implements IServerFeatureAPI {
    private static final String TABLE = "server_feature";
    private static final String WHERE_ID = "id = 1";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS server_feature (
                id INT PRIMARY KEY CHECK (id = 1),
                is_real_time_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                is_real_weather_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);

        if (repo().count(TABLE, WHERE_ID) < 1) {
            db().update("""
                INSERT INTO server_feature
                (id, is_real_time_enabled, is_real_weather_enabled)
                VALUES (1, ?, ?)
            """, false, false);
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
}