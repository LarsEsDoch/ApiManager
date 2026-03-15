package dev.lars.apimanager.apis.progressionAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ProgressionAPIImpl implements IProgressionAPI {
    private static final String TABLE = "server_progression";

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
                nether_unlock_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                end_unlock_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
    public void setNetherUnlockAt(Instant unlockAt) {
        ApiManagerValidateParameter.validateInstant(unlockAt);
        repo().updateColumn(TABLE, "nether_unlock_at", unlockAt, "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Void> setNetherUnlockAtAsync(Instant unlockAt) {
        ApiManagerValidateParameter.validateInstant(unlockAt);
        return repo().updateColumnAsync(TABLE, "nether_unlock_at", unlockAt, "server_id = ?", serverId());
    }

    @Override
    public Instant getNetherUnlockAt() {
        return repo().getInstant(TABLE, "nether_unlock_at", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getNetherUnlockAtAsync() {
        return repo().getInstantAsync(TABLE, "nether_unlock_at", "server_id = ?", serverId());
    }

    @Override
    public void setEndUnlockAt(Instant unlockAt) {
        ApiManagerValidateParameter.validateInstant(unlockAt);
        repo().updateColumn(TABLE, "end_unlock_at", unlockAt, "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Void> setEndUnlockAtAsync(Instant unlockAt) {
        ApiManagerValidateParameter.validateInstant(unlockAt);
        return repo().updateColumnAsync(TABLE, "end_unlock_at", unlockAt, "server_id = ?", serverId());
    }

    @Override
    public Instant getEndUnlockAt() {
        return repo().getInstant(TABLE, "end_unlock_at", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getEndUnlockAtAsync() {
        return repo().getInstantAsync(TABLE, "end_unlock_at", "server_id = ?", serverId());
    }
}