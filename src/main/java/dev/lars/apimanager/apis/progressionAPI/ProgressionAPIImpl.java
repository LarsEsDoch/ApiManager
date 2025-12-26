package dev.lars.apimanager.apis.progressionAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ProgressionAPIImpl implements IProgressionAPI {
    private static final String TABLE = "server_progression";
    private static final String WHERE_ID = "id = 1";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS server_progression (
                id INT PRIMARY KEY CHECK (id = 1),
                nether_unlock_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                end_unlock_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);

        if (repo().count(TABLE, WHERE_ID) < 1) {
            db().update("""
                INSERT INTO server_progression
                (id, nether_unlock_at, end_unlock_at)
                VALUES (1, ?, ?)
            """, Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));
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
    public void setNetherUnlockAt(Instant unlockAt) {
        ApiManagerValidateParameter.validateInstant(unlockAt);
        repo().updateColumn(TABLE, "nether_unlock_at", unlockAt, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setNetherUnlockAtAsync(Instant unlockAt) {
        ApiManagerValidateParameter.validateInstant(unlockAt);
        return repo().updateColumnAsync(TABLE, "nether_unlock_at", unlockAt, WHERE_ID);
    }

    @Override
    public Instant getNetherUnlockAt() {
        return repo().getInstant(TABLE, "nether_unlock_at", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getNetherUnlockAtAsync() {
        return repo().getInstantAsync(TABLE, "nether_unlock_at", WHERE_ID);
    }

    @Override
    public void setEndUnlockAt(Instant unlockAt) {
        ApiManagerValidateParameter.validateInstant(unlockAt);
        repo().updateColumn(TABLE, "end_unlock_at", unlockAt, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setEndUnlockAtAsync(Instant unlockAt) {
        ApiManagerValidateParameter.validateInstant(unlockAt);
        return repo().updateColumnAsync(TABLE, "end_unlock_at", unlockAt, WHERE_ID);
    }

    @Override
    public Instant getEndUnlockAt() {
        return repo().getInstant(TABLE, "end_unlock_at", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getEndUnlockAtAsync() {
        return repo().getInstantAsync(TABLE, "end_unlock_at", WHERE_ID);
    }
}