package de.lars.apimanager.apis.serverSettingsAPI;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IServerSettingsAPI {
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

    void enableMaintenance(String reason, Instant start, Instant endTime, Instant maxEndTime);

    CompletableFuture<Void> enableMaintenanceAsync(String reason, Instant start, Instant endTime, Instant maxEndTime);

    void disableMaintenance();

    CompletableFuture<Void> disableMaintenanceAsync();

    boolean isMaintenanceEnabled();

    CompletableFuture<Boolean> isMaintenanceEnabledAsync();

    void setMaintenanceReason(String reason);

    CompletableFuture<Void> setMaintenanceReasonAsync(String reason);

    String getMaintenanceReason();

    CompletableFuture<String> getMaintenanceReasonAsync();

    void setMaintenanceStart(Instant startTime);

    CompletableFuture<Void> setMaintenanceStartAsync(Instant startTime);

    Instant getMaintenanceStart();

    CompletableFuture<Instant> getMaintenanceStartAsync();

    void setMaintenanceEnd(Instant endTime);

    CompletableFuture<Void> setMaintenanceEndAsync(Instant endTime);

    Instant getMaintenanceEnd();

    CompletableFuture<Instant> getMaintenanceEndAsync();

    void setMaintenanceMaxEnd(Instant maxEndTIme);

    CompletableFuture<Void> setMaintenanceMaxEndAsync(Instant maxEndTIme);

    Instant getMaintenanceMaxEnd();

    CompletableFuture<Instant> getMaintenanceMaxEndAsync();

    void setMaxPlayers(int maxPlayers);

    CompletableFuture<Void> setMaxPlayersAsync(int maxPlayers);

    Integer getMaxPlayers();

    CompletableFuture<Integer> getMaxPlayersAsync();

    void setServerName(String serverName);

    CompletableFuture<Void> setServerNameAsync(String serverName);

    String getServerName();

    CompletableFuture<String> getServerNameAsync();

    void setServerVersion(String serverVersion);

    CompletableFuture<Void> setServerVersionAsync(String serverVersion);

    String getServerVersion();

    CompletableFuture<String> getServerVersionAsync();
}