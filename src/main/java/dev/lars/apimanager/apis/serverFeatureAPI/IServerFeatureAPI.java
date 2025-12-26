package dev.lars.apimanager.apis.serverFeatureAPI;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IServerFeatureAPI {
    Instant getCreatedAt();

    CompletableFuture<Instant> getCreatedAtAsync();

    Instant getUpdatedAt();

    CompletableFuture<Instant> getUpdatedAtAsync();

    void setRealTimeEnabled(boolean enabled);

    CompletableFuture<Void> setRealTimeEnabledAsync(boolean enabled);

    boolean isRealTimeEnabled();

    CompletableFuture<Boolean> isRealTimeEnabledAsync();

    void setRealWeatherEnabled(boolean enabled);

    CompletableFuture<Void> setRealWeatherEnabledAsync(boolean enabled);

    boolean isRealWeatherEnabled();

    CompletableFuture<Boolean> isRealWeatherEnabledAsync();
}