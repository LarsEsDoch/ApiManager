package dev.lars.apimanager.apis.progressionAPI;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IProgressionAPI {
    Instant getCreatedAt();

    CompletableFuture<Instant> getCreatedAtAsync();

    Instant getUpdatedAt();

    CompletableFuture<Instant> getUpdatedAtAsync();

    void setNetherUnlockAt(Instant unlockAt);

    CompletableFuture<Void> setNetherUnlockAtAsync(Instant unlockAt);

    Instant getNetherUnlockAt();

    CompletableFuture<Instant> getNetherUnlockAtAsync();

    void setEndUnlockAt(Instant unlockAt);

    CompletableFuture<Void> setEndUnlockAtAsync(Instant unlockAt);

    Instant getEndUnlockAt();

    CompletableFuture<Instant> getEndUnlockAtAsync();
}